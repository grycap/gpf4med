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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.ConceptName.CodeSchema;
import org.grycap.gpf4med.model.ConceptName.CodeValue;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.util.TemplateUtils;
import org.grycap.gpf4med.util.TestUtils;
import org.junit.Test;

import com.google.common.collect.ImmutableCollection;

/**
 * Tests template utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public class TemplateUtilsTest {

	@Test
	public void test() {
		System.out.println("TemplateUtilsTest.test()");
		try {
			// load default templates
			TestUtils.getTemplateFiles();
			final ImmutableCollection<DocumentTemplate> templates = TemplateManager.INSTANCE.listTemplates();
			assertThat("template list is not null", templates, notNullValue());
			assertThat("template list is not empty", !templates.isEmpty());
			
			DocumentTemplate template = null;
			for (final DocumentTemplate item : templates) {
				if (new Integer(5).equals(item.getIdOntology())) {
					template = item;
				}
			}
			assertThat("template is not null", template,notNullValue());
			// find BI-RADS 4A
			final CodeValue value = new CodeValue();
			value.setValue("RID36031");
			final ConceptName conceptName = new ConceptName();
			conceptName.setCodeSchema(CodeSchema.RADLEX);
			conceptName.setCodeValue(value);
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