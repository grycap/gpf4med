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

import java.util.ArrayList;
import java.util.Map;

import org.grycap.gpf4med.DocumentManager;
import org.grycap.gpf4med.Group;
import org.grycap.gpf4med.TemplateManager;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.ext.GraphConnectorManager;
import org.grycap.gpf4med.model.Document;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.model.util.AvailableGraphs;
import org.grycap.gpf4med.model.util.AvailableReport;
import org.grycap.gpf4med.model.util.AvailableReports;
import org.grycap.gpf4med.model.util.AvailableTemplate;
import org.grycap.gpf4med.model.util.AvailableTemplates;
import org.grycap.gpf4med.model.util.GraphStatistics;
import org.grycap.gpf4med.model.util.LinkSet;
import org.grycap.gpf4med.model.util.PingResponse;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * Partial implementation of {@link Gpf4MedResource} that provides those methods
 * that do not need a group of resources to be created in the cloud provider. Other
 * methods will fail with and exception.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedResourcePartialImpl implements Gpf4MedResource {

	@Override
	public PingResponse ping() {
		final PingResponse response = new PingResponse();
		response.setResponse(PingResponse.OK_RESPONSE);
		return response;
	}

	@Override
	public AvailableTemplates listTemplates() {
		final AvailableTemplates templates = new AvailableTemplates();
		templates.setTemplates(new ArrayList<AvailableTemplate>());
		final ImmutableCollection<DocumentTemplate> documentTemplates = TemplateManager.INSTANCE.listTemplates();
		if (documentTemplates != null) {
			for (final DocumentTemplate item : documentTemplates) {
				final AvailableTemplate template = new AvailableTemplate();
				template.setIdOntology(item.getIdOntology());
				template.setDescription(item.getDescription());
				template.setCodeValue(item.getContainerTemplate().getConceptName().getCodeValue().toString());
				template.setCodeSchema(item.getContainerTemplate().getConceptName().getCodeSchema().toString());
				template.setCodeMeaning(item.getContainerTemplate().getConceptName().getCodeMeaning());
				template.setCodeMeaning2(item.getContainerTemplate().getConceptName().getCodeMeaning2());
				templates.getTemplates().add(template);
			}
		}
		return templates;
	}

	@Override
	public AvailableReports listReports() {
		final AvailableReports reports = new AvailableReports();
		reports.setReports(new ArrayList<AvailableReport>());
		final ImmutableCollection<Document> documents = DocumentManager.INSTANCE.listDocuments();
		if (documents != null) {
			for (final Document item : documents) {
				final AvailableReport report = new AvailableReport();
				report.setIdOntology(item.getIdOntology());
				report.setIdReport(item.getIdReport());
				report.setIdTrencadisReport(item.getIdTrencadisReport());
				report.setDateStart(item.getDateStart());
				report.setDateEnd(item.getDateEnd());
				reports.getReports().add(report);
			}
		}
		return reports;
	}	

	@Override
	public AvailableReports listReports(int idCenter) {
		final AvailableReports reports = new AvailableReports();
		reports.setReports(new ArrayList<AvailableReport>());
		final ImmutableCollection<Document> documents = DocumentManager.INSTANCE.listDocuments(idCenter);
		if (documents != null) {
			for (final Document item : documents) {
				final AvailableReport report = new AvailableReport();
				report.setIdOntology(item.getIdOntology());
				report.setIdReport(item.getIdReport());
				report.setIdTrencadisReport(item.getIdTrencadisReport());
				report.setDateStart(item.getDateStart());
				report.setDateEnd(item.getDateEnd());
				reports.getReports().add(report);
			}
		}
		return reports;
	}

	@Override
	public AvailableReports listReports(String idOntology) {
		final AvailableReports reports = new AvailableReports();
		reports.setReports(new ArrayList<AvailableReport>());
		final ImmutableCollection<Document> documents = DocumentManager.INSTANCE.listDocuments(idOntology);
		if (documents != null) {
			for (final Document item : documents) {
				final AvailableReport report = new AvailableReport();
				report.setIdOntology(item.getIdOntology());
				report.setIdReport(item.getIdReport());
				report.setIdTrencadisReport(item.getIdTrencadisReport());
				report.setDateStart(item.getDateStart());
				report.setDateEnd(item.getDateEnd());
				reports.getReports().add(report);
			}
		}
		return reports;
	}

	@Override
	public AvailableReports listReports(int idCenter, String idOntology) {
		final AvailableReports reports = new AvailableReports();
		reports.setReports(new ArrayList<AvailableReport>());
		final ImmutableCollection<Document> documents = DocumentManager.INSTANCE.listDocuments(idCenter, idOntology);
		if (documents != null) {
			for (final Document item : documents) {
				final AvailableReport report = new AvailableReport();
				report.setIdOntology(item.getIdOntology());
				report.setIdReport(item.getIdReport());
				report.setIdTrencadisReport(item.getIdTrencadisReport());
				report.setDateStart(item.getDateStart());
				report.setDateEnd(item.getDateEnd());
				reports.getReports().add(report);
			}
		}
		return reports;
	}

	@Override
	public AvailableGraphs listGraphs() {
		final AvailableGraphs graphs = new AvailableGraphs();
		graphs.setPaths(new ArrayList<String>());
		final ImmutableMap<String, GraphConnector> connectors = GraphConnectorManager.INSTANCE.listConnectors();
		if (connectors != null) {			
			for (final Map.Entry<String, GraphConnector> entry : connectors.entrySet()) {
				graphs.getPaths().add(entry.getValue().path());
			}
		}
		return graphs;
	}

	@Override
	public String create(final String graph, final LinkSet reports, final Group group) {
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public void destroy(final Group group) {
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public GraphStatistics getGraphStatistics(final Group group) {
		throw new UnsupportedOperationException("Unsupported operation");
	}


}