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

package org.grycap.gpf4med.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grycap.gpf4med.Group;
import org.grycap.gpf4med.model.util.AvailableGraphs;
import org.grycap.gpf4med.model.util.AvailableReports;
import org.grycap.gpf4med.model.util.AvailableTemplates;
import org.grycap.gpf4med.model.util.GraphStatistics;
import org.grycap.gpf4med.model.util.LinkSet;
import org.grycap.gpf4med.model.util.PingResponse;

/**
 * Gpf4Med REST resource. API version is mandatory in the URL. Implementations must 
 * rely on this small subset of HTTP status codes:
 * <ul>
 *   <li>200 - OK</li>
 *   <li>201 - Created</li>
 *   <li>304 - Not Modified</li>
 *   <li>400 - Bad Request</li>
 *   <li>401 - Unauthorized</li>
 *   <li>403 - Forbidden</li>
 *   <li>404 - Not Found</li>
 *   <li>500 - Internal Server Error</li>
 * </ul>
 * @author Erik Torres <ertorser@upv.es>
 */
@Path("graphrs/v1")
public interface Gpf4MedResource {

	/**
	 * Resource path that includes API version.
	 */
	public final static String RESOURCE_PATH = Gpf4MedResource.class.getAnnotation(Path.class).value();	
	
	/**
	 * Checks service availability: connectivity and correct configuration on the 
	 * server side. Clients must get a {@link PingResponse#OK_RESPONSE} if the 
	 * service is available. Otherwise, a REST fault is raised.
	 * @return {@link PingResponse#OK_RESPONSE} if the service is available, otherwise
	 *         a REST fault is raised.
	 */
	@Path("ping")
	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	PingResponse ping();

	/**
	 * Lists medical reports (e.g. DICOM-SR) available to this service.
	 * @return medical reports available to this service.
	 */
	@Path("list/templates")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableTemplates listTemplates();
	
	/**
	 * Lists medical format templates (e.g. DICOM-SR) available to this service.
	 * @return medical reports available to this service.
	 */
	@Path("list/reports")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableReports listReports();
	
	/**
	 * Lists medical format templates (e.g. DICOM-SR) available to this service
	 * from a given center.
	 * @return medical format templates available to this service.
	 */
	@Path("list/reports/center/{center}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableReports listReports(@PathParam("center") int idCenter);
	
	/**
	 * Lists medical format templates (e.g. DICOM-SR) available to this service
	 * with a given ontology.
	 * @return medical reports available to this service.
	 */
	@Path("list/reports/ontology/{ontology}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableReports listReports(@PathParam("ontology") String idOntology);
	/**
	 * Lists medical format templates (e.g. DICOM-SR) available to this service
	 * from a given center with a given ontology.
	 * @return medical reports available to this service.
	 */
	@Path("list/reports/center/{center}/ontology/{ontology}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableReports listReports(@PathParam("center") int idCenter, @PathParam("ontology") String idOntology);

	/**
	 * Lists graph connectors available to this service.
	 * @return graph connectors available to this service.
	 */
	@Path("list/graphs")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	AvailableGraphs listGraphs();
	
	/**
	 * Creates a new graph with the specified type, loading the reports from their 
	 * location. If the value of the optional group parameter is set, then the graph
	 * is made available for this group. Otherwise, the default group {@link #DEFAULT_GROUP} 
	 * is assumed. Notice that a single instance of the service can manage only one 
	 * graph at the same time. To study more than one graph you must use the enactment 
	 * service and assign a different group per graph.
	 * @param graph type of graph. Must coincide with one of the graph connectors 
	 *        available to this service.
	 * @param reports list of reports (references).
	 * @param group optional group.
	 * @return an optional, informative message about the operation.
	 */
	@Path("create/{graph}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	String create(@PathParam("graph") String graph, LinkSet reports, 
			@DefaultValue(Group.DEFAULT_GROUP) @QueryParam("group") Group group);

	/**
	 * Destroys the graph loaded in this service. When no group is passed, then the 
	 * default group {@link #DEFAULT_GROUP} is assumed.
	 * @param group optional group.
	 */
	@Path("destroy")
	@DELETE
	void destroy(@DefaultValue(Group.DEFAULT_GROUP) @QueryParam("group") Group group);

	/**
	 * Gets the usage statistics about the specified graph. When no group is passed, 
	 * then the default group {@link #DEFAULT_GROUP} is assumed.
	 * @param group optional group.
	 * @return usage statistics about the specified graph.
	 */
	@Path("stats/graph")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	GraphStatistics getGraphStatistics(@DefaultValue(Group.DEFAULT_GROUP) @QueryParam("group") Group group);

}