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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import net.xeoh.plugins.base.util.uri.ClassURI;

import org.grycap.gpf4med.data.mock.GraphConnectorMock;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.ext.GraphConnectorInformation;
import org.grycap.gpf4med.ext.GraphConnectorManager;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the graph connector manager.
 * @author Erik Torres <ertorser@upv.es>
 */
public class GraphConnectorManagerTest {

	@Test
	public void test() {
		System.out.println("GraphConnectorManagerTest.test()");
		try {
			GraphConnectorManager.INSTANCE.getPluginManager().addPluginsFrom(new ClassURI(GraphConnectorMock.class).toURI());
			final ImmutableMap<String, GraphConnector> connectors = GraphConnectorManager.INSTANCE.listConnectors();
			assertThat("connectors is not null", connectors, notNullValue());
			assertThat("connectors is not empty", !connectors.isEmpty());
			final ImmutableList<GraphConnectorInformation> information = GraphConnectorManager.INSTANCE.getConnectorsInformation();
			assertThat("connectors information is not null", information, notNullValue());
			assertThat("connectors information is not empty", !information.isEmpty());
			/* uncomment for additional output
			for (final GraphConnectorInformation item : information) {
				System.out.println(" >> Connector: " + item.toString());
			} */
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("GraphConnectorManagerTest.test() failed: " + e.getMessage());
		} finally {            
			System.out.println("GraphConnectorManagerTest.test() has finished");
		}		
	}

}