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

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.grycap.gpf4med.event.FileEnqueuedEvent;
import org.grycap.gpf4med.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * Manages the DICOM-SR documents.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum DocumentManager implements Closeable2 {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentManager.class);

	public static final int TIMEOUT_SECONDS = 60;
	public static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

	private final ExecutorService executor;
	private final SimpleTimeLimiter limiter;

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
		// nothing to do
	}
	
	@Override
	public void preload() {
		// nothing to do
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

}