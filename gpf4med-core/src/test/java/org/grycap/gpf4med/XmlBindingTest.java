package org.grycap.gpf4med;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.getProperty;
import static java.nio.charset.Charset.forName;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.io.FilenameUtils.concat;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.threads.ImportReportsGroupTask;
import org.grycap.gpf4med.util.TRENCADISUtils;
import org.grycap.gpf4med.xml.report.ReportType;
import org.grycap.gpf4med.xml.report.ReportsType;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import trencadis.infrastructure.services.DICOMStorage.impl.wrapper.xmlOutputDownloadAllReportsID.DICOM_SR_ID;
import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.login.TRENCADIS_SESSION;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;

/**
 * Unit test for the XML binding classes supporting this application.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XmlBindingTest {

//	private static final File TEST_OUTPUT_DIR = new File(concat(getProperty("java.io.tmpdir"),
//			XmlBindingTest.class.getSimpleName() + "_test" ));
	
	private static final File TEST_OUTPUT_DIR = new File(concat(getProperty("java.io.tmpdir"),
			"xml_binding_test" ));

	@Before
	public void setUp() {
		deleteQuietly(TEST_OUTPUT_DIR);
	}
/*
	@After
	public void cleanUp() throws IOException {
		deleteQuietly(TEST_OUTPUT_DIR);		
	}*/

	/**
	 * General tests of the report binding.
	 */
	@Test
	public void test1_ReportBinding() {
		System.out.println("ReportBinding()");
		try {
			// create test dataset
			
			//final String reportsXml = "<DICOM_REPORTS><DICOM_SR IDOntology=\"5\" IDReport=\"ds1-4\" DateTimeStart=\"24/11/2014 18:41:59\" DateTimeEnd=\"24/11/2014 18:45:00\" IDTRENCADISReport=\"1D30DF20-C70A-11E4-8D35-E68FFB6C6EED\"><CONTAINER>S30XgLAdgdQ8E1l3yOi;QRF0C0rj0qoo,w3Dow7b jOxZWvLkMhoP8NA3ski;024R;k1S4TOzND</CONTAINER><DICOM_SR IDOntology=\"6\" IDReport=\"ds1-5\" DateTimeStart=\"24/11/2014 18:41:59\" DateTimeEnd=\"24/11/2014 18:45:00\" IDTRENCADISReport=\"8D30DF20-C70A-11E4-8D35-E68FFB6C6EEF\"><CONTAINER>S30XgLAdgdQ8E1l3yOi;QRF0C0rj0qoo,w3Dow7b jOxZWvLkMhoP8NA3ski;024R;k1S4TOzNX</CONTAINER></DICOM_SR></DICOM_REPORTS>";			
		
			TRENCADIS_SESSION session = new TRENCADIS_SESSION(ConfigurationManager.INSTANCE.getTrencadisConfigFile(),
					ConfigurationManager.INSTANCE.getTrencadisPassword());
			TRENCADISUtils.getReportsID(session, 1, "6");
			
			Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorage = TRENCADISUtils.getDicomStorage();
			String ids = "";
			BackEnd backend = null;
			String centerName = null;
			if (dicomStorage != null) {
				for (TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorageIDS : dicomStorage) {
					centerName = dicomStorageIDS.getCenterName();
					backend = new BackEnd(dicomStorageIDS.getBackend().toString());
					for (DICOM_SR_ID id : dicomStorageIDS.getDICOM_DSR_IDS()) {
						ids += id.getValue() + ",";
					}
				}
				
			}
			ids = ids.subSequence(0, ids.length() - 1).toString();
			String reportsXml = backend.xmlGetAllDICOMSRFiles(ids, session.getX509VOMSCredential());	
			
			final File reportsFile = new File(TEST_OUTPUT_DIR, "reports.xml");
			write(reportsFile, reportsXml, forName("UTF-8"));
			assertThat("reports file is not null", reportsFile, notNullValue());
			String payload = readFileToString(reportsFile);
			assertThat("reports file content is not null", payload, notNullValue());
			assertThat("reports file is not empty", isNotBlank(payload), equalTo(true));
			
			final ReportsType reports = REPORT_XMLB.typeFromFile(reportsFile);
			checkState(reports != null, "Expected reports set XML, but no content read from downloaded file");
			if (reports.getDICOMSR() != null) {
				final List<ReportType> reportList = reports.getDICOMSR();
				for (final ReportType report : reportList) {						
					final String id = report.getIDReport();
					if (id != null) {
						final File file = new File(TEST_OUTPUT_DIR, id + ".xml");							
						REPORT_XMLB.typeToFile(report, file);
						//files.add(file.getCanonicalPath());
					} else {
						System.out.println("Ignoring malformed report (id not found) in backend response");
					}
				}
			} else {
				System.out.println("Ignoring malformed report (report list not found) in backend response");
			}
			
			/*
			// test set unmarshalling
			final ReportsType reports = REPORT_XMLB.typeFromFile(reportsFile);
			assertThat("reports is not null", reports, notNullValue());
			// uncomment for additional output
			System.out.println(" >> REPORTS: " + reports.toString());

			// test set marshalling
			final File reportsFile2 = new File(TEST_OUTPUT_DIR, "reports2.xml");
			REPORT_XMLB.typeToFile(reports, reportsFile2);
			assertThat("created reports file is not null", reportsFile2, notNullValue());
			payload = readFileToString(reportsFile2);
			assertThat("created reports file content is not null", payload, notNullValue());
			assertThat("created reports file is not empty", isNotBlank(payload), equalTo(true));
			// uncomment for additional output
			System.out.println(" >> CREATED REPORTS XML: " + payload);

			// test individual report marshalling
			final File reportFile = new File(TEST_OUTPUT_DIR, "report.xml");
			REPORT_XMLB.typeToFile(reports.getDICOMSR().get(0), reportFile);
			assertThat("created report file is not null", reportFile, notNullValue());
			payload = readFileToString(reportFile);
			assertThat("created report file content is not null", payload, notNullValue());
			assertThat("created report file is not empty", isNotBlank(payload), equalTo(true));
			// uncomment for additional output
			System.out.println(" >> CREATED REPORT XML: " + payload);

			// test individual report unmarshalling
			final ReportType report = REPORT_XMLB.typeFromFile(reportFile);
			assertThat("report is not null", report, notNullValue());
			// uncomment for additional output
			System.out.println(" >> REPORT: " + report);
			*/
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ReportBinding() failed: " + e.getMessage());
		} finally {			
			System.out.println("ReportBinding() has finished");
		}
	}

}