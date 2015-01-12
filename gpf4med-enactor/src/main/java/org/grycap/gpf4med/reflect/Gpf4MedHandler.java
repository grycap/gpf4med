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

import java.lang.reflect.Method;

import org.grycap.gpf4med.rest.Gpf4MedResource;
import org.grycap.gpf4med.rest.Gpf4MedResourcePartialImpl;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Specialization of the resource handler that overrides some specific behaviors.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedHandler extends AsyncHandler<Gpf4MedResource> {

	private final Gpf4MedResourcePartialImpl underlying = new Gpf4MedResourcePartialImpl();

	public Gpf4MedHandler(final ListeningExecutorService executorService) {
		super(executorService, Gpf4MedResource.class);
	}

	@Override
	protected Object handleInvocation(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		if (undefinedGroup(args)) {
			return method.invoke(underlying, args);			
		}
		return super.handleInvocation(proxy, method, args);
	}	

}