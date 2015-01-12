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

package org.grycap.gpf4med.ext;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.Path;

import net.xeoh.plugins.base.PluginInformation;
import net.xeoh.plugins.base.PluginInformation.Information;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.Closeable2;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.util.NamingUtils;
import org.grycap.gpf4med.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Manages plug-ins load.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum GraphConnectorManager implements Closeable2 {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(GraphConnectorManager.class);

	private final PluginManager pluginManager;
	private final PluginManagerUtil pluginManagerUtil;	
	private final PluginInformation pluginInformation;

	private Collection<URL> urls = null;
	private ImmutableMap<String, GraphConnector> dont_use = null;

	private GraphConnectorManager() {
		// create plug-in manager and helper
		pluginManager = PluginManagerFactory.createPluginManager();
		pluginManagerUtil = new PluginManagerUtil(pluginManager);
		pluginInformation = pluginManager.getPlugin(PluginInformation.class);
	}

	@Override
	public void setup(final @Nullable Collection<URL> urls) {
		this.dont_use = null;
		this.urls = urls;
	}

	@Override
	public void preload() {
		// lazy load, so initial access is needed
		final ImmutableMap<String, GraphConnector> connectors = listConnectors();
		if (connectors.size() > 0) {
			LOGGER.info(connectors.size() + " graph connectors loaded");
		} else {
			LOGGER.warn("No graph connectors loaded");
		}
	}

	@Override
	public void close() throws IOException {
		if (pluginManager != null) {
			pluginManager.shutdown();
		}
	}

	public ImmutableMap<String, GraphConnector> listConnectors() {
		return new ImmutableMap.Builder<String, GraphConnector>().putAll(connectors()).build();
	}

	public @Nullable GraphConnector getConnector(final String path) {
		checkArgument(StringUtils.isNotBlank(path), "Uninitialized or invalid path");
		final ImmutableMap<String, GraphConnector> connectors = listConnectors();
		return (connectors != null ? connectors.get(path) : null);
	}

	public ImmutableList<GraphConnectorInformation> getConnectorsInformation() {
		final ImmutableList.Builder<GraphConnectorInformation> builder = new ImmutableList.Builder<GraphConnectorInformation>();
		// get connectors		
		for (final Map.Entry<String, GraphConnector> connector : connectors().entrySet()) {
			try {				
				// resource classes
				final Class<?> resourceDefinition = connector.getValue().restResourceDefinition();
				final Class<?> resourceImplementation = connector.getValue().restResourceImplementation();								
				checkArgument(resourceDefinition.isAnnotationPresent(Path.class), 
						"No @Path annotation found, resource definition must be a REST resource");
				checkArgument(resourceDefinition.isInterface(), "Resource definition is not an interface");
				checkArgument(resourceDefinition.isAssignableFrom(resourceImplementation), 
						"Resource does not implements the definition");				
				// path
				final String path = connector.getKey();
				checkArgument(path.equals(resourceDefinition.getAnnotation(Path.class).value()), 
						"Path does not coincide with with @Path annotation");
				checkArgument(path.matches(".*/v\\d+$"), "Path does not contain API version (e.g. resource/v1)");
				// version
				String version = null;
				List<String> items = new ArrayList<String>(pluginInformation.getInformation(
						Information.VERSION, connector.getValue()));
				if (items != null && items.size() == 1) {
					version = formatVersion(items.get(0));
				} else {
					throw new IllegalStateException("Cannot find version");
				}
				// author
				String author = null;
				items = new ArrayList<String>(pluginInformation.getInformation(Information.AUTHORS, connector.getValue()));
				if (items !=null && items.size() > 0) {
					String authors = "";
					int i = 0;
					for (; i < items.size() - 1; i++) {
						authors += items.get(i) + "; ";
					}
					authors += items.get(i);
					author = authors;
				}
				// package name
				String packageName = getPluginPackagename(connector.getValue());
				if (StringUtils.isNotBlank(packageName)) {
					packageName = FilenameUtils.getName(new URI(packageName).getPath());
				}
				// description
				String description = null;
				if (StringUtils.isNotBlank(connector.getValue().getDescription())) {
					description = connector.getValue().getDescription();
				}
				builder.add(new GraphConnectorInformation(path, resourceDefinition, resourceImplementation, version, 
						author, packageName, description));
			} catch (Exception e) {
				LOGGER.warn("Incomplete information found in the connector: "
						+ connector.getClass().getCanonicalName(), e);
			}
		}
		return builder.build();
	}

	/**
	 * Needed to test the class.
	 * @return plug-in manager.
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	/**
	 * Lazy load.
	 * @return the list of available connectors.
	 */
	private ImmutableMap<String, GraphConnector> connectors() {
		if (dont_use == null) {
			synchronized (GraphConnectorManager.class) {
				if (dont_use == null) {
					// connectors can be loaded from class-path, local files, or through HTTP
					File connectorsCacheDir = null;
					try {
						// prepare local cache directory
						connectorsCacheDir = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), 
								"connectors" + File.separator + ConfigurationManager.INSTANCE.getConnectorsVersion());
						FileUtils.deleteQuietly(connectorsCacheDir);
						FileUtils.forceMkdir(connectorsCacheDir);
						// read index
						if (urls == null) {							
							final URL index = ConfigurationManager.INSTANCE.getConnectorsIndex();
							urls = Arrays.asList(URLUtils.readIndex(index));
						}
						// get a local copy of the connectors			
						for (final URL url : urls) {
							try {
								final File destination = new File(connectorsCacheDir, NamingUtils
										.genSafeFilename(new String[] { url.toString() }, null, ".jar"));
								URLUtils.download(url, destination);
							} catch (Exception e2) {
								LOGGER.warn("Failed to get connector from URL: " + url.toString(), e2);
							}
						}
					} catch (Exception e) {
						LOGGER.error("Failed to prepare connectors for access", e);
					}
					// load available connectors
					checkArgument(connectorsCacheDir != null, "Uninitialized connectors local cache directory");
					final Map<String, GraphConnector> tmpMap = new HashMap<String, GraphConnector>();
					pluginManager.addPluginsFrom(connectorsCacheDir.toURI());
					final Collection<GraphConnector> discoveredConnectors = pluginManagerUtil.getPlugins(GraphConnector.class);
					if (discoveredConnectors != null) {
						for (final GraphConnector connector : discoveredConnectors) {
							final String key = connector.path();
							if (!tmpMap.containsKey(key)) {
								tmpMap.put(key, connector);
								LOGGER.info("Connector '" + key + "' loaded from: " + getPluginPackagename(connector));					
							} else {
								LOGGER.warn("Duplicated connector Id '" + key + "' was not load from: " 
										+ getPluginPackagename(connector));
							}
						}
					}
					dont_use = new ImmutableMap.Builder<String, GraphConnector>().putAll(tmpMap).build();
				}
			}
		}
		return dont_use;
	}

	private String getPluginPackagename(final GraphConnector connector) {
		String packagename = null;
		try {		
			final List<String> items = new ArrayList<String>(pluginInformation.getInformation(
					Information.CLASSPATH_ORIGIN, connector));
			if (items != null && items.size() == 1) {
				packagename = items.get(0);
			}
		} catch (Exception e) {			
			packagename = null;
			LOGGER.warn("Cannot find connector package name", e);
		}
		return packagename;
	}

	private String formatVersion(final String version) {
		String formatted = "";
		try {
			// check that version is a valid integer
			if (Integer.parseInt(version) < 0) {
				throw new IllegalStateException("Version must be a positive integer");
			}
			// parse version
			String release = "", major = "", minor = "";
			final String reverseVersion = new StringBuffer(version).reverse().toString();
			final Iterable<String> chunks = Splitter.fixedLength(2).split(reverseVersion);			
			final Iterator<String> iterator = chunks.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				switch (i) {
				case 0:
					minor = Integer.valueOf(new StringBuffer(iterator.next()).reverse()
							.toString()).toString();
					break;
				case 1:
					major = Integer.valueOf(new StringBuffer(iterator.next()).reverse()
							.toString()).toString();
					break;
				default:
					release = new StringBuffer(iterator.next()).reverse().toString() + release;
					break;
				}
				i++;
			}
			formatted = release + "." + major + "." + minor;
		} catch (Exception e) {
			throw new IllegalStateException("Invalid version found: " + version);
		}
		return formatted;
	}

}