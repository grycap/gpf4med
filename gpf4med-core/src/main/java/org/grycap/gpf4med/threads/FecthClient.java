package org.grycap.gpf4med.threads;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.Futures.addCallback;
import static java.io.File.createTempFile;
import static java.nio.charset.Charset.forName;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.write;
import static org.grycap.gpf4med.concurrent.TaskRunner.TASK_RUNNER;
import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.grycap.gpf4med.xml.report.ReportType;
import org.grycap.gpf4med.xml.report.ReportsType;
import org.slf4j.Logger;

import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

public final class FecthClient {

	private static final Logger LOGGER = getLogger(FecthClient.class);
	
	public static void fetchReports(final BackEnd backend, final String centerName,
			final String credentials, final List<String> ids, final int retstart, final int retmax, final File directory) throws Exception {
		// save the bulk of files to a temporary file
		final File tmpFile = createTempFile("reports-", ".tmp", directory);
		final String idsParam = Joiner.on(",").skipNulls().join(ids);
		LOGGER.trace("Fetching " + ids.size() + " files, retstart=" + retstart + ", retmax=" + retmax + ", file=" + tmpFile.getPath());		
		
		String reports = backend.xmlGetAllDICOMSRFiles(idsParam, credentials);
		if (reports.length() <= 0) {
			throw new IOException("Response contains no content");
		}
		
		write(tmpFile, reports, forName("UTF-8"));

		// process files to extract individual items from the bulks
		final ListenableFuture<String[]> future = TASK_RUNNER.submit(new Callable<String[]>() {
			@Override
			public String[] call() throws Exception {
				final Set<String> files = newHashSet();
				final ReportsType reports = REPORT_XMLB.typeFromFile(tmpFile);
				checkState(reports != null, "Expected reports set XML, but no content read from downloaded file");
				if (reports.getDICOMSR() != null) {
					final List<ReportType> reportList = reports.getDICOMSR();
					for (final ReportType report : reportList) {						
						final String id = report.getIDTRENCADISReport();
						if (id != null) {
							final File file = new File(directory, id + ".xml");							
							REPORT_XMLB.typeToFile(report, file);
							files.add(file.getCanonicalPath());
							//LOGGER.trace("File " + file.toPath() + " saved.");
						} else {
							LOGGER.warn("Ignoring malformed report (id not found) in backend response");
						}
					}
				} else {
					LOGGER.warn("Ignoring malformed report (report list not found) in backend response");
				}
				return files.toArray(new String[files.size()]);
			}
		});
		addCallback(future, new FutureCallback<String[]>() {
			@Override
			public void onSuccess(final String[] result) {
				LOGGER.info("One bulk report file was processed successfully: " + tmpFile.getName()
						+ ", number of created files: " + result.length);
				deleteQuietly(tmpFile);
			}
			@Override
			public void onFailure(final Throwable error) {
				LOGGER.error("Failed to process bulk report file " + tmpFile.getName(), error);
			}
		});
		// wait for files to be processed
		future.get(1l, TimeUnit.MINUTES);
		//future.get(); 
	}

}