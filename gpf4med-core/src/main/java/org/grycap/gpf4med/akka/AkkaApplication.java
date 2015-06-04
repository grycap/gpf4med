/*
 * Copyright 2015 Institute for Molecular Imaging Instrumentation (I3M)
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

package org.grycap.gpf4med.akka;

import java.io.File;

/**
 * Main class.
 * @author Erik Torres <etserrano@gmail.com>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public enum AkkaApplication {
	
	INSTANCE;

	public static final String GPF4MED_FULLNAME  = "gpf4med Multithread Application (gpf4med-akka)";
	public static final String GPF4MED_SHORTNAME = "gpf4med-akka";
	
	private File documentsCacheDir = null;
	
	private AkkaService service = null;
	
	public File getDocumentsCacheDir() {
		return documentsCacheDir;
	}
	
	public void setDocumentsCacheDir(File documentsCacheDir) {
		this.documentsCacheDir = documentsCacheDir;
	}
	
	public AkkaService createService() throws Exception {		
		if (service == null) {
			// start service by calling the lazy loader in the singleton
			service = new AkkaService();
			service.startAsync().awaitRunning();
		}
		return service;
		
	}
	
}