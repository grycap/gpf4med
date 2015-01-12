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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.grycap.gpf4med.util.URLUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests URL utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public class URLUtilsTest {

	private static final String TEST_OUTPUT_DIR = FilenameUtils.concat(System.getProperty("java.io.tmpdir"),
			URLUtilsTest.class.getSimpleName() + "_" + RandomStringUtils.random(8, true, true));

	@Before
	public void setUp() {
		FileUtils.deleteQuietly(new File(TEST_OUTPUT_DIR));
	}

	@Test
	public void test() {
		System.out.println("URLUtilsTest.test()");
		try {
			// load index
			final String[] validLines = new String[] { 
					"file:///path/to/local-file-system",
					"http://www.google.com/index.html",
					"https://github.com/index.html # GitHub"
			};
			final String[] invalidLines = new String[] { 
					"/path/to/local-file-system",
					"invalid_protocol://www.google.com/index.html" 
			};
			final String[] commentLines = new String[] { 
					"#file:///path/to/local-file-system",
					" #  http://www.google.com/index.html", 
					"#file:///path/to/local-file-system",
					" #  http://www.google.com/index.html",
					"   #file:///path/to/local-file-system"
			};
			final List<String> linesList = new ArrayList<String>();
			linesList.addAll(Arrays.asList(validLines));
			linesList.addAll(Arrays.asList(invalidLines));
			linesList.addAll(Arrays.asList(commentLines));
			final String indexFilename = FilenameUtils.concat(TEST_OUTPUT_DIR, "index.txt");
			FileUtils.writeLines(new File(indexFilename), linesList);
			final URL[] urls = URLUtils.readIndex(new File(indexFilename).toURI().toURL());
			assertThat("URLs is not null", urls, notNullValue());
			assertThat("number of readed URLs coincides", urls.length, equalTo(validLines.length));
			for (final String line : validLines) {
				boolean found = false;
				final String target = new StringTokenizer(line).nextToken();
				for (int i = 0; i < urls.length && !found; i++) {
					final String query = (!URLUtils.isFileProtocol(urls[i]) ? urls[i].toString()
							: URLUtils.FILE + "://" + urls[i].getPath());
					if (query.equals(target)) {
						found = true;
					}
				}
				assertThat("a valid URL was found in the index: " + line, found);
			}
			// parse URL
			final String[][] paths = { 
					{ "file:///foo", "/foo" }, 
					{ "/foo//", "/foo/" }, 
					{ "/foo/./", "/foo/" },
					{ "/foo/../bar", "/bar" }, 
					{ "/foo/../bar/", "/bar/" },
					{ "/foo/../bar/../baz", "/baz" },
					{ "//foo//./bar", "/foo/bar" },
					{ "/../", null },
					{ "../foo", FilenameUtils.concat(System.getProperty("user.dir"), "../foo") },
					{ "foo/bar/..", FilenameUtils.concat(System.getProperty("user.dir"), "foo/bar/..") },
					{ "foo/../../bar", FilenameUtils.concat(System.getProperty("user.dir"), "foo/../../bar") },
					{ "foo/../bar", FilenameUtils.concat(System.getProperty("user.dir"), "foo/../bar") },
					{ "//server/foo/../bar", "/server/bar" },
					{ "//server/../bar", null }, 
					{ "C:\\foo\\..\\bar", "/bar" }, 
					{ "C:\\..\\bar", null }, 
					{ "~/foo/../bar/", FilenameUtils.concat(System.getProperty("user.home"), "foo/../bar/") }, 
					{ "~/../bar", FilenameUtils.concat(System.getProperty("user.home"), "../bar") },
					{ "http://localhost", null }
			};
			for (int i = 0; i < paths.length; i++) {
				URL url = null;
				try {
					url = URLUtils.parseURL(paths[i][0]);					
					System.out.println("PATH='" + paths[i][0] 
							+ "', URL='" + (url != null ? url.toString() : "NULL") 
							+ "', EXPECTED='" + paths[i][1] + "'");
					assertThat("URL is not null", url, notNullValue());
					if (URLUtils.isFileProtocol(url)) {
						final File file = FileUtils.toFile(url);
						assertThat("file is not null", file, notNullValue());
						assertThat("file name coincides", FilenameUtils.normalizeNoEndSeparator(file.getCanonicalPath(), true)
								.equals(FilenameUtils.normalizeNoEndSeparator(paths[i][1], true)));
					}
				} catch (IOException ioe) {
					if (paths[i][1] != null) {
						throw ioe;
					} else {
						System.out.println("PATH='" + paths[i][0] + "' thrown the expected exception");
					}
				}
			}
			// validate URL
			assertThat("valid URL", URLUtils.isValid("http://www.google.com", true));
			assertThat("valid URL", !URLUtils.isValid("http://invalidURL^$&%$&^", true));
			assertThat("valid URL", URLUtils.isValid("http://example.com/file[/].html", false));
			assertThat("valid URL", !URLUtils.isValid("http://example.com/file[/].html", true));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("URLUtilsTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("URLUtilsTest.test() has finished");
		}		
	}

	@After
	public void cleanUp() {
		FileUtils.deleteQuietly(new File(TEST_OUTPUT_DIR));
	}

}