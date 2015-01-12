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

package org.grycap.gpf4med.cloud;

import static java.lang.String.format;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_CONNECTORS_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_HOME;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_INSTALLER_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_PORT;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_TEMPLATES_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_VERSION;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.saveHttpResponseTo;

import java.net.URI;

import javax.inject.Named;

import org.jclouds.scriptbuilder.InitScript;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures Gpf4Med study service to run as a system daemon.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ConfigureGpf4MedDaemon extends AbstractModule {

	@Override
	protected void configure() {
		// nothing to do
	}

	@Provides
	final InitScript configureGpf4MedDaemon(final @Named(GPF4MED_INSTALLER_URL) String url, 
			final @Named(GPF4MED_VERSION) String version, 
			final @Named(GPF4MED_HOME) String home,
			final @Named(GPF4MED_TEMPLATES_URL) String templates,
			final @Named(GPF4MED_CONNECTORS_URL) String connectors,
			final @Named(GPF4MED_PORT) String port) {
		return InitScript.builder().name("gpf4med")
				.init(saveHttpResponseTo(URI.create(url), "${INSTANCE_HOME}", "install-gpf4med.sh"))
				.run(exec(format("bash install-gpf4med.sh '%s' '%s' '%s' '%s' '%s'", 
						version, home, templates, connectors, port))).build();
	}

}