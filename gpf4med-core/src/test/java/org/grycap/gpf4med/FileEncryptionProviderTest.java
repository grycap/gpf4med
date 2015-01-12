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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests file encryption.
 * @author Erik Torres <ertorser@upv.es>
 */
public class FileEncryptionProviderTest {

	private static final File TEST_OUTPUT_DIR = new File(FilenameUtils.concat(System.getProperty("java.io.tmpdir"),
			FileEncryptionProviderTest.class.getSimpleName() + "_" + RandomStringUtils.random(8, true, true)));

	@Before
	public void setUp() {
		FileUtils.deleteQuietly(TEST_OUTPUT_DIR);
		System.out.println("Is unlimited cryptography available? " 
				+ FileEncryptionProvider.UNLIMITED_CRYPTOGRAPHY_AVAILABLE);
	}

	@Test
	public void test() {
		System.out.println("FileEncryptionProviderTest.test()");
		try {			
			// prepare test file
			final File clearFile = new File(TEST_OUTPUT_DIR, "clear.txt");
			final File encryptedFile = new File(TEST_OUTPUT_DIR, "encrypt.txt");
			final File clearTestFile = new File(TEST_OUTPUT_DIR, "clear_test.txt");

			final String message = RandomStringUtils.randomAscii(10007);
			FileUtils.write(clearFile, message);

			// test encryption
			FileInputStream inputStream = new FileInputStream(clearFile);
			FileOutputStream outputStream = new FileOutputStream(encryptedFile);

			final FileEncryptionProvider provider = FileEncryptionProvider.getInstance(
					RandomStringUtils.randomAscii(1024));
			provider.encrypt(inputStream, outputStream);

			final String encryptedMessage = FileUtils.readFileToString(encryptedFile);
			assertThat("encrypted message is not null", encryptedMessage, notNullValue());
			assertThat("encrypted message is not empty", StringUtils.isNotBlank(encryptedMessage));
			assertThat("encrypted message does not coincide with clear message", message, not(equalTo(encryptedMessage)));

			// test decryption
			inputStream = new FileInputStream(encryptedFile);
			outputStream = new FileOutputStream(clearTestFile);

			provider.decrypt(inputStream, outputStream);

			final String clearMessage = FileUtils.readFileToString(clearTestFile);
			assertThat("clear message is not null", clearMessage, notNullValue());
			assertThat("clear message is not empty", StringUtils.isNotBlank(clearMessage));
			assertThat("clear message coincides with original message", message, equalTo(clearMessage));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("FileEncryptionProviderTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("FileEncryptionProviderTest.test() has finished");
		}
	}

	@After
	public void cleanUp() {
		FileUtils.deleteQuietly(TEST_OUTPUT_DIR);
	}

}