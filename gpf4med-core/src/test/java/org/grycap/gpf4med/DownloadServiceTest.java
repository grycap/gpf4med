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
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests the download manager.
 * @author Erik Torres <ertorser@upv.es>
 */
public class DownloadServiceTest {

	private static final File TEST_OUTPUT_DIR = new File(FilenameUtils.concat(System.getProperty("java.io.tmpdir"),
			DownloadServiceTest.class.getSimpleName() + "_" + RandomStringUtils.random(8, true, true)));

	@Before
	public void setUp() {
		FileUtils.deleteQuietly(TEST_OUTPUT_DIR);		
	}

	@Test
	public void test() {
		System.out.println("DownloadServiceTest.test()");
		try {
			// prepare input requests
			final String[] uris = { 
					"http://www.chromix.com/downloadarea/testimages/frontier_color57sb.jpg",
					"http://www.hutchcolor.com/Targets_&_images_to_go/GrayBoat.zip",
					"http://www.chromix.com/downloadarea/testimages/frontier_color57s.jpg"
			};
			// CRC32 checksum computed with Apache Commons IO
			final ImmutableMap<String, Map.Entry<Long, Long>> chc32sums = 
					new ImmutableMap.Builder<String, Map.Entry<Long, Long>>()
					.put("frontier_color57sb.jpg", new AbstractMap.SimpleEntry<Long, Long>(1885150690l, 205632l))
					.put("GrayBoat.zip", new AbstractMap.SimpleEntry<Long, Long>(2944050653l, 1286661l))
					.put("frontier_color57s.jpg", new AbstractMap.SimpleEntry<Long, Long>(3600934165l, 967643l))
					.build();
			// test download with encryption
			final ImmutableMap<URI, File> requests =
					new ImmutableMap.Builder<URI, File>()
					.put(new URI(uris[0]), uriToFile(uris[0]))
					.put(new URI(uris[1]), uriToFile(uris[1]))
					.put(new URI(uris[2]), uriToFile(uris[2]))
					.build();
			final ImmutableMap<URI, File> pending = new DownloadService().download(requests, null, 
					null, null);
			assertThat("service response is not null", pending, notNullValue());
			assertThat("there are no pending requests", pending.size(), equalTo(0));
			for (final Map.Entry<String, Map.Entry<Long, Long>> chc32sum : chc32sums.entrySet()) {
				final File file = new File(TEST_OUTPUT_DIR, chc32sum.getKey());
				assertThat("file exists", file.exists(), equalTo(true));
				assertThat("file size coincides", chc32sum.getValue().getValue().longValue(), equalTo(file.length()));
				assertThat("CRC32 checksum coincides", chc32sum.getValue().getKey().longValue(), 
						equalTo(FileUtils.checksumCRC32(file)));				
			}
			// test download with encryption
			FileUtils.deleteQuietly(TEST_OUTPUT_DIR);
			final FileEncryptionProvider encryptionProvider = FileEncryptionProvider.getInstance(
					RandomStringUtils.randomAscii(1024));
			final ImmutableMap<URI, File> pending2 = new DownloadService().download(requests, null, 
					encryptionProvider, null);
			assertThat("service response is not null", pending2, notNullValue());
			assertThat("there are no pending requests", pending2.size(), equalTo(0));
			for (final Map.Entry<String, Map.Entry<Long, Long>> chc32sum : chc32sums.entrySet()) {
				final File file = new File(TEST_OUTPUT_DIR, chc32sum.getKey());
				assertThat("file exists", file.exists(), equalTo(true));
				// decrypt file
				final File clearFile = new File(file.getCanonicalPath() + ".tmp");
				encryptionProvider.decrypt(new FileInputStream(file), new FileOutputStream(clearFile));
				// check file				
				assertThat("file size concides", chc32sum.getValue().getValue().longValue(), 
						equalTo(clearFile.length()));
				assertThat("CRC32 checksum coincides", chc32sum.getValue().getKey().longValue(), 
						equalTo(FileUtils.checksumCRC32(clearFile)));
			}
			// test file validation fail
			final String anyFile = uris[0];
			final FileValidator alwaysFailValidator = mock(FileValidator.class);
			when(alwaysFailValidator.isValid(any(File.class))).thenReturn(false);			
			final ImmutableMap<URI, File> pending3 = new DownloadService().download(new ImmutableMap.Builder<URI, File>()
					.put(new URI(anyFile), new File(TEST_OUTPUT_DIR, "any_file")).build(),
					alwaysFailValidator, null, null);
			assertThat("service response is not null", pending3, notNullValue());
			assertThat("read invalid file is expected to return a pending file", pending3.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("DownloadServiceTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("DownloadServiceTest.test() has finished");
		}
	}

	@After
	public void cleanUp() {
		FileUtils.deleteQuietly(TEST_OUTPUT_DIR);
	}

	public File uriToFile(final String uri) {
		return new File(TEST_OUTPUT_DIR, FilenameUtils.getName(uri));
	}

}