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
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.ext.GraphConnectorManager;
import org.grycap.gpf4med.manager.ShutdownHook;
import org.grycap.gpf4med.rest.Gpf4MedResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractIdleService;

/**
 * Gpf4Med service.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedService extends AbstractIdleService {

	private final static Logger LOGGER = LoggerFactory.getLogger(Gpf4MedService.class);

	public static final String SERVICE_NAME = ConfigurationManager.GPF4MED_NAME + " service";
	public static final String APP_NAME = "gpf4med";

	private final HttpServer server;
	private final ShutdownHook shutdownHook = new ShutdownHook();
	private final String baseUri;

	public Gpf4MedService() {
		// load configuration
		final String hostname = ConfigurationManager.INSTANCE.getContainerHostname();
		final int port = ConfigurationManager.INSTANCE.getContainerPort();
		// create base URI the Grizzly HTTP server will listen on
		baseUri = "http://" + hostname + ":" + port + "/" + APP_NAME + "/";
		// create the HTTP server
		server = Gpf4MedService.createServer(baseUri);
		checkState(server != null, "Uninitialized HTTP server");
		// register shutdown hook
		shutdownHook.register(this);
		LOGGER.info(String.format(SERVICE_NAME + " initialized successfully with WADL available at "
				+ "%sapplication.wadl", baseUri));
	}

	/**
	 * Creates a Grizzly HTTP server exposing JAX-RS resources defined in this service.
	 * @return Grizzly HTTP server.
	 */
	private static HttpServer createServer(final String baseUri) {
		HttpServer server = null;
		try {
			// scan for connectors
			final Set<Class<?>> classSet = newLinkedHashSet();
			final ImmutableMap<String, GraphConnector> connectors = GraphConnectorManager.INSTANCE.listConnectors();
			if (connectors != null && !connectors.isEmpty()) {
				classSet.addAll(transform(connectors.entrySet(), new Function<Map.Entry<String, GraphConnector>, Class<?>>() {
					@Override
					public Class<?> apply(final Entry<String, GraphConnector> entry) {				
						return entry.getValue().restResourceImplementation();
					}			
				}));
			}

			// create a resource configuration that scans for JAX-RS resources and providers
			final ResourceConfig resourceConfig = ResourceConfig.forApplication(new Application() {
				@Override
				public Set<Class<?>> getClasses() {
					final Set<Class<?>> classes = newHashSet();
					classes.add(Gpf4MedResourceImpl.class);
					return classes;
				}
			}).register(MoxyJsonFeature.class);			
			if (!classSet.isEmpty()) {
				resourceConfig.registerClasses(classSet);
			}

			// create a non-started-yet HttpServer instance
			server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), resourceConfig, false);

			// serve static HTTP resources from file-system
			final File htdocsDir = ConfigurationManager.INSTANCE.getHtdocsDir();
			if (htdocsDir != null && htdocsDir.isDirectory() && htdocsDir.canRead()) {
				final StaticHttpHandler staticHttpHandler = new StaticHttpHandler(htdocsDir.getCanonicalPath());
				server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");
			}

			// apply any optional configuration to the HttpServer instance and start the service
			final TCPNIOTransport transport = server.getListener("grizzly").getTransport();
			transport.setTcpNoDelay(true);
		} catch (Exception e) {
			server = null;
			LOGGER.error("Failed to create HTTP server", e);
		}		
		return server;
	}

	public String getBaseUri() {
		return baseUri;
	}

	@Override
	protected void startUp() throws Exception {
		// pre-load configuration and core services
		CloserService.INSTANCE.preload();
		// start HTTP server
		server.start();		
		LOGGER.info(SERVICE_NAME + " started");
	}

	@Override
	protected void shutDown() throws Exception {		
		// stop HTTP server
		server.shutdownNow();
		// close core services
		CloserService.INSTANCE.close();
		LOGGER.info(SERVICE_NAME + " terminated");
	}

}