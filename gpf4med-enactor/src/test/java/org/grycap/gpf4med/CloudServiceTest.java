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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Iterator;

import org.grycap.gpf4med.cloud.CloudService;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;

/**
 * Tests the cloud service.
 * @author Erik Torres <ertorser@upv.es>
 */
public class CloudServiceTest {

	private Gpf4MedEnactor enactor;

	@Before
	public void setUp() throws Exception {
		final ImmutableList<URL> urls = new ImmutableList.Builder<URL>()
				.add(new URL("file:///opt/gpf4med/etc/gpf4med.xml"))
				.add(new URL("file:///opt/gpf4med/etc/gpf4med-container.xml"))
				.add(new URL("file:///opt/gpf4med/etc/gpf4med-enactor.xml"))
				.build();
		ConfigurationManager.INSTANCE.setup(urls);
		enactor = SingletonService.INSTANCE.service();
	}

	@Test
	public void test() {
		System.out.println("CloudServiceTest.test()");
		try {
			assertThat("enactor is not null", enactor, notNullValue());
			final String group = "unit-testing";
			CloudService.INSTANCE.addServers(group, 1);
			final Iterable<HostAndPort> servers = CloudService.INSTANCE.list(group);
			assertThat("servers is not null", servers, notNullValue());
			final Iterator<HostAndPort> serversIt = servers.iterator();
			assertThat("servers is not empty", serversIt.hasNext());
			while (serversIt.hasNext()) {
				final HostAndPort server = serversIt.next();
				System.out.printf("Server %s%n", server);
			}			

			// TODO
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("CloudServiceTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("CloudServiceTest.test() has finished");
		}
	}

}