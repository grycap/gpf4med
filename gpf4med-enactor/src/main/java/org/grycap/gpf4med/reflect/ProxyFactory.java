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

package org.grycap.gpf4med.reflect;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static org.glassfish.jersey.client.proxy.WebResourceFactory.newResource;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.grycap.gpf4med.rest.Gpf4MedResource;

import com.google.common.reflect.Reflection;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Creates proxy-based consumers.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ProxyFactory {

	/**
	 * Creates a proxy to the {@link Gpf4MedResource} resource that internally maps method 
	 * calls to a set of replicas in the available servers. Exceptionally, some of the methods
	 * can be attended by a local instance of the resource (e.g. methods that list the status
	 * of the service).
	 * @param executorService the pool of threads that will be used to execute the methods.
	 * @return a proxy to the {@link Gpf4MedResource} resource.
	 */
	public static Gpf4MedResource newGpf4MedResourceProxy(final ListeningExecutorService executorService) {
		return Reflection.newProxy(Gpf4MedResource.class, new Gpf4MedHandler(executorService));
	}
	
	/**
	 * Creates a proxy to a resource that internally maps the method calls to a set of
	 * replicas in the available servers.
	 * @param type the interface where the resource is defined, including REST annotations.
	 * @param executorService the pool of threads that will be used to execute the methods.
	 * @return a proxy to the resource.
	 */
	public static <T> T newResourceProxy(final Class<T> type, final ListeningExecutorService executorService) {
		checkArgument(type != null, "Uninitialized type");
		checkArgument(type.isAnnotationPresent(Path.class), "No @Path annotation found, type must be a REST resource");
		return Reflection.newProxy(type, new AsyncHandler<T>(executorService, type));
	}
	
	/**
	 * Creates a proxy-based RESTful client to interact with the specified resource. For example:
	 * <ul>
	 *   <li>Address: {@code http://hostname/webapp/resource}</li>
	 *   <ul>
	 *     <li>Base URL: {@code http://hostname/webapp}</li>
	 *     <li>Path: {@code resource}</li>
	 *   </ul>
	 * </ul>
	 * @param baseURL the base URL of the service.
	 * @param path path to the specific resource.
	 * @param type the interface where the resource is defined, including REST annotations.
	 * @return a proxy to the resource.
	 */
	public static <T> T newResourceProxyClient(final String baseURL, final String path, final Class<T> type) {
		final Client client = newClient();
		final WebTarget target = client.target(baseURL).path(path);
		return newResource(type, target);
	}	

}