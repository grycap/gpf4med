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
import static com.google.common.base.Preconditions.checkState;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.ext.GraphConnectorManager;

import com.google.common.collect.ImmutableList;

/**
 * Manages the study environment.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum StudyManager {

	INSTANCE;

	public void create(final String graph, final ImmutableList<URL> urls) {
		checkArgument(StringUtils.isNotBlank(graph), "Uninitialized or invalid graph");
		checkArgument(urls != null, "Uninitialized URLs");
		// find a suitable graph connector
		final GraphConnector connector = GraphConnectorManager.INSTANCE.getConnector(graph);
		checkState(connector != null, "Unsupported graph: " + graph);		
		// release any previous graph and set the total number of submitted reports
		destroy();
		GraphDatabaseHandler.INSTANCE.setConnector(connector);
		Statistics.INSTANCE.setTotalSubmitted(urls.size());
		// start asynchronously load
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {			
			@Override
			public void run() {
				DocumentFetcher.INSTANCE.fetch(urls);
			}
		});
	}

	public void destroy() {
		// restart data stores and facilities, including statistics restart
		FileQueue.INSTANCE.restart();
		DocumentQueue.INSTANCE.restart();
		GraphDatabaseHandler.INSTANCE.restart();
		Statistics.INSTANCE.setTotalSubmitted(0);
	}	

}