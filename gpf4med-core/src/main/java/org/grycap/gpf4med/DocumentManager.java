/*
 * Copyright 2013 Institute for Molecular Imaging Instrumentation (I3M)
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by 
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 *   http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 */

package org.grycap.gpf4med;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.grycap.gpf4med.concurrent.TaskStorage.TASK_STORAGE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.event.FileEnqueuedEvent;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.util.Id;
import org.grycap.gpf4med.threads.ImportReportsGroupTask;
import org.grycap.gpf4med.util.TRENCADISUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trencadis.infrastructure.services.DICOMStorage.impl.wrapper.xmlOutputDownloadAllReportsID.DICOM_SR_ID;
import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.login.TRENCADIS_SESSION;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * Manages the DICOM-SR documents.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public enum DocumentManager implements Closeable2 {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentManager.class);
	
	public static final int PARTITION = 5;
	public static final int TIMEOUT_SECONDS = 60;
	public static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

	private final ExecutorService executor;
	private final SimpleTimeLimiter limiter;
	
	private Collection<URL> urls = null;
	private ImmutableMap<String, Document> dont_use = null;

	private DocumentManager() {
		executor = Executors.newCachedThreadPool();
		limiter = new SimpleTimeLimiter(executor);		
	}

	@Subscribe
	public void processFileEvent(final FileEnqueuedEvent event) {
		// parse file		
		final File file = FileQueue.INSTANCE.remove();
		if (file != null) {
			String filename = null;
			Document document = null;
			try {			
				filename = file.getCanonicalPath();
				final DocumentLoaderIf proxy = limiter.newProxy(DocumentLoader.create(file), DocumentLoaderIf.class, 
						TIMEOUT_SECONDS, TimeUnit.SECONDS);				
				document = proxy.load();
			} catch (UncheckedTimeoutException ignore) {
				document = null;
				LOGGER.warn("File cannot be parsed, timeout has expired: " + filename);
			} catch (IOException e) {
				LOGGER.warn("Failed to parse file" + filename, e);
			}
			checkState(document != null, "Failed to load document from file: " + filename);
			DocumentQueue.INSTANCE.add(document);
		}
	}

	@Override
	public void setup(final @Nullable Collection<URL> urls) {
		this.urls = urls;
		this.dont_use = null;
	}
	
	@Override
	public void preload() {
		// lazy load, so initial access is needed
		final ImmutableCollection<Document> documents = listDocuments();		
		if (documents != null && documents.size() > 0) {
			LOGGER.info(documents.size() + " DICOM-SR Documents loaded");
		} else {
			LOGGER.warn("No DICOM-SR Documents loaded");
		}
	}

	@Override
	public void close() throws IOException {
		// disable new jobs from being submitted
		executor.shutdown();			
		try {
			// wait a while for existing jobs to terminate
			if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				executor.shutdownNow(); // cancel currently executing jobs
				// wait a while for jobs to respond to being cancelled
				if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					LOGGER.warn("Document fetcher did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (re-)cancel if current thread also interrupted
			executor.shutdownNow();
			// preserve interrupt status
			Thread.currentThread().interrupt();
		} finally {
			LOGGER.trace("Document fetcher terminated");
		}
	}
	
	public @Nullable Document getDocument(final ConceptName conceptName) {
		checkArgument(conceptName != null, "Unninitialized or invalid concept name");
		final ImmutableMap<String, Document> documents = documents(-1, null);
		return documents != null ? documents.get(Id.getId(conceptName)) : null;
	}
	
	public ImmutableCollection<Document> listDocuments() {
		final ImmutableMap<String, Document> documents = documents(-1, null);
		return documents != null ? documents.values() : new ImmutableList.Builder<Document>().build();
	}
	public ImmutableCollection<Document> listDocuments(int idCenter) {
		final ImmutableMap<String, Document> documents = documents(idCenter, null);
		return documents != null ? documents.values() : new ImmutableList.Builder<Document>().build();
	}
	public ImmutableCollection<Document> listDocuments(String idOntology) {
		final ImmutableMap<String, Document> documents = documents(-1, idOntology);
		return documents != null ? documents.values() : new ImmutableList.Builder<Document>().build();
	}
	public ImmutableCollection<Document> listDocuments(int idCenter, String idOntology) {
		final ImmutableMap<String, Document> documents = documents(idCenter, idOntology);
		return documents != null ? documents.values() : new ImmutableList.Builder<Document>().build();
	}
	
	/**
	 * Lazy load -> Adapted to consider TRENCADIS storage
	 * @return the list of available document Documents.
	 */
	public ImmutableMap<String, Document> documents(int idCenter, String idOntology) {
		if (dont_use == null) {
			synchronized (DocumentManager.class) {
				if (dont_use == null) {
					// Documents can be loaded from class-path, local files, through HTTP or through TRENCADIS plug-in 
					File documentsCacheDir = null;
					List<String> medicalCenters = null;
					try {
						// prepare local cache directory
						documentsCacheDir = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), 
								"reports" + File.separator + ConfigurationManager.INSTANCE.getTemplatesVersion());
						if (urls == null && ConfigurationManager.INSTANCE.getTrencadisConfigFile() == null) {
							LOGGER.debug("Source of report not found");
						}
						if (ConfigurationManager.INSTANCE.getTrencadisConfigFile() != null
								&& ConfigurationManager.INSTANCE.getTrencadisPassword() != null) {
							urls = null;
						}
						FileUtils.deleteQuietly(documentsCacheDir);
						FileUtils.forceMkdir(documentsCacheDir);
						medicalCenters = new ArrayList<String>();
						if  (urls == null) {
							try {
								TRENCADIS_SESSION trencadisSession = new TRENCADIS_SESSION(
									ConfigurationManager.INSTANCE.getTrencadisConfigFile(),
									ConfigurationManager.INSTANCE.getTrencadisPassword());
								
								if (idCenter == -1 && idOntology == null) {
									TRENCADISUtils.getReportsID(trencadisSession);
								} else if(idCenter != -1 && idOntology == null) {
									TRENCADISUtils.getReportsID(trencadisSession, idCenter);
								} else if(idCenter == -1 && idOntology != null) {
									TRENCADISUtils.getReportsID(trencadisSession, idOntology);
								} else if(idCenter != -1 && idOntology != null){
									TRENCADISUtils.getReportsID(trencadisSession, idCenter, idOntology);
								}
								
								//final ImportReportsGroupTask groupTask = new ImportReportsGroupTask();
								Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorage = TRENCADISUtils.getDicomStorage();
								if (dicomStorage != null) {
									for (TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorageIDS : dicomStorage) {
										final List<String> ids = new ArrayList<String>();
										final String centerName = dicomStorageIDS.getCenterName().replaceAll(" ", "_");
										medicalCenters.add(centerName);
										final BackEnd backend = new BackEnd(dicomStorageIDS.getBackend().toString());
										for (DICOM_SR_ID id : dicomStorageIDS.getDICOM_DSR_IDS()) {
											ids.add(id.getValue());
											TRENCADISUtils.downloadReport(trencadisSession, backend, centerName, id.getValue(), documentsCacheDir.getAbsolutePath());
										}
										//groupTask.addTask(backend, centerName, trencadisSession.getX509VOMSCredential(), ids, PARTITION, documentsCacheDir);
									}
									//groupTask.sumbitAll();
									//TASK_STORAGE.add(groupTask);
								}
								
							} catch (Exception e3) {
								LOGGER.warn("Failed to get reports from TRENCADIS" , e3);
							}
						}
					} catch (Exception e) {
						LOGGER.warn("Failed to prepare reports for access" , e);
					}
					checkArgument(documentsCacheDir != null, "Uninitialized reports local cache directory");
					
					final ImmutableMap.Builder<String, Document> builder = new ImmutableMap.Builder<String, Document>();
					
					for (final File file : FileUtils.listFiles(documentsCacheDir, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY)) {
						String filename = null;
						try {
							filename = file.getCanonicalPath();
							final Document report = DocumentLoader.create(file).load();
							checkState(report != null && report.getCONTAINER() != null 
									&& report.getCONTAINER().getCONCEPTNAME().getCODEVALUE() != null, "No report found");
							final String id = report.getIDTRENCADISReport();
							checkState(StringUtils.isNotBlank(id), "Uninitialized or invalid TRENCADIS identifier");
							builder.put(id, report);
							LOGGER.trace("New report " + report.getIDReport() + ", ontology " + report.getIDOntology() 
									+ ", loaded from: " + filename);
						} catch (Exception e) {						
							LOGGER.error("Failed to load report: " + filename, e);
						}
					}
					dont_use = builder.build();
				}
			}
		}
		return dont_use;
	}
	
}