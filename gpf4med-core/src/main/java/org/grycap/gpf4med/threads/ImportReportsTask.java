package org.grycap.gpf4med.threads;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.partition;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Range.closed;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.Futures.successfulAsList;
import static com.google.common.util.concurrent.ListenableFutureTask.create;
import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.moveFile;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToNull;
import static org.grycap.gpf4med.concurrent.TaskRunner.TASK_RUNNER;
import static org.grycap.gpf4med.concurrent.TaskStorage.TASK_STORAGE;
import static org.grycap.gpf4med.threads.FecthClient.fetchReports;
import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.grycap.gpf4med.concurrent.CancellableTask;
import org.grycap.gpf4med.model.document.Document;
import org.slf4j.Logger;

import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

public class ImportReportsTask extends CancellableTask<Integer> {

	private static final Logger LOGGER = getLogger(ImportReportsTask.class);

	public static final long TIMEOUT_MINUTES = 60l;
	public static final int MAX_RECORDS_FETCHED = 10000;

	private final int partition;
	private final ImmutableList<String> ids;
	private final File destDir;
	
	private final BackEnd backend;
	private final String centerName;
	private final String credentials;

	private AtomicInteger pending = new AtomicInteger(0);
	private AtomicInteger fetched = new AtomicInteger(0);

	
	public ImportReportsTask(final BackEnd backend, final String centerName, final String credentials,
			final List<String> ids, final int partition, final File destDir) {
		this(null, backend, centerName, credentials, ids, partition, destDir);
	}
	
	public ImportReportsTask(final UUID puuid, final BackEnd backend, final String centerName, final String credentials, 
			final List<String> ids, final int partition, final File destDir) {
		super(puuid);
		this.backend = (backend != null) ? backend : null;
		checkNotNull(backend, "Uninitalized or invalid BackEnd");
		String centerName2 = null;
		checkArgument(isNotBlank(centerName2 = trimToNull(centerName)), "Uninitialized or invalid center name");
		this.centerName = centerName2;
		String credentials2 = null;
		checkArgument(isNotBlank(credentials2 = trimToNull(credentials)), "Uninitialized or invalid credentials");
		this.credentials = credentials2;
		this.partition = closed(1, MAX_RECORDS_FETCHED).contains(partition) ? partition : MAX_RECORDS_FETCHED;
		this.ids = ids != null ? copyOf(ids) : new ImmutableList.Builder<String>().build();
		this.task = create(importReportsTask());
		this.destDir = checkNotNull(destDir, "Uninitialized or invalid destination directory");
		checkState((this.destDir.isDirectory() && this.destDir.canWrite()) || this.destDir.mkdirs(), "Destination directory is not writable");
	}

	private Callable<Integer> importReportsTask() {
		return new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				LOGGER.info("Importing new reports");
				int count = 0;
				final File tmpDir = createTmpDir();
				try {
					final List<ListenableFuture<Integer>> subTasks = newArrayList(importSubTasks(tmpDir));
					final ListenableFuture<List<Integer>> globalTask = successfulAsList(subTasks);					
					final List<Integer> results = globalTask.get(TIMEOUT_MINUTES, MINUTES);
					for (final Integer result : results) {
						if (result != null) {
							count += result;
						} else {
							setHasErrors(true);
							setStatus("Error while importing reports: not all reports were imported");
							LOGGER.error("Error while importing reports: not all reports were imported");
						}
					}
				} catch (InterruptedException ie) {					
					// ignore and propagate
					LOGGER.warn("Report import was interrupted, exiting");
					throw ie;
				} catch (Exception e) {
					setHasErrors(true);
					setStatus("Uncaught error while importing reports: not all reports were imported");
					LOGGER.error("Uncaught error while importing reports", e);
				} finally {
					deleteQuietly(tmpDir);
				}
				final String msg = count + " new reports were imported";				
				if (!hasErrors()) {
					setStatus(msg);
					LOGGER.info(msg);
				} else {
					LOGGER.warn(msg + " - errors reported");
				}				
				// unregister this task before returning the result to the execution service
				TASK_STORAGE.remove(getUuid());
				return new Integer(count);
			}
		};
	}

	private List<ListenableFuture<Integer>> importSubTasks(final File tmpDir) {
		final List<ListenableFuture<Integer>> subTasks = newArrayList();
		final Set<String> deduplicated = newHashSet(ids);
		final Iterable<List<String>> subsets = partition(deduplicated, partition);
		for (final List<String> subset : subsets) {
			subTasks.add(TASK_RUNNER.submit(importSubTask(subset, tmpDir, "xml")));
			LOGGER.trace("Partition produced " + subset.size() + " new records");
		}
		return subTasks;
	}

	private Callable<Integer> importSubTask(final List<String> subset, final File tmpDir, final String extension) {
		return new Callable<Integer>() {
			private int efetchCount = 0;
			@Override
			public Integer call() throws Exception {
				if (subset.size() > 0) {
					setStatus("Fetching reports");
					// update progress
					int pendingCount = pending.addAndGet(subset.size());
					setProgress(100.0d * fetched.get() / pendingCount);
					// fetch report files					
					final Path tmpDir2 = createTempDirectory(tmpDir.toPath(), "fetch_report_task_");
					fetchReports(backend, centerName, credentials, subset, 0, partition, tmpDir2.toFile());
					// import report files to the local collection		
					for (final String id : subset) {
						setStatus("Importing reports into local collection");
						final Path source = tmpDir2.resolve(id + "." + extension);
						try {							
							// move files to their final destination
							final Document report = REPORT_XMLB.typeFromFile(source.toFile());
							final String globalId = report.getIDTRENCADISReport();
							final String ontology = report.getIDOntology();
							final String centername = centerName.replaceAll(" ", "_");
							checkState(isNotBlank(globalId), "Global identifier not found");
							checkState(isNotBlank(ontology), "Ontology not found");
							final File destSubdir = new File(destDir, centername + File.separator
														     + "ontology_" + ontology);
							destSubdir.mkdirs();
							moveFile(source.toFile(), new File(destSubdir, globalId + "." + extension));							
							efetchCount++;
							LOGGER.info("New report file stored: " + source.toString());
							// update progress
							int fetchedCount = fetched.incrementAndGet();
							setProgress(100.0d * fetchedCount / pending.get());							
						} catch (Exception e) {
							LOGGER.warn("Failed to import reports from file: " + source.getFileName(), e);
						}
					}
				}
				checkState(subset.size() == efetchCount, "No all reports were imported");
				return efetchCount;
			}
		};
	}

	private static File createTmpDir() {
		File tmpDir = null;
		try {
			tmpDir = createTempDirectory("tmp_report_").toFile();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create temporary directory", e);
		}
		return tmpDir;
	}

}