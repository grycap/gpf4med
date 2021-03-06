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

import java.io.Closeable;
import java.util.LinkedList;
import java.util.Queue;

import org.grycap.gpf4med.cloud.CloudService;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.event.EventBusHandler;
import org.grycap.gpf4med.ext.GraphConnectorManager;

import com.google.common.util.concurrent.Monitor;

/**
 * Close registered resources when it is closed.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum CloserService implements CloserServiceIf {

	INSTANCE;

	private final Monitor monitor = new Monitor();

	private final Queue<Closeable> queue = new LinkedList<Closeable>();

	private CloserService() { }

	@Override
	public void preload() {		
		// load default configuration
		ConfigurationManager.INSTANCE.preload();
		// load event bus and register it for closing
		EventBusHandler.INSTANCE.preload();
		register(EventBusHandler.INSTANCE);
		// load graph connector manager and register it for closing
		GraphConnectorManager.INSTANCE.preload();
		register(GraphConnectorManager.INSTANCE);
		// load cloud service and register it for closing
		CloudService.INSTANCE.preload();
		register(CloudService.INSTANCE);
	}

	@Override
	public void register(final Closeable closeable) {
		monitor.enter();
		try {
			queue.add(closeable);
		} finally {
			monitor.leave();
		}
	}

	@Override
	public void close() {
		monitor.enter();
		try {
			while (!queue.isEmpty()) {
				try {
					queue.remove().close();
				} catch (Exception ignore) { }
			}
		} finally {
			monitor.leave();
		}
	}

}