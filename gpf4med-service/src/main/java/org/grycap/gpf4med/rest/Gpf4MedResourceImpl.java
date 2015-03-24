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

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.DocumentManager;
import org.grycap.gpf4med.Group;
import org.grycap.gpf4med.Statistics;
import org.grycap.gpf4med.StudyManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Gpf4Med REST resource implementation. This class does not contain any REST annotations.
 * For REST annotations you should revise the interface {@link Gpf4MedResource}.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedResourceImpl implements Gpf4MedResource {

	private final static Logger LOGGER = LoggerFactory.getLogger(Gpf4MedResource.class);

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
		checkArgument(StringUtils.isNotBlank(graph), "Uninitialized or invalid graph");
		checkArgument(reports != null, "Uninitialized reports");
		final ImmutableList.Builder<URL> builder = new ImmutableList.Builder<URL>();
		if (reports.getLinks() != null) {
			for (final String link : reports.getLinks()) {
				try {
					builder.add(new URL(link));
				} catch (Exception ignore) {
					LOGGER.info("Ignoring invalid link: " + link);
				}
			}
		}
		StudyManager.INSTANCE.create(graph, builder.build());
		return "New " + graph + " created, report load will start in a few seconds";
	}

	@Override
	public void destroy(final Group group) {
		StudyManager.INSTANCE.destroy();
	}

	@Override
	public GraphStatistics getGraphStatistics(final Group group) {
		final GraphStatistics statistics = new GraphStatistics();
		statistics.setTotalSubmitted(Statistics.INSTANCE.getTotalSubmitted());
		statistics.setSuccessDownloads(Statistics.INSTANCE.getSuccessDownloads());
		statistics.setFailedDownloads(Statistics.INSTANCE.getFailedDownloads());
		statistics.setSuccessParses(Statistics.INSTANCE.getSuccessParses());
		statistics.setFailedParses(Statistics.INSTANCE.getFailedParses());
		statistics.setSuccessGraphLoads(Statistics.INSTANCE.getSuccessGraphLoads());
		statistics.setFailedGraphLoads(Statistics.INSTANCE.getFailedGraphLoads());
		return statistics;
	}

}