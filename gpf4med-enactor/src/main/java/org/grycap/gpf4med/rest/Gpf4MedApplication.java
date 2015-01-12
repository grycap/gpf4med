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

package org.grycap.gpf4med.rest;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.grycap.gpf4med.reflect.ProxyFactory.newGpf4MedResourceProxy;
import static org.grycap.gpf4med.reflect.ProxyFactory.newResourceProxy;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.grycap.gpf4med.Gpf4MedEnactor;
import org.grycap.gpf4med.SingletonService;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.ext.GraphConnectorManager;
import org.grycap.gpf4med.manager.ShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * JAX-RS application.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedApplication extends Application {

	private final static Logger LOGGER = LoggerFactory.getLogger(Gpf4MedApplication.class);

	public static final String SERVICE_NAME = ConfigurationManager.GPF4MED_NAME + " service";

	final Set<Object> instances = newLinkedHashSet();
	private final ListeningExecutorService executorService = listeningDecorator(newCachedThreadPool());
	private final ShutdownHook shutdownHook = new ShutdownHook();

	public Gpf4MedApplication() {
		// load configuration
		ConfigurationManager.INSTANCE.getRootDir();
		// create a proxy to the Gpf4Med JAX-RS resource implementation		
		final Gpf4MedResource gpf4medProxy = newGpf4MedResourceProxy(executorService);
		instances.add(gpf4medProxy);
		// scan for connectors and create proxies to their JAX-RS resources
		final ImmutableMap<String, GraphConnector> connectors = GraphConnectorManager.INSTANCE.listConnectors();
		if (connectors != null && !connectors.isEmpty()) {
			instances.addAll(transform(connectors.entrySet(), new Function<Map.Entry<String, GraphConnector>, Object>() {
				@Override
				public Object apply(final Entry<String, GraphConnector> entry) {
					final Class<?> type = entry.getValue().restResourceDefinition();
					return newResourceProxy(type, executorService);
				}			
			}));
		}		
		// start service
		final Gpf4MedEnactor service = SingletonService.INSTANCE.service();
		// register shutdown hook
		shutdownHook.register(service);
		LOGGER.info(SERVICE_NAME + " initialized successfully, registered resources: " + Arrays.toString(instances.toArray()));
	}

	@Override
	public Set<Class<?>> getClasses() {		
		final Set<Class<?>> classes = newHashSet();
		// add additional JAX-RS providers
		classes.add(MoxyJsonFeature.class);
		return classes;		
	}

	@Override
	public Set<Object> getSingletons() {
		return instances;
	}

}