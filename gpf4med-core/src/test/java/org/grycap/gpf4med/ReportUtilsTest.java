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

package org.grycap.gpf4med;

import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.util.Id;
import org.grycap.gpf4med.util.ReportUtils;
import org.grycap.gpf4med.util.TestUtils;
import org.junit.Test;

/**
 * Tests template utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ReportUtilsTest {

	@Test
	public void test() {
		System.out.println("ReportUtilsTest.test()");
		try {
			// load a test template
			File reportFile = new File(TestUtils.getTestReportsDirectoy(), "MAMO_1_500.xml");
			Document report = REPORT_XMLB.typeFromFile(reportFile);
					
			assertThat("report is not null", report, notNullValue());
			
			//find "Skin retraction"
			final ConceptName conceptName = new ConceptName();
			conceptName.setCODEVALUE("RID34383");
			conceptName.setCODESCHEMA("RADLEX");
			final String meaning = ReportUtils.getMeaning(conceptName, report);
			assertThat("meaning is not null", meaning, notNullValue());
			assertThat("meaning is not empty", StringUtils.isNotBlank(meaning));
			
			System.out.println(" >> Report Identifier found: " + Id.getId(conceptName) + ", meaning: " + meaning);
			
			String tree_mamo = ReportUtils.getStringReport(report);
			FileUtils.writeStringToFile(new File("/opt/trencadis/tree_mamo.txt"), tree_mamo);
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ReportUtilsTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("ReportUtilsTest.test() has finished");
		}
	}

}