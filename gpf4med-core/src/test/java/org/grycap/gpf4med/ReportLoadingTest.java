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

import java.io.File;
import java.util.Collection;

import org.grycap.gpf4med.model.Document;
import org.grycap.gpf4med.util.TestUtils;
import org.junit.Test;

/**
 * Test report loading.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ReportLoadingTest {

	@Test
	public void test() {
		System.out.println("ResportLoadingTest.test()");
		try {
			// load example reports
			final Collection<File> files = TestUtils.getReportFiles();
			for (final File file : files) {
				final Document document = DocumentLoader.create(file).load();
				assertThat("document is not null", document, notNullValue());
				/* uncomment for additional output
				System.out.println(" >> Document\n" + document.toString() + "\n"); */
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ResportLoadingTest.test() failed: " + e.getMessage());
		} finally {            
			System.out.println("ResportLoadingTest.test() has finished");
		}
	}

}