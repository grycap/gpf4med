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

package org.grycap.gpf4med.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.grycap.gpf4med.AllUnitTests;
import org.grycap.gpf4med.DocumentManager;
import org.grycap.gpf4med.TemplateManager;

/**
 * Test utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public class TestUtils {

	public static final String ANCHOR_FILENAME = "m2anchor";

	public static final String TEMPLATES_FOLDER = "templates";
	public static final String REPORTS_FOLDER = "reports";

	public static final String RESOURCES_FOLDER;
	static {
		final URL anchorURL = TestUtils.class.getClassLoader().getResource(ANCHOR_FILENAME);
		File anchorFile = null;
		try {
			anchorFile = new File(anchorURL.toURI());
		} catch (Exception e) {
			anchorFile = new File(System.getProperty("user.dir"));
		}
		RESOURCES_FOLDER = anchorFile.getParent();
	}
	/*
	public static Collection<File> getTemplateFiles() {
		final File dir = new File(FilenameUtils.concat(AllUnitTests.TEST_RESOURCES_PATH, TEMPLATES_FOLDER));
		return FileUtils.listFiles(dir, new String[] { "xml" }, false);
	}*/
	public static void getTemplateFiles() {
		final File dir = new File(FilenameUtils.concat(AllUnitTests.TEST_RESOURCES_PATH, TEMPLATES_FOLDER));
		Collection<File> files = FileUtils.listFiles(dir, new String[] { "xml" }, false);
		try {
			TemplateManager.INSTANCE.setup(Arrays.asList(FileUtils.toURLs(files.toArray(new File[files.size()]))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getReportFiles() {
		final File dir = new File(FilenameUtils.concat(AllUnitTests.TEST_RESOURCES_PATH, REPORTS_FOLDER));
		Collection<File> files = FileUtils.listFiles(dir, new String[] { "xml" }, false);
		try {
			DocumentManager.INSTANCE.setup(Arrays.asList(FileUtils.toURLs(files.toArray(new File[files.size()]))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
}