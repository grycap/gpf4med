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

package org.grycap.gpf4med.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.Closeable2;
import org.grycap.gpf4med.DocumentQueue;
import org.grycap.gpf4med.Statistics;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.event.DocumentEnqueuedEvent;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.model.Document;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.google.common.base.Throwables;
import com.google.common.eventbus.Subscribe;

/**
 * Graph database handler.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum GraphDatabaseHandler implements Closeable2 {

	INSTANCE;

	public static final String NEO4J_PROPERTIES = "neo4j.properties";

	private GraphDatabaseService graphDb = null;

	private GraphConnector connector = null;

	@Subscribe
	public void processDocumentEvent(final DocumentEnqueuedEvent event) {
		checkState(graphDb != null && connector != null, 
				"New document enqueued event received, but database is not ready");
		// load document
		final Document document = DocumentQueue.INSTANCE.remove();
		if (document != null) {
			try {
				connector.add(document);
				Statistics.INSTANCE.incSuccessGraphLoads(1);
			} catch (Exception e) {
				Statistics.INSTANCE.incFailedGraphLoads(1);
			}
		}
	}

	@Override
	public void setup(final Collection<URL> urls) {
		// nothing to do
	}

	@Override
	public void preload() {
		// nothing to do
	}

	@Override
	public void close() throws IOException {		
		try {
			if (graphDb != null) {
				graphDb.shutdown();
			}
		} catch (Throwable t) {
			Throwables.propagateIfInstanceOf(t, IOException.class);
			throw Throwables.propagate(t);
		} finally {
			graphDb = null;
		}
	}	

	public final void restart() {
		// drop current database
		try {
			close();
		} catch (Exception ignore) { }
		// release connector
		connector = null;
		// restart statistics
		Statistics.INSTANCE.initSuccessGraphLoads();
		Statistics.INSTANCE.initFailedGraphLoads();
		// create a new database
		create();
	}

	public @Nullable GraphDatabaseService service() {
		return graphDb;
	}

	public @Nullable GraphConnector getConnector() {
		return connector;
	}

	public void setConnector(final GraphConnector connector) {
		this.connector = checkNotNull(connector, "Uninitialized connector");
	}

	private void create() {
		String storePath = null;
		try {
			final File storeDir = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), "graphdb");
			FileUtils.deleteQuietly(storeDir);
			storeDir.mkdirs();		
			storePath = storeDir.getCanonicalPath();
		} catch (Exception ignore) { }
		checkState(StringUtils.isNotBlank(storePath), "Cannot create the graph store directory");
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(storePath)
				.loadPropertiesFromURL(GraphDatabaseHandler.class.getClassLoader().getResource(NEO4J_PROPERTIES))
				.newGraphDatabase();
	}

}