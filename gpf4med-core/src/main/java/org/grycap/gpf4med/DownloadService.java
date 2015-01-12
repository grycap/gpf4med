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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.pool.PoolStats;
import org.grycap.gpf4med.exec.PostProcessTask;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Handles concurrent file download. This class will do its best effort to optimally handle the 
 * downloads, opening a pool of connections to the servers and reusing them as much as possible. Also, 
 * it will create several concurrent threads in the JVM in order to perform simultaneous downloads.
 * @author Erik Torres <ertorser@upv.es>
 */
public class DownloadService {

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadService.class);

	public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 4;
	public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;

	/**
	 * A convenient variant of the method {@link #downloadNoFailOnReadTimeout(ImmutableMap, FileValidator, DownloadConfiguration)}
	 * that uses the default configuration to process the requests.
	 * @param requests a key-value map with the list of requests to handle. The source of the object is the key of
	 *        the map, while the value is the destination file.
	 * @param validator checks the file for correctness.
	 * @param task an optional task that will be executed passing each individual file as parameter, when the download 
	 *        of the file ends.
	 * @return the requests that could not be served after exhausting the individual retries.
	 * @throws IOException if an error occurs in the execution of the operation.
	 */
	public ImmutableMap<URI, File> download(final ImmutableMap<URI, File> requests, final @Nullable FileValidator validator,
			final @Nullable FileEncryptionProvider encryptionProvider, final @Nullable PostProcessTask<File> task) 
					throws IOException {
		return download(requests, validator, new DefaultDownloadConfiguration(), encryptionProvider, task);
	}

	/**
	 * Uses a group of URIs to retrieve objects and writes them to the same number of files. This method will do 
	 * its best effort to optimally handle the downloads, opening a pool of connections to the servers and reusing 
	 * them as much as possible. Also, it will create several concurrent threads in the JVM in order to perform 
	 * simultaneous downloads.
	 * @param requests a key-value map with the list of requests to handle. The source of the object is the key of
	 *        the map, while the value is the destination file.
	 * @param validator checks the file for correctness.
	 * @param config download settings.
	 * @param encryptionProvider an optional encryption provider that, when available, is used to encrypt the 
	 *        files after download.
	 * @param task an optional task that will be executed passing each individual file as parameter, when the download 
	 *        of the file ends.
	 * @return the requests that could not be served after exhausting the individual retries.
	 * @throws IOException if an error occurs in the execution of the operation.
	 */
	public ImmutableMap<URI, File> download(final ImmutableMap<URI, File> requests, 
			final @Nullable FileValidator validator, 
			final DownloadConfiguration config, 
			final @Nullable FileEncryptionProvider encryptionProvider, 
			final @Nullable PostProcessTask<File> task) throws IOException {
		checkArgument(requests != null, "Uninitialized request");
		checkArgument(config != null, "Uninitialized configuration");
		ImmutableMap<URI, File> pending = ImmutableMap.copyOf(requests);
		final List<URI> cancelled = new ArrayList<URI>();
		try {
			for (int attempt = 0; attempt < config.getRetries() && !pending.isEmpty() 
					&& pending.size() > cancelled.size(); attempt++) {
				LOGGER.info("Attempt " + (attempt + 1) + " to download " + requests.size() + " files");
				// create connection manager
				final PoolingNHttpClientConnectionManager connectionManager = createConnectionManager();				
				// create HTTP asynchronous client
				int eSoTimeoutMs = config.soToMs + (int)(config.soToMs * attempt * (config.toIncPercent >= 0.0d 
						&& config.toIncPercent <= 1.0d ? config.toIncPercent : 0.0d));
				int eConnTimeoutMs = config.connToMs + (int)(config.connToMs * attempt * (config.toIncPercent >= 0.0d 
						&& config.toIncPercent <= 1.0d ? config.toIncPercent : 0.0d));
				final RequestConfig requestConfig = RequestConfig.custom()
						.setConnectTimeout(eConnTimeoutMs)
						.setConnectionRequestTimeout(eConnTimeoutMs)
						.setSocketTimeout(eSoTimeoutMs).build();
				final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
						.setConnectionManager(connectionManager)
						.setDefaultRequestConfig(requestConfig).build();
				httpclient.start();
				// attempt to perform download
				try {
					final CountDownLatch latch = new CountDownLatch(pending.size());
					for (final Map.Entry<URI, File> entry : pending.entrySet()) {
						final URI uri = entry.getKey();
						if (cancelled.contains(uri)) {
							continue;
						}
						final File file = entry.getValue();
						FileUtils.forceMkdir(file.getParentFile());
						final HttpGet request = new HttpGet(uri);	
						final HttpAsyncRequestProducer producer = new BasicAsyncRequestProducer(
								new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()), request);					
						final ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(file) {
							@Override
							protected File process(final HttpResponse response, final File file,
									final ContentType contentType) throws Exception {
								releaseResources();
								if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
									FileUtils.deleteQuietly(file);
									throw new ClientProtocolException("Download failed: " 
											+ response.getStatusLine());
								}
								if (validator != null && !validator.isValid(file)) {
									FileUtils.deleteQuietly(file);
									cancelled.add(uri);
									throw new IOException(file.getCanonicalPath() 
											+ " not recognised as a supported file format");
								}
								if (encryptionProvider != null) {
									try {
										final File cipherFile = File.createTempFile(RandomStringUtils.random(8, true, true), ".tmp");
										encryptionProvider.encrypt(new FileInputStream(file), new FileOutputStream(cipherFile));
										FileUtils.deleteQuietly(file);
										FileUtils.moveFile(cipherFile, file);
										LOGGER.info("File encrypted: " + file.getCanonicalPath());
									} catch (Exception e) {
										FileUtils.deleteQuietly(file);
										cancelled.add(uri);
										LOGGER.warn("Failed to encrypt: " + file.getCanonicalPath(), e);
										throw new IOException("File encryption failed");
									}
								}
								LOGGER.info("Download succeed to file: " + file.getCanonicalPath());
								return file;
							}
						};
						httpclient.execute(producer, consumer, new FutureCallback<File>() {
							@Override
							public void completed(final File result) {
								request.releaseConnection();
								latch.countDown();
								if (task != null) {
									task.apply(result);
								}
								LOGGER.info("Request succeed: " + request.getRequestLine() 
										+ " => Response file length: " + result.length());
							}
							@Override
							public void failed(final Exception ex) {
								request.releaseConnection();
								FileUtils.deleteQuietly(file);
								latch.countDown();
								LOGGER.error("Request failed: " + request.getRequestLine() + "=>" + ex);
							}
							@Override
							public void cancelled() {
								request.releaseConnection();
								FileUtils.deleteQuietly(file);
								latch.countDown();
								LOGGER.error("Request cancelled: " + request.getRequestLine());
							}
						});
					}
					latch.await();
				} finally {
					try {
						httpclient.close();
					} catch (Exception ignore) { }
					try {
						shutdown(connectionManager, 0l);
					} catch (Exception ignore) { }
				}
				// populate the pending list with the files that does not exist
				final ImmutableMap.Builder<URI, File> builder = new ImmutableMap.Builder<URI, File>();
				for (final Map.Entry<URI, File> entry : requests.entrySet()) {
					if (!entry.getValue().exists()) {						
						builder.put(entry.getKey(), entry.getValue());
					}
				}
				pending = builder.build();
				if ((attempt + 1) < config.retries && !pending.isEmpty() 
						&& pending.size() > cancelled.size()) {
					final long waitingTime = (long)(config.soToMs * 0.1d);
					LOGGER.info("Waiting " + waitingTime +  " ms before attempt " 
							+ (attempt + 2) + " to download " + requests.size() 
							+ " pending files");
					Thread.sleep(waitingTime);
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException("Download has failed", e);
		}
		return pending;
	}	

	private PoolingNHttpClientConnectionManager createConnectionManager() throws IOReactorException {
		final IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setSoReuseAddress(false).build();		
		final PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
				new DefaultConnectingIOReactor(ioReactorConfig));
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		LOGGER.info("Connection manager created: [max_connections_total=" 
				+ connectionManager.getMaxTotal() + ", max_connections_per_route=" 
				+ connectionManager.getDefaultMaxPerRoute() + "]");
		return connectionManager;
	}

	/**
	 * Initiates shutdown of the download manager and blocks approximately for the 
	 * given period of time in milliseconds waiting for the download manager to 
	 * terminate all active connections, to shut down itself and to release system 
	 * resources it currently holds.
	 * @param connectionManager connection session pool.
	 * @param waitMs wait time in milliseconds.
	 */
	private void shutdown(final PoolingNHttpClientConnectionManager connectionManager, final long waitMs) {
		PoolStats poolStats = null;
		try {
			if (connectionManager != null) {				
				try {
					poolStats = connectionManager.getTotalStats();
				} catch (Exception ignore) {
					LOGGER.warn("Failed to collect connection manager stats", ignore);
				}
				connectionManager.shutdown(waitMs);
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to shut down connection manager", e);
		} finally {
			LOGGER.info("Connection manager was shutted down [pool_stats_before_shutdown=" 
					+ (poolStats != null ? poolStats.toString() : "NOT_AVAILABLE")+ "]");
		}
	}

	/* Inner classes */

	public static class DefaultDownloadConfiguration extends DownloadConfiguration {
		public static final int CONNECTION_TIMEOUT_MS = 3000; // 3 seconds
		public static final int READ_TIMEOUT_MS = 3000;       // 3 seconds
		public static final int NUM_RETRIES = 3;
		public static final double TIMEOUT_INCREMENT_PERCENTAGE = 0.5d;

		public DefaultDownloadConfiguration() {
			super(CONNECTION_TIMEOUT_MS, READ_TIMEOUT_MS, NUM_RETRIES, TIMEOUT_INCREMENT_PERCENTAGE);
		}
	}

	public static class DownloadConfiguration {
		private final int connToMs;
		private final int soToMs;
		private final int retries;
		private final double toIncPercent;		

		/**
		 * Creates a download configuration with the specified parameters.
		 * @param connToMs connection time-out in milliseconds.
		 * @param soToMs read time-out in milliseconds.
		 * @param retries the number of retries in case of an error.
		 * @param toIncPercent the starting timeout period is incremented this percentage for 
		 *        consecutive errors.
		 */
		public DownloadConfiguration(final int connToMs, final int soToMs, final int retries, 
				final double toIncPercent) {
			this.connToMs = connToMs;
			this.soToMs = soToMs;
			this.retries = retries;
			this.toIncPercent = toIncPercent;
		}	

		public int getConnToMs() {
			return connToMs;
		}

		public int getSoToMs() {
			return soToMs;
		}

		public int getRetries() {
			return retries;
		}

		public double getToIncPercent() {
			return toIncPercent;
		}
	}

}
