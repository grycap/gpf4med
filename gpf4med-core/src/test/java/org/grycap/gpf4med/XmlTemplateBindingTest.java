package org.grycap.gpf4med;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.grycap.gpf4med.xml.TemplateXmlBinder.TEMPLATE_XMLB;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.util.TestUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Unit test for the XML binding classes supporting this application.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XmlTemplateBindingTest {

	@Test
	public void test1_templateBinding() {
		System.out.println("TemplateBinding()");
		try {
			final File dir = TestUtils.getTestTemplatesDirectoy();
			final File templateFile = FileUtils.getFile(dir, "05_Mammography_BIRADS_5th.xml");

			// template marshalling
			assertThat("created template file is not null", templateFile, notNullValue());
			String payload = readFileToString(templateFile);
			assertThat("created template file content is not null", payload, notNullValue());
			assertThat("created template file is not empty", isNotBlank(payload), equalTo(true));
			// uncomment for additional output
			//System.out.println(" >> CREATED TEMPLATE XML: " + payload);
			
			// template unmarshalling
			final Template template = TEMPLATE_XMLB.typeFromFile(templateFile);
			assertThat("template is not null", template, notNullValue());
			// uncomment for additional output
			System.out.println(" >> TEMPLATE: " + template);
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("TemplateBinding() failed: " + e.getMessage());
		} finally {			
			System.out.println("TemplateBinding() has finished");
		}
	}

}