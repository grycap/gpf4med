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

package org.grycap.gpf4med.conf;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.grycap.gpf4med.util.URLUtils.parseURL;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.Closeable2;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.grycap.gpf4med.util.NetworkingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * Manages configuration.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum ConfigurationManager implements Closeable2 {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

	public static final String MAIN_CONFIGURATION = "gpf4med.xml";
	public static final String TRENCADIS_CONFIGURATION = "gpf4med-trencadis.xml";
	public static final String CONTAINER_CONFIGURATION = "gpf4med-container.xml";
	public static final String ENACTOR_CONFIGURATION = "gpf4med-enactor.xml";

	public static final ImmutableList<String> IGNORE_LIST = new ImmutableList.Builder<String>()
			.add("logback.xml").build();

	public static final String GPF4MED_NAME = "Graph processing framework for medical information (Gpf4Med)";

	private ConfigurationManager.Configuration dont_use = null;
	private Collection<URL> urls = ConfigurationManager.getDefaultConfiguration();	

	// public methods

	public File getRootDir() {
		return configuration().getRootDir();
	}

	public URL getTemplatesUrl() {
		return configuration().getTemplatesUrl();
	}

	public URL getConnectorsUrl() {
		return configuration().getConnectorsUrl();
	}

	public File getLocalCacheDir() throws IOException {
		final File localCacheDir = configuration().getLocalCacheDir();
		if (!localCacheDir.exists()) {
			checkState(localCacheDir.mkdirs(), "Cannot create local cache directory");
		} else {
			checkState(localCacheDir.isDirectory() && localCacheDir.canWrite(), 
					"Invalid local cache directory: " + localCacheDir.getCanonicalPath());
		}
		return localCacheDir;
	}

	public File getHtdocsDir() {
		return configuration().getHtdocsDir();
	}

	public boolean getEncryptLocalStorage() {
		return configuration().getEncryptLocalStorage();
	}

	public boolean getUseStrongCryptography() {
		return configuration().getUseStrongCryptography();
	}

	public String getTemplatesVersion() {
		return configuration().getTemplatesVersion();
	}

	public URL getTemplatesIndex() {
		return configuration().getTemplatesIndex();
	}

	public String getConnectorsVersion() {
		return configuration().getConnectorsVersion();
	}

	public URL getConnectorsIndex() {
		return configuration().getConnectorsIndex();
	}

	public String getContainerHostname() {
		return configuration().getContainerHostname();
	}

	public int getContainerPort() {
		return configuration().getContainerPort();
	}

	public String getEnactorProvider() {
		return configuration().getEnactorProvider().or("");
	}

	public String getEnactorIdentity() {
		return configuration().getEnactorIdentity().or("");
	}

	public String getEnactorCredential() {
		return configuration().getEnactorCredential().or("");
	}

	public String getServerVersion() {
		return configuration().getServerVersion().or("");
	}

	public @Nullable URL getServerInstallerUrl() {
		return configuration().getServerInstallerUrl().orNull();
	}

	public @Nullable File getServerHome() {
		return configuration().getServerHome().orNull();
	}

	public @Nullable File getTrencadisConfigFile() {
		return configuration().getTrencadisConfigFile().orNull();
	}
	
	public @Nullable String getTrencadisPassword() {
		return configuration().getTrencadisPassword().orNull();
	}
	
	public @Nullable String getProperty(final String name, final @Nullable String defaultValue) {
		return configuration().getProperty(name, defaultValue);
	}

	public @Nullable FileEncryptionProvider getFileEncryptionProvider() {
		return configuration().getFileEncryptionProvider();
	}

	@Override
	public void setup(final @Nullable Collection<URL> urls) {		
		this.dont_use = null;
		this.urls = (urls != null && !urls.isEmpty() ? urls 
				: ConfigurationManager.getDefaultConfiguration());
	}

	@Override
	public void preload() {
		// lazy load, so initial access is needed
		ConfigurationManager.INSTANCE.getRootDir();
	}

	@Override
	public void close() throws IOException { }

	// auxiliary methods

	private ConfigurationManager.Configuration configuration() {
		if (dont_use == null) {
			synchronized (ConfigurationManager.Configuration.class) {
				if (dont_use == null && urls != null) {
					try {						
						XMLConfiguration main = null;
						// sorting secondary configurations ensures that combination 
						// always result the same
						final SortedMap<String, XMLConfiguration> secondary = new TreeMap<String, XMLConfiguration>();
						// extract main configuration
						for (final URL url : urls) {
							final String filename = FilenameUtils.getName(url.getPath());							
							if (MAIN_CONFIGURATION.equalsIgnoreCase(filename)) {
								main = new XMLConfiguration(url);
								LOGGER.info("Loading main configuration from: " + url.toString());
							} else if (!IGNORE_LIST.contains(FilenameUtils.getName(url.getPath()))) {
								secondary.put(filename, new XMLConfiguration(url));
								LOGGER.info("Loading secondary configuration from: " + url.toString());
							} else {
								LOGGER.info("Ignoring: " + url.toString());
							}
						}
						if (main != null) {							
							final CombinedConfiguration configuration = new CombinedConfiguration(new OverrideCombiner());							
							configuration.addConfiguration(main, MAIN_CONFIGURATION);
							for (final Map.Entry<String, XMLConfiguration> entry : secondary.entrySet()) {
								configuration.addConfiguration(entry.getValue(), entry.getKey());
							}
							if (LOGGER.isDebugEnabled()) {
								String names = "";
								for (final String name : configuration.getConfigurationNameList()) {
									names += name + " ";
								}
								LOGGER.trace("Loading configuration from: " + names);
							}
							final List<String> foundNameList = new ArrayList<String>();
							// get main property will fail if the requested property is missing
							configuration.setThrowExceptionOnMissing(true);
							final File rootDir = getFile("gpf4med-root", configuration, foundNameList, true, null);							
							final URL templatesUrl = getUrl("storage.templates", configuration, foundNameList, null);
							final URL connectorsUrl = getUrl("storage.connectors", configuration, foundNameList, null);
							final File localCacheDir = getFile("storage.local-cache", configuration, foundNameList, true, null);
							final File htdocsDir = getFile("storage.htdocs", configuration, foundNameList, false, null);							
							final boolean encryptLocalStorage = getBoolean("security.encrypt-local-storage", configuration, foundNameList, true);
							final boolean useStrongCryptography = getBoolean("security.use-strong-cryptography", configuration, foundNameList, false);
							final String templatesVersion = getString("dicom.version", configuration, foundNameList, null);
							final URL templatesIndex = getUrl("dicom.index", configuration, foundNameList, null);
							final String connectorsVersion = getString("graph.version", configuration, foundNameList, null);
							final URL connectorsIndex = getUrl("graph.index", configuration, foundNameList, null);
							// get secondary property will return null if the requested property is missing
							configuration.setThrowExceptionOnMissing(false);
							final String containerHostname = getString("service-container.hostname", configuration, foundNameList, null);
							final int containerPort = getInteger("service-container.port", configuration, foundNameList, new Integer(8080));
							final String enactorProvider = getString("enactor.provider", configuration, foundNameList, null);
							final File enactorIdentity = getFile("enactor.identity", configuration, foundNameList, false, null);
							final File enactorCredential = getFile("enactor.credential", configuration, foundNameList, false, null);							
							final String serverVersion = getString("container-server.version", configuration, foundNameList, null);
							final URL serverInstallerUrl = getUrl("container-server.installer.url", configuration, foundNameList, null);
							final File serverHome = getFile("container-server.home", configuration, foundNameList, false, null);
							
							// Add this for read the TRENCADIS configuration
							final File trencadisConfiguration = getFile("trencadis.config-file", configuration, foundNameList, false, null);
							final String trencadisPassword = getString("trencadis.pass", configuration, foundNameList, null);
							
							// get other (free-format) properties
							final Iterator<String> keyIterator = configuration.getKeys();
							final Map<String, String> othersMap = new Hashtable<String, String>();
							while (keyIterator.hasNext()) {
								final String key = keyIterator.next();
								if (key != null && !foundNameList.contains(key)) {
									final String value = configuration.getString(key);
									if (value != null) {
										othersMap.put(key, value);
									}								
								}
							}
							dont_use = new Configuration(rootDir, templatesUrl, connectorsUrl, localCacheDir, htdocsDir, 
									encryptLocalStorage, useStrongCryptography, templatesVersion, templatesIndex, 
									connectorsVersion, connectorsIndex, containerHostname, containerPort, enactorProvider, 
									enactorIdentity, enactorCredential, serverVersion, serverInstallerUrl, serverHome,
									trencadisConfiguration, trencadisPassword, othersMap);
							LOGGER.info(dont_use.toString());
						} else {
							throw new IllegalStateException("Main configuration not found");
						}
					} catch (IllegalStateException e1) {
						throw e1;
					} catch (ConfigurationException e2) {
						throw new IllegalStateException(e2);
					} catch (Exception e) {						
						LOGGER.error("Failed to load configuration", e);
					}
				}
			}
		}
		return dont_use;
	}

	private static ImmutableList<URL> getDefaultConfiguration() {
		
		return new ImmutableList.Builder<URL>()
				.add(ConfigurationManager.class.getClassLoader().getResource(MAIN_CONFIGURATION))
				.add(ConfigurationManager.class.getClassLoader().getResource(TRENCADIS_CONFIGURATION))
				.add(ConfigurationManager.class.getClassLoader().getResource(CONTAINER_CONFIGURATION))
				.add(ConfigurationManager.class.getClassLoader().getResource(ENACTOR_CONFIGURATION))
				.build();
	}

	private static @Nullable File getFile(final String name, final CombinedConfiguration configuration, 
			final List<String> foundNameList, final boolean ensureWriting, final @Nullable File defaultValue) {
		foundNameList.add(name);
		final String value = subsEnvVars(configuration.getString(name), ensureWriting);
		return value != null ? new File(value) : defaultValue;		
	}

	private static @Nullable URL getUrl(final String name, final CombinedConfiguration configuration, 
			final List<String> foundNameList, final @Nullable URL defaultValue) {
		foundNameList.add(name);
		String value = subsEnvVars(configuration.getString(name), false);		
		URL url = null;
		try {
			url = parseURL(value);
		} catch (MalformedURLException e) {
			url = defaultValue;
			LOGGER.warn("The property contains an invalid URL: " + name);
		}
		return url;
	}

	private static @Nullable String getString(final String name, final CombinedConfiguration configuration, 
			final List<String> foundNameList, final @Nullable String defaultValue) {
		foundNameList.add(name);
		return configuration.getString(name, defaultValue);
	}

	private static boolean getBoolean(final String name, final CombinedConfiguration configuration, 
			final List<String> foundNameList, final boolean defaultValue) {
		foundNameList.add(name);
		return configuration.getBoolean(name, defaultValue);
	}

	private static @Nullable Integer getInteger(final String name, final CombinedConfiguration configuration, 
			final List<String> foundNameList, final @Nullable Integer defaultValue) {
		foundNameList.add(name);
		return configuration.getInt(name, defaultValue);
	}

	private static String subsEnvVars(final String path, final boolean ensureWriting) {
		String substituted = path;
		if (path != null && path.trim().length() > 0) {
			substituted = path.trim();
			if (substituted.startsWith("$HOME")) {
				final String userDir = FileUtils.getUserDirectoryPath();
				substituted = (ensureWriting && !new File(userDir).canWrite()
						? substituted.replaceFirst("\\$HOME", FileUtils.getTempDirectoryPath())
								: substituted.replaceFirst("\\$HOME", userDir));
			} else if (substituted.startsWith("$TMP")) {
				substituted = substituted.replaceFirst("\\$TMP", FileUtils.getTempDirectoryPath());				
			}
		}
		return substituted;
	}

	/* Inner classes */

	public static class Configuration {
		// common configuration
		private final File rootDir;
		private final URL templatesUrl;
		private final URL connectorsUrl;
		private final File localCacheDir;
		private final File htdocsDir;
		private final boolean encryptLocalStorage;
		private final boolean useStrongCryptography;
		private final String templatesVersion;
		private final URL templatesIndex;
		private final String connectorsVersion;
		private final URL connectorsIndex;
		// service container
		private final String containerHostname;
		private final int containerPort;
		// enactor service
		private final Optional<String> enactorProvider;
		private final Optional<String> enactorIdentity;
		private final Optional<String> enactorCredential;		
		private final Optional<String> serverVersion;
		private final Optional<URL> serverInstallerUrl;
		private final Optional<File> serverHome;
		// TRENCADIS
		private final Optional<File> trencadisConfiguration;
		private final Optional<String> trencadisPassword;
		// other configurations
		private final ImmutableMap<String, String> othersMap;
		// file encryption provider
		private final @Nullable FileEncryptionProvider fileEncryptionProvider;
		public Configuration(final File rootDir, final URL templatesUrl, final URL connectorsUrl, final File localCacheDir, 
				final File htdocsDir, final boolean encryptLocalStorage, final boolean useStrongCryptography, 
				final String templatesVersion, final URL templatesIndex, final String connectorsVersion, 
				final URL connectorsIndex, final @Nullable String containerHostname, final int containerPort,
				final @Nullable String enactorProvider, final @Nullable File enactorIdentity, 
				final @Nullable File enactorCredential, final @Nullable String serverVersion,
				final @Nullable URL serverInstallerUrl, final @Nullable File serverHome,
				final @Nullable File trencadisConfiguration, final @Nullable String trencadisPassword, 
				final @Nullable Map<String, String> othersMap) {
			this.rootDir = checkNotNull(rootDir, "Uninitialized root directory");
			this.templatesUrl = checkNotNull(templatesUrl, "Uninitialized templates URL");
			this.connectorsUrl = checkNotNull(connectorsUrl, "Uninitialized connectors URL");
			this.localCacheDir = checkNotNull(localCacheDir, "Uninitialized local cache directory");			
			this.htdocsDir = checkNotNull(htdocsDir, "Uninitialized hyper-text documents directory");			
			this.encryptLocalStorage = encryptLocalStorage;
			this.useStrongCryptography = useStrongCryptography;
			FileEncryptionProvider tmp = null;
			if (this.encryptLocalStorage) {
				if (this.useStrongCryptography && !FileEncryptionProvider.UNLIMITED_CRYPTOGRAPHY_AVAILABLE) {
					throw new IllegalStateException("Strong cryptography is unavailable");
				}
				try {
					tmp = FileEncryptionProvider.getInstance(RandomStringUtils.randomAscii(1024));
				} catch (Exception e2) {
					LOGGER.warn("Failed to create file encryption provider", e2);
				}
				checkState(tmp != null, "No file encryption provider was created");
			}
			this.fileEncryptionProvider = tmp;
			checkArgument(StringUtils.isNotBlank(templatesVersion), "Uninitialized or invalid templates version");
			this.templatesVersion = templatesVersion.trim();
			this.templatesIndex = checkNotNull(templatesIndex, "Uninitialized templates index");
			checkArgument(StringUtils.isNotBlank(connectorsVersion), "Uninitialized or invalid connectors version");
			this.connectorsVersion = connectorsVersion.trim();
			this.connectorsIndex = checkNotNull(connectorsIndex, "Uninitialized connectors index");
			this.containerHostname = StringUtils.isNotBlank(containerHostname) ? containerHostname.trim() : NetworkingUtils.getInet4Address();
			this.containerPort = containerPort;
			this.enactorProvider = Optional.fromNullable(StringUtils.trimToNull(enactorProvider));
			this.enactorIdentity = Optional.fromNullable(readFromFile(enactorIdentity));
			this.enactorCredential = Optional.fromNullable(readFromFile(enactorCredential));			
			this.serverVersion = Optional.fromNullable(StringUtils.trimToNull(serverVersion));
			this.serverInstallerUrl = Optional.fromNullable(serverInstallerUrl);
			this.serverHome = Optional.fromNullable(serverHome);
			this.trencadisConfiguration = Optional.fromNullable(trencadisConfiguration);
			this.trencadisPassword = Optional.fromNullable(StringUtils.trimToNull(trencadisPassword));
			this.othersMap = new ImmutableMap.Builder<String, String>().putAll(othersMap).build();			
		}		
		public File getRootDir() {
			return rootDir;
		}
		public URL getTemplatesUrl() {
			return templatesUrl;
		}
		public URL getConnectorsUrl() {
			return connectorsUrl;
		}
		public File getLocalCacheDir() {
			return localCacheDir;
		}
		public File getHtdocsDir() {
			return htdocsDir;
		}		
		public boolean getEncryptLocalStorage() {
			return encryptLocalStorage;
		}
		public boolean getUseStrongCryptography() {
			return useStrongCryptography;
		}
		public String getTemplatesVersion() {
			return templatesVersion;
		}
		public URL getTemplatesIndex() {
			return templatesIndex;
		}
		public String getConnectorsVersion() {
			return connectorsVersion;
		}
		public URL getConnectorsIndex() {
			return connectorsIndex;
		}
		public String getContainerHostname() {
			return containerHostname;
		}
		public int getContainerPort() {
			return containerPort;
		}			
		public Optional<String> getEnactorProvider() {
			return enactorProvider;
		}
		public Optional<String> getEnactorIdentity() {
			return enactorIdentity;
		}
		public Optional<String> getEnactorCredential() {
			return enactorCredential;
		}		
		public Optional<String> getServerVersion() {
			return serverVersion;
		}
		public Optional<URL> getServerInstallerUrl() {
			return serverInstallerUrl;
		}
		public Optional<File> getServerHome() {
			return serverHome;
		}
		public Optional<File> getTrencadisConfigFile() {
			return trencadisConfiguration;
		}
		public Optional<String> getTrencadisPassword() {
			return trencadisPassword;
		}
		public ImmutableMap<String, String> getOthersMap() {
			return othersMap;
		}
		public @Nullable String getProperty(final String name, final @Nullable String _default) {
			String configuration = null;
			if (othersMap != null && name != null) {
				configuration = othersMap.get(name);
			}
			return configuration != null ? configuration : _default; 
		}
		public @Nullable FileEncryptionProvider getFileEncryptionProvider() {
			return fileEncryptionProvider;
		}
		@Override
		public String toString() {
			return toStringHelper(this)
					.add("rootDir", rootDir)
					.add("templatesUrl", templatesUrl)
					.add("connectorsUrl", connectorsUrl)
					.add("localCacheDir", localCacheDir)
					.add("htdocsDir", htdocsDir)					
					.add("encryptLocalStorage", encryptLocalStorage)
					.add("useStrongCryptography", useStrongCryptography)
					.add("templatesVersion", templatesVersion)
					.add("templatesIndex", templatesIndex)
					.add("connectorsVersion", connectorsVersion)
					.add("connectorsIndex", connectorsIndex)
					.add("containerHostname", containerHostname)
					.add("containerPort", containerPort)
					.add("enactorProvider", enactorProvider.orNull())
					.add("enactorIdentity", enactorIdentity.orNull())
					.add("enactorCredential", enactorCredential.orNull())
					.add("serverVersion", serverVersion.orNull())
					.add("serverInstallerUrl", serverInstallerUrl.orNull())
					.add("serverHome", serverHome.orNull())
					.add("trencadisConfiguration", trencadisConfiguration.orNull())
					.add("trencadisPassword", trencadisPassword.orNull())
					.add("customProperties", customPropertiesToString())
					.toString();
		}
		private String customPropertiesToString() {
			String str = "[";
			if (othersMap != null) {
				for (final Map.Entry<String, String> entry : othersMap.entrySet()) {
					str += entry.getKey() + "=" + entry.getValue() + " ";
				}
			}
			return str.trim() + "]";
		}
		private static @Nullable String readFromFile(final File file) {
			String str = null;
			if (file != null) {
				if (file.canRead()) {
					try {
						str = StringUtils.trimToEmpty(Files.toString(file, Charsets.UTF_8));
					} catch (Exception e) {
						LOGGER.error("Failed to read file: " + file.getPath(), e);
					}	
				} else {
					LOGGER.warn("Ignoring unreadable file: " + file.getPath());
				}
			}
			return str; 
		}
	}

}