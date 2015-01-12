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

package org.grycap.gpf4med.data.mock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;

import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.model.Document;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Mocks {@link GraphConnector}.
 * @author Erik Torres <ertorser@upv.es>
 */
@Author(name = "GRyCAP, UPVLC, I3M, ES")
@Version(version = 10000)
@PluginImplementation
public class GraphConnectorMock implements GraphConnector {

	@Override
	public String path() {
		return GraphConnectorResourceMock.PATH_STRING;
	}

	@Override
	public @Nullable String getDescription() {
		return "Mock-up graph connector";
	}

	@Override
	public Class<?> restResourceDefinition() {
		return GraphConnectorResourceMock.class;
	}

	@Override
	public Class<?> restResourceImplementation() {
		return GraphConnectorResourceMockImpl.class;
	}	

	@Override
	public void create() { }

	@Override
	public void clear() { }

	@Override
	public void add(final Document document) {
		checkArgument(document != null, "Uninitialized document");
		final GraphDatabaseService dbService = GraphDatabaseHandler.INSTANCE.service();
		checkState(dbService != null, "Uninitialized graph database service");
	}	

}