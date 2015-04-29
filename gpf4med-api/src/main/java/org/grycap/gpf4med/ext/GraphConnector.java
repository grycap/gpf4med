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

package org.grycap.gpf4med.ext;

import javax.annotation.Nullable;

import net.xeoh.plugins.base.Plugin;

import org.grycap.gpf4med.model.document.Document;

/**
 * A connector that can be plugged into the DICOM graph store framework.
 * @author Erik Torres <ertorser@upv.es>
 */
public interface GraphConnector extends Plugin {	

	/**
	 * The path name where RESTful resource of this connector is made available. It must 
	 * coincide with the {@code @Path} of the RESTful resource and it must be unique.
	 * @return path name of the RESTful resource. 
	 */
	String path();
	
	/**
	 * Interface that contains the RESTful resource exposed where the operations of this 
	 * connector are defined.
	 * @return the interface that defines the RESTful resource of this connector.
	 */
	Class<?> restResourceDefinition();
	
	/**
	 * Class that contains the RESTful resource exposed where the operations of this 
	 * connector are implemented.
	 * @return the class that implements the RESTful resource of this connector.
	 */
	Class<?> restResourceImplementation();
	
	/**
	 * Gets an optional description of the connector.
	 * @return A description of the connector if exists or {@code null}.
	 */
	@Nullable String getDescription();	

	/**
	 * Creates a new graph.
	 */
	void create();
	
	/**
	 * Releases the graph and the resources associated to the graph, removing all loaded 
	 * objects from the graph.
	 */
	void clear();

	/**
	 * Adds a new document to the current graph.
	 * @param document the document to be added.
	 */
	void add(Document document);

}