package org.grycap.gpf4med;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.util.TestUtils;
import org.grycap.gpf4med.xml.report.ReportType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Unit test for the XML binding classes supporting this application.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XmlBindingTest {

	@Test
	public void test1_ReportBinding() {
		System.out.println("ReportBinding()");
		try {
			final File dir = TestUtils.getTestDirectoy();
			final File reportFile = FileUtils.getFile(dir, "MAMO_1_500.xml");

			// Report marshalling
			assertThat("created report file is not null", reportFile, notNullValue());
			String payload = readFileToString(reportFile);
			assertThat("created report file content is not null", payload, notNullValue());
			assertThat("created report file is not empty", isNotBlank(payload), equalTo(true));
			// uncomment for additional output
			//System.out.println(" >> CREATED REPORT XML: " + payload);
			
			// Report unmarshalling
			final ReportType report = REPORT_XMLB.typeFromFile(reportFile);
			assertThat("report is not null", report, notNullValue());
			// uncomment for additional output
			//System.out.println(" >> REPORT: " + report);
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ReportBinding() failed: " + e.getMessage());
		} finally {			
			System.out.println("ReportBinding() has finished");
		}
	}

}