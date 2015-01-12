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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.grycap.gpf4med.model.util.PingResponse;
import org.grycap.gpf4med.rest.Gpf4MedResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Gpf4Med resource.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedResourceTest {

	private Gpf4MedService service;
	private WebTarget target;

	@Before
	public void setUp() throws Exception {
		// start the service
		service = SingletonService.INSTANCE.service();
		// create the client
		final Client client = ClientBuilder.newBuilder().register(MoxyJsonFeature.class).build();
		// configure Web target
		target = client.target(service.getBaseUri());
	}

	@After
	public void tearDown() throws Exception {
		if (service != null && service.isRunning()) {
			service.stopAsync().awaitTerminated();
		}
	}

	/**
	 * Test to see that the message {@link PingResponse#OK_RESPONSE} is sent in the response.
	 */
	@Test
	public void test() {
		System.out.println("Gpf4MedResourceTest.test()");
		// test ping
		final PingResponse response = target.path(Gpf4MedResource.RESOURCE_PATH).path("ping")
				.request(MediaType.APPLICATION_JSON).get(PingResponse.class);		
		assertThat(response.getResponse(), equalTo(PingResponse.OK_RESPONSE));
	}

}