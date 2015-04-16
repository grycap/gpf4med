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

import static org.grycap.gpf4med.xml.TemplateXmlBinder.TEMPLATE_XMLB;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.template.ConceptNameTemplate;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.util.TemplateUtils;
import org.grycap.gpf4med.util.TestUtils;
import org.junit.Test;

/**
 * Tests template utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public class TemplateUtilsTest {

	@Test
	public void test() {
		System.out.println("TemplateUtilsTest.test()");
		try {
			// load a test template
			File templateFile = new File(TestUtils.getTestTemplatesDirectoy(), "05_Mammography_BIRADS_5th.xml");
			Template template = TEMPLATE_XMLB.typeFromFile(templateFile);
					
			assertThat("template is not null", template, notNullValue());
			// find BI-RADS 4A
			final ConceptNameTemplate conceptName = new ConceptNameTemplate();
			conceptName.setCODEVALUE("RID36031");
			conceptName.setCODESCHEMA("RADLEX");
			final String meaning = TemplateUtils.getMeaning(conceptName, template, null);
			assertThat("meaning is not null", meaning, notNullValue());
			assertThat("meaning is not empty", StringUtils.isNotBlank(meaning));
			System.out.println(" >> BI_RADS found: " + conceptName.toString() + ", meaning: " + meaning);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("TemplateUtilsTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("TemplateUtilsTest.test() has finished");
		}
	}

}