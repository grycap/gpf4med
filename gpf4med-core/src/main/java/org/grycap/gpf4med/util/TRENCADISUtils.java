package org.grycap.gpf4med.util;

import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.files.TRENCADIS_XML_DICOM_SR_FILE;
import trencadis.middleware.files.TRENCADIS_XML_ONTOLOGY_FILE;
import trencadis.middleware.login.TRENCADIS_SESSION;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_XMLDSR_DOWNLOAD;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_XMLDSR_DOWNLOAD_ALL;
import trencadis.middleware.operations.OntologiesServer.TRENCADIS_GET_ALL_ONTOLOGIES;
import trencadis.middleware.operations.OntologiesServer.TRENCADIS_GET_ONTOLOGY;

/**
 * 
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */

public class TRENCADISUtils {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(TRENCADISUtils.class);
	
	private static Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorage = null;

	public static Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> getDicomStorage() {
		return dicomStorage;
	}

	/**
	 * Downloads all reports using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param idOntology The ontology of the DICOM-SR to download
	 * @param idCenter Identifier of the hospital replica
	 * @param destination Directory in which will be stored the reports 
	 */
	public static void downloadAllReports(TRENCADIS_SESSION session,
			String idOntology, int idCenter, String destination) {

		Vector<TRENCADIS_XML_DICOM_SR_FILE> v_dsr = null;
		TRENCADIS_XMLDSR_DOWNLOAD_ALL get_dsr = null;
		try {
			get_dsr = new TRENCADIS_XMLDSR_DOWNLOAD_ALL(session, idCenter);
			v_dsr = get_dsr.execute();
			for (TRENCADIS_XML_DICOM_SR_FILE dsr : v_dsr) {
				FileUtils.writeStringToFile(new File(destination + File.separator
						+ dsr.getIDReport() + ".xml"), dsr.getContents());
			}

		} catch (Exception e) {
			LOGGER.error("Can not download the reports for ontology " + idOntology);
		}

	}
	
	/**
	 * Downloads a report using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param idOntology The ontology of the DICOM-SR to download
	 * @param idReport The identifier of the DICOM-SR report to download
	 * @param idCenter Identifier of the hospital replica
	 * @param destination Directory in which the report will be stored
	 */
	public static void downloadReport(TRENCADIS_SESSION session,
			String idOntology, String idReport, int id_center,
			String destination) {

		TRENCADIS_XMLDSR_DOWNLOAD get_dsr = null;
		TRENCADIS_XML_DICOM_SR_FILE dsr = null;
		try {
			get_dsr = new TRENCADIS_XMLDSR_DOWNLOAD(session, idOntology,
					idReport, id_center);
			dsr = get_dsr.execute();
			FileUtils.writeStringToFile(
					new File(destination + File.separator + dsr.getIDOntology() + "_"
							+ dsr.getIDReport() + ".xml"), dsr.getContents());
		} catch (Exception e) {
			LOGGER.error("Can not download the report " + idReport);
		}

	}
	
	/**
	 * Downloads from a backend a report
	 * 
	 * @param session TRENCADIS session
	 * @param backend The backend in which is stored the reports
	 * @param idReport The identifier of the DICOM-SR report to download
	 * @param destination Directory in which the report will be stored
	 */
	public static void downloadReport(TRENCADIS_SESSION session,
			BackEnd backend, String idReport, String destination) {
		try {
			String report = backend.xmlGetDICOMSRFile(idReport,
					session.getX509VOMSCredential());
			FileUtils.writeStringToFile(new File(destination + File.separator
					+ idReport + ".xml"), report);
		} catch (Exception e) {
			LOGGER.error("Can not download the report " + idReport);
		}
	}
	
	/**
	 * Downloads a ontology using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param idOdontology The ontology of the DICOM-SR to download
	 * @param destination Directory in which the ontology will be stored
	 */
	public static void downloadOntology(TRENCADIS_SESSION session,
			String idOdontology, String destination) {

		TRENCADIS_GET_ONTOLOGY get_onto = null;
		TRENCADIS_XML_ONTOLOGY_FILE ontology = null;
		try {
			get_onto = new TRENCADIS_GET_ONTOLOGY(session, idOdontology);
			ontology = get_onto.execute();
			FileUtils.writeStringToFile(new File(destination + File.separator
					+ idOdontology + ".xml"), ontology.getContents());
		} catch (Exception e) {
			LOGGER.error("Can not download the ontology");
		}

	}
	
	/**
	 * Downloads all ontologies using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param destination Directory in which the ontologies will be stored
	 */
	public static void downloadAllOntologies(TRENCADIS_SESSION session,
			String destination) {

		TRENCADIS_GET_ALL_ONTOLOGIES get_onto = null;
		Vector<TRENCADIS_XML_ONTOLOGY_FILE> v_ontologies = null;
		try {
			get_onto = new TRENCADIS_GET_ALL_ONTOLOGIES(session);
			v_ontologies = get_onto.execute();
			for (TRENCADIS_XML_ONTOLOGY_FILE ontology : v_ontologies) {
				FileUtils.writeStringToFile(new File(destination + File.separator
						+ ontology.getIDOntology() + ".xml"),
						ontology.getContents());
			}
		} catch (Exception e) {
			LOGGER.error("Can not download the ontologies");
		}

	}	
	
	/**
	 * Gets all ID of the DICOM-SR reports using TRENCADIS plug-in 
	 * 
	 * @param session TRENCADIS session
	 */
	public static void getReportsID(TRENCADIS_SESSION session) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(session);
			dicomStorage = getIDs.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports");
		}
	}
	
	/**
	 * Gets all ID of the DICOM-SR reports from a given center
	 * using TRENCADIS plug-in 
	 * 
	 * @param session TRENCADIS session
	 * @param idCenter Identifier of the hospital replica
	 */
	public static void getReportsID(TRENCADIS_SESSION session, int idCenter) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(session, idCenter);
			dicomStorage = getIDs.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports from center " + idCenter);
		}
	}
	
	/**
	 * Gets all ID of the DICOM-SR reports with a given ontology using TRENCADIS plug-in
	 * 
	 * @param session TRENCADIS session
	 * @param idOntology The ontology of the DICOM-SR to download
	 */
	public static void getReportsID(TRENCADIS_SESSION session, String idOntology) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDsOnto = null;
		try {			
			getIDsOnto = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(session, idOntology);
			dicomStorage = getIDsOnto.getDICOMStorageIDS();			
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports of the ontology " + idOntology);
		}
	}

	/**
	 * Gets all ID of the DICOM-SR reports from a given center
	 * using TRENCADIS plug-in 
	 * 
	 * @param session TRENCADIS session
	 * @param idCenter Identifier of the hospital replica
	 * @param idOntology The ontology of the DICOM-SR to download
	 * 
	 */
	public static void getReportsID(TRENCADIS_SESSION session, int idCenter, String idOntology) {

		TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS getIDs = null;
		try {
			getIDs = new TRENCADIS_STORAGE_BROKER_RETRIEVE_IDS(session,
					idCenter, idOntology);
			dicomStorage = getIDs.getDICOMStorageIDS();
		} catch (Exception e) {
			LOGGER.error("Can not get the IDs of all reports of the ontology "
					+ idOntology + " from center " + idCenter);
		}
	}
	
}
