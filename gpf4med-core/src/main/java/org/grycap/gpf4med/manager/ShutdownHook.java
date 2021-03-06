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

package org.grycap.gpf4med.manager;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * Executes when the Java virtual machine shuts down, calling the stop method of
 * the attached service and waiting until the service finishes.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ShutdownHook {

	private final static Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

	private final Thread hook;

	// preserves insertion order of entries
	private final Map<String, AbstractIdleService> services = Collections
			.synchronizedMap(new LinkedHashMap<String, AbstractIdleService>());

	/**
	 * Creates an instance of this class and registers it with the JVM.
	 */
	public ShutdownHook() {
		hook = new Thread() {
			@Override
			public void run() {
				for (final Map.Entry<String, AbstractIdleService> entry : services.entrySet()) {
					final AbstractIdleService service = entry.getValue();
					if (service != null && service.isRunning()) {
						service.stopAsync().awaitTerminated();
					}					
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(hook);
	}

	/**
	 * Registers a service for shutdown.
	 * @param service the service of which the shutdown sequence is managed by this class.
	 */
	public void register(final AbstractIdleService service) {
		checkArgument(service != null, "Uninitialized or invalid service");
		final String name = service.getClass().getCanonicalName();
		services.put(name, service);
	}

	/**
	 * Cancels the execution of the shutdown sequence specified in this class.
	 */
	public void cancel() {
		try {
			Runtime.getRuntime().removeShutdownHook(hook);
			LOGGER.info("Shutdown hook dettached");
		} catch (Exception ignore) { }
	}	

}