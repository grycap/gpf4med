/*
 * Copyright 2015 Institute for Molecular Imaging Instrumentation (I3M)
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

package org.grycap.gpf4med.util;

import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.grycap.gpf4med.xml.TemplateXmlBinder.TEMPLATE_XMLB;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.document.Documents;
import org.grycap.gpf4med.model.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.files.TRENCADIS_XML_DICOM_SR_FILE;
import trencadis.middleware.files.TRENCADIS_XML_ONTOLOGY_FILE;
import trencadis.middleware.login.TRENCADIS_SESSION;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_XMLDSR_DOWNLOAD;
import trencadis.middleware.operations.OntologiesServer.TRENCADIS_GET_ALL_ONTOLOGIES;
import trencadis.middleware.operations.OntologiesServer.TRENCADIS_GET_ONTOLOGY;

/**
 * TRENCADIS Utils
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */

public enum TRENCADISUtils {

	INSTANCE;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TRENCADISUtils.class);

	private TRENCADIS_SESSION trencadis_session = createSession();
	
	private Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorages = null;
	
	public TRENCADIS_SESSION getTRENCADISSession() {
		if (trencadis_session.getProxyLifetime() == 0) {
			trencadis_session = createSession();
		}
		return trencadis_session;
	}
	
	public Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> getDicomStorages() {
		return dicomStorages;
	}
	
	private TRENCADIS_SESSION createSession() {
		try {
			//LOGGER.info("Creating new TRENCADIS session");
			return new TRENCADIS_SESSION(
					ConfigurationManager.INSTANCE.getTrencadisConfigFile(),
					ConfigurationManager.INSTANCE.getTrencadisPassword());
		} catch (IOException | InterruptedException e) {
			LOGGER.warn("Can not create TRENCADIS session");
			return null;
		}		
	}
	
	/**
	 * Downloads all reports using TRENCADIS plug-in
	 * 
	 * @param idOntology The ontology of the DICOM-SR to download
	 * @param idCenter Identifier of the hospital replica
	 * @param destination Directory in which will be stored the reports 
	 */
	@SuppressWarnings("unchecked")
	public void downloadAllReports(String idOntology, int idCenter, String destination) {

		Vector<TRENCADIS_XML_DICOM_SR_FILE> v_dsr = null;
		TRENCADIS_XMLDSR_DOWNLOAD get_dsr = null;
		try {
			get_dsr = new TRENCADIS_XMLDSR_DOWNLOAD(trencadis_session, idCenter);
			v_dsr = (Vector<TRENCADIS_XML_DICOM_SR_FILE>)get_dsr.execute();
			for (TRENCADIS_XML_DICOM_SR_FILE dsr : v_dsr) {
				File newDest = new File(destination + File.separator + "centerID_" + idCenter
			  			  			  + File.separator + dsr.getIDOntology());
				newDest.mkdirs();
				Document report = REPORT_XMLB.typeFromXml(dsr.getContents());
				REPORT_XMLB.typeToFile(report, new File(newDest + File.separator 
									 + report.getIDReport() + ".xml"));
			}

		} catch (Exception e) {
			LOGGER.error("Can not download the reports for ontology " + idOntology);
		}

	}
	
	/**
	 * Downloads a report using TRENCADIS plug-in
	 * 
	 * @param idCenter Identifier of the hospital replica
	 * @param idOntology The ontology of the DICOM-SR to download
	 * @param idReport The identifier of the DICOM-SR report to download
	 * @param destination Directory in which the report will be stored
	 */
	public void downloadReport(int idCenter, String idOntology,
			String idReport, String destination) {

		TRENCADIS_XMLDSR_DOWNLOAD get_dsr = null;
		TRENCADIS_XML_DICOM_SR_FILE dsr = null;
		try {
			get_dsr = new TRENCADIS_XMLDSR_DOWNLOAD(trencadis_session, idCenter,
					idReport, idOntology);
			dsr = (TRENCADIS_XML_DICOM_SR_FILE) get_dsr.execute();
			File newDest = new File(destination + File.separator + "centerID_" + idCenter
					  			  + File.separator + "ontology_" + dsr.getIDOntology());
			newDest.mkdirs();
			Document report = REPORT_XMLB.typeFromXml(dsr.getContents());
			REPORT_XMLB.typeToFile(report, new File(newDest + File.separator 
												  + report.getIDReport() + ".xml"));
		} catch (Exception e) {
			LOGGER.error("Can not download the report " + idReport);
		}

	}
	
	/**
	 * Downloads from a backend a report
	 * 
	 * @param backend The backend in which is stored the reports
	 * @param idReport The identifier of the DICOM-SR report to download
	 * @param destination Directory in which the report will be stored
	 */
	public void downloadReport(BackEnd backend, String centerName, String idReport, String destination) {
		try {
			String reportData = backend.xmlGetDICOMSRFile(idReport,
					trencadis_session.getX509VOMSCredential());
			if (reportData == null)
				LOGGER.trace("Report not downloaded");
			Document report = REPORT_XMLB.typeFromXml(reportData);
			
			File newDest = new File(destination, centerName + "_files"
									+ File.separator + "ontology_" + report.getIDOntology());
			newDest.mkdirs();
			REPORT_XMLB.typeToFile(report, new File(newDest + File.separator
					+ idReport + ".xml"));
		} catch (Exception e) {
			LOGGER.error("Can not download the report " + idReport);
		}
	}
	
	/**
	 * Downloads from a backend the reports specified
	 * 
	 * @param backend The backend in which is stored the reports
	 * @param idReport The identifier of the DICOM-SR report to download
	 * @param destination Directory in which the report will be stored
	 * @throws Exception 
	 */
	@SuppressWarnings("null")
	public boolean downloadReports(BackEnd backend, String idReports, String destination) throws Exception {
		try {
			String reportsData = backend.xmlGetAllDICOMSRFiles(idReports,
					trencadis_session.getX509VOMSCredential());
			if (reportsData != null || reportsData.length() > 0) {
				Documents reports = REPORT_XMLB.typeFromXml(reportsData);
				for (Document report : reports.getDICOMSR()) {
					File newDest = new File(destination, "ontology_" + report.getIDOntology());
					newDest.mkdirs();
					REPORT_XMLB.typeToFile(report, new File(newDest + File.separator
							+ report.getIDTRENCADISReport() + ".xml"));
				}
				return true;
			} else {
				LOGGER.error("Reports not downloaded");
				return false;
			}
			
		} catch (IOException e1) {
			throw new IOException();
		} catch (Exception e2) {
			throw new Exception();
		}
		
	}
	
	/**
	 * Downloads a ontology using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param idOdontology The ontology of the DICOM-SR to download
	 * @param destination Directory in which the ontology will be stored
	 */
	public void downloadOntology(String idOdontology, String destination) {

		TRENCADIS_GET_ONTOLOGY get_onto = null;
		TRENCADIS_XML_ONTOLOGY_FILE ontology = null;
		try {
			get_onto = new TRENCADIS_GET_ONTOLOGY(trencadis_session, idOdontology);
			ontology = get_onto.execute();
			Template template = TEMPLATE_XMLB.typeFromXml(ontology.getContents());
			TEMPLATE_XMLB.typeToFile(template, new File(destination + File.separator
													  + ontology.getIDOntology() + "_"
													  + ontology.getDescription().replaceAll(" ", "_") + ".xml"));
		} catch (Exception e) {
			LOGGER.error("Can not download the ontology " + idOdontology);
		}

	}
	
	/**
	 * Downloads all ontologies using TRENCADIS plug-in
	 * 
	 * @param destination Directory in which the ontologies will be stored
	 */
	public void downloadAllOntologies(String destination) {

		TRENCADIS_GET_ALL_ONTOLOGIES get_onto = null;
		Vector<TRENCADIS_XML_ONTOLOGY_FILE> v_ontologies = null;
		try {
			get_onto = new TRENCADIS_GET_ALL_ONTOLOGIES(trencadis_session);
			v_ontologies = get_onto.execute();
			for (TRENCADIS_XML_ONTOLOGY_FILE ontology : v_ontologies) {
				Template template = TEMPLATE_XMLB.typeFromXml(ontology.getContents());
				TEMPLATE_XMLB.typeToFile(template, new File(destination + File.separator
														  + ontology.getIDOntology() + "_"
														  + ontology.getDescription().replaceAll(" ", "_") + ".xml"));
			}
		} catch (Exception e) {
			LOGGER.error("Can not download the ontologies");
		}

	}	
	
	/**
	 * Gets all ID of the DICOM-SR reports using TRENCADIS plug-in 
	 * 
	 */
	public void getReportsID() {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(trencadis_session);
			dicomStorages = getIDs.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports");
		}
	}
	
	/**
	 * Gets all ID of the DICOM-SR reports from a given center
	 * using TRENCADIS plug-in 
	 * 
	 * @param idCenter Identifier of the hospital replica
	 */
	public void getReportsID(int idCenter) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(trencadis_session, idCenter);
			dicomStorages = getIDs.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports from center " + idCenter);
		}
	}
	
	/**
	 * Gets all ID of the DICOM-SR reports with a given ontology using TRENCADIS plug-in
	 * 
	 * @param idOntology The ontology of the DICOM-SR to download
	 */
	public void getReportsID(String idOntology) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDsOnto = null;
		try {			
			getIDsOnto = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(trencadis_session, idOntology);
			dicomStorages = getIDsOnto.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports of the ontology " + idOntology);
		}
	}

	/**
	 * Gets all ID of the DICOM-SR reports from a given center
	 * using TRENCADIS plug-in 
	 * 
	 * @param idCenter Identifier of the hospital replica
	 * @param idOntology The ontology of the DICOM-SR to download
	 * 
	 */
	public void getReportsID(int idCenter, String idOntology) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(trencadis_session,
					idCenter, idOntology);
			dicomStorages = getIDs.getDICOMStorageIDS();
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports of the ontology "
					+ idOntology + " from center " + idCenter);
		}
	}
	
}
