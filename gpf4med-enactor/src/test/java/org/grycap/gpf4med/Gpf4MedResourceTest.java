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

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.grycap.gpf4med.reflect.ProxyFactory.newGpf4MedResourceProxy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.grycap.gpf4med.model.util.AvailableGraphs;
import org.grycap.gpf4med.model.util.AvailableTemplates;
import org.grycap.gpf4med.model.util.LinkSet;
import org.grycap.gpf4med.model.util.PingResponse;
import org.grycap.gpf4med.rest.Gpf4MedResource;
import org.junit.Test;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Tests enactment service RESTful resource.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedResourceTest extends JerseyTest {

	private final ListeningExecutorService executorService = listeningDecorator(newCachedThreadPool());

	@Override
	protected Application configure() {		
		final Gpf4MedResource gpf4medProxy = newGpf4MedResourceProxy(executorService);		
		return new ResourceConfig().registerInstances(gpf4medProxy)
				.register(MoxyJsonFeature.class);		
	}

	@Test
	public void test() {
		System.out.println("Gpf4MedResourceTest.test()");
		try {
			// test ping
			String path = Gpf4MedResource.RESOURCE_PATH + "/ping";
			System.out.println(" >> Testing operation: " + path + " on base URI: " + getBaseUri());
			final PingResponse pingResponse = target(path).request().get(PingResponse.class);
			assertThat("ping response is not null", pingResponse, notNullValue());
			assertThat("expected ping response", pingResponse.getResponse(), equalTo(PingResponse.OK_RESPONSE));
			// test templates listing
			path = Gpf4MedResource.RESOURCE_PATH + "/list/templates";
			System.out.println(" >> Testing operation: " + path + " on base URI: " + getBaseUri());
			final AvailableTemplates templates = target(path).request().get(AvailableTemplates.class);
			assertThat("list templates response is not null", templates, notNullValue());
			assertThat("templates list is not empty", !templates.getTemplates().isEmpty());
			// test plug-ins listing
			path = Gpf4MedResource.RESOURCE_PATH + "/list/graphs";
			System.out.println(" >> Testing operation: " + path + " on base URI: " + getBaseUri());
			final AvailableGraphs graphs = target(path).request().get(AvailableGraphs.class);
			assertThat("list graphs response is not null", graphs, notNullValue());
			assertThat("graphs list is not empty", !graphs.getPaths().isEmpty());
			// test create graph			
			path = Gpf4MedResource.RESOURCE_PATH + "/create/graph-base";
			System.out.println(" >> Testing operation: " + path + " on base URI: " + getBaseUri());
			final LinkSet linkSet = new LinkSet();			
			final String createResponse = target(path).request().post(Entity.entity(linkSet, MediaType.APPLICATION_JSON_TYPE), String.class);
			assertThat("create graph response is not null", createResponse, notNullValue());
			assertThat("create graph response is not empty", StringUtils.isNotBlank(createResponse));




			// TODO
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("Gpf4MedResourceTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("Gpf4MedResourceTest.test() has finished");
		}

	}

}