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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.manager.ShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * Gpf4Med enactment service.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedEnactor extends AbstractIdleService {

	private final static Logger LOGGER = LoggerFactory.getLogger(Gpf4MedEnactor.class);

	public static final String SERVICE_NAME = ConfigurationManager.GPF4MED_NAME + " enactment service";
	public static final String DEFAULT_CONFIG_DIR = "/opt/gpf4med/etc";

	private final ShutdownHook shutdownHook = new ShutdownHook();

	public Gpf4MedEnactor() {
		// register shutdown hook
		shutdownHook.register(this);
		LOGGER.info(SERVICE_NAME + " initialized successfully");
	}

	@Override
	protected void startUp() throws Exception {
		// setup configuration URLs
		final File configDir = new File(DEFAULT_CONFIG_DIR);
		if (configDir.isDirectory()) {
			final Collection<URL> config = new ArrayList<URL>();
			final Collection<File> configFiles = FileUtils.listFiles(configDir, new String[]{ "xml" }, false);
			for (final File file : configFiles) {
				config.add(file.toURI().toURL());
			}
			if (!config.isEmpty()) {
				ConfigurationManager.INSTANCE.setup(config);
			}
		}		
		// pre-load configuration and core services
		CloserService.INSTANCE.preload();
		LOGGER.info(SERVICE_NAME + " started");
	}

	@Override
	protected void shutDown() throws Exception {		
		// close core services
		CloserService.INSTANCE.close();
		LOGGER.info(SERVICE_NAME + " terminated");
	}

}