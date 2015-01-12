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

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.DownloadService.DownloadConfiguration;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.exec.PostProcessTask;
import org.grycap.gpf4med.util.NamingUtils;
import org.grycap.gpf4med.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.SimpleTimeLimiter;

/**
 * Fetches documents from remote repositories. This class uses a cached thread pool, which over-performs 
 * for most cases. See {@link SimpleTimeLimiter} for a comprehensive explanation.
 * @author Erik Torres <ertorser@upv.es>
 *
 */
public enum DocumentFetcher {

	INSTANCE;	

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentFetcher.class);

	public static final int CONNECTION_TIMEOUT_MILLIS = 60000; // 1 minutes
	public static final int READ_TIMEOUT_MILLIS = 180000;      // 3 minutes
	public static final int RETRIES = 3;
	public static final double TIMEOUT_INCREMENT_PERCENTAGE = 0.5d;

	private DocumentFetcher() { }

	public void fecth(final ImmutableList<URL> urls) {
		checkArgument(urls != null, "Uninitialized URLs");
		final ImmutableMap.Builder<URI, File> pendingBuilder = new ImmutableMap.Builder<URI, File>();
		try {
			final File cacheDir = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), "reports");						
			final ImmutableMap.Builder<URI, File> requestBuilder = new ImmutableMap.Builder<URI, File>();
			for (final URL url : urls) {
				try {
					if (URLUtils.isRemoteProtocol(url)) {					
						final URI source = url.toURI().normalize();
						final File destination = new File(cacheDir, NamingUtils
								.genSafeFilename(new String[] { source.toString() }, null, ".xml"));
						requestBuilder.put(source, destination);						
					} else if (URLUtils.isFileProtocol(url)) {
						FileQueue.INSTANCE.add(FileUtils.toFile(url));
					} else {
						FileQueue.INSTANCE.failed(1);
						LOGGER.warn("Ignoring unsupported URL: " + url.toString());
					}
				} catch (Exception e2) {
					FileQueue.INSTANCE.failed();
				}
			}
			final DownloadConfiguration downloadConfig = new DownloadConfiguration(CONNECTION_TIMEOUT_MILLIS,
					READ_TIMEOUT_MILLIS, RETRIES, TIMEOUT_INCREMENT_PERCENTAGE);
			final ImmutableMap<URI, File> pending = new DownloadService().download(requestBuilder.build(), 
					null, downloadConfig, ConfigurationManager.INSTANCE.getFileEncryptionProvider(),
					new PostProcessTask<File>() {						
				@Override
				public void apply(final File object) {
					FileQueue.INSTANCE.add(object);
				}
			});
			if (pending != null) {
				pendingBuilder.putAll(pending);
			}
		} catch (Exception e) {
			final ImmutableMap<URI, File> pending = pendingBuilder.build();
			if (pending != null && pending.size() > 0) {
				FileQueue.INSTANCE.failed(pending.size());				
			}
		}
	}

}