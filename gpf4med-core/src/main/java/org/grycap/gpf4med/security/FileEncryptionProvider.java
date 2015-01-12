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

package org.grycap.gpf4med.security;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides file encryption/decryption with the AES (Advanced Encryption Standard) in Cipher Block 
 * Chaining (CBC) mode.
 * @author Erik Torres <ertorser@upv.es>
 * @see Cryptographic Service Provider (CSP).
 * @see Java Cryptographic Architecture (JCA).
 * @see Java Cryptography Extension (JCE) Unlimited Strength.
 */
public class FileEncryptionProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(FileEncryptionProvider.class);

	/**
	 * Is set to {@code true} if unlimited cryptography strength is available. Otherwise, is set
	 * to {@code false}, indicating that only 128-bit encryption is available.
	 */
	public final static boolean UNLIMITED_CRYPTOGRAPHY_AVAILABLE;	

	static {
		boolean tmp = false;
		try {
			tmp = Cipher.getMaxAllowedKeyLength("AES") == Integer.MAX_VALUE;
		} catch (NoSuchAlgorithmException ignore) { }
		UNLIMITED_CRYPTOGRAPHY_AVAILABLE = tmp;
	}

	private final Cipher encryptCipher;
	private final Cipher decryptCipher;	

	private FileEncryptionProvider(final Cipher encryptCipher, final Cipher decryptCipher) {
		this.encryptCipher = encryptCipher;
		this.decryptCipher = decryptCipher;
	}	

	/**
	 * Creates a new {@link FileEncryptionProvider} instance that provides encryption/decryption
	 * capabilities based on the specified password.
	 * @param password encryption/decryption password.
	 * @return a new {@link FileEncryptionProvider} instance.
	 * @throws Exception if an error occurs in the execution of the operation.
	 */
	public static FileEncryptionProvider getInstance(final String password) throws Exception {
		checkArgument(StringUtils.isNotBlank(password), "Uninitialized or invalid password");
		// generate key from password
		final byte[] salt = generateSalt();
		LOGGER.trace("Generated salt: " + Hex.encodeHexString(salt));		
		final SecretKey secret = generateKey(password, salt);
		LOGGER.trace("Generated key: " + Hex.encodeHexString(secret.getEncoded()));
		// create encryption cipher - bouncycastle equivalent: Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
		final Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");		
		encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
		// initialization vector needed by the CBC mode
		final AlgorithmParameters params = encryptCipher.getParameters();		
		final byte[] initVector = params.getParameterSpec(IvParameterSpec.class).getIV();
		// create decryption cipher - bouncycastle equivalent: Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
		final Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");		
		decryptCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(initVector));
		LOGGER.trace(String.format("Encryption/decryption ciphers were created - %s", 
				encryptCipher.getProvider().getInfo()));
		return new FileEncryptionProvider(encryptCipher, decryptCipher);
	}

	/**
	 * Encrypts the message read from the specified input stream and writes the cipher text to the 
	 * specified output stream.
	 * @param fis input stream from where to read the plain text message.
	 * @param fos output stream to where the cipher text is written.
	 * @throws Exception if an error occurs in the execution of the operation.
	 */
	public void encrypt(final InputStream fis, final OutputStream fos) throws Exception {
		CipherOutputStream cos = null;
		try {
			cos = new CipherOutputStream(fos, encryptCipher);
			final byte[] buffer = new byte[1024];			
			int bytesRead = 0;			
			while ((bytesRead = fis.read(buffer)) >= 0) {
				cos.write(buffer, 0, bytesRead);
			}
			cos.flush();
		} finally {
			try {
				fis.close();
			} catch (Exception ignore) { }
			try {
				cos.close();
			} catch (Exception ignore) { }
		}		
	}

	/**
	 * Decrypts the cipher text read from the specified input stream and writes the decrypted message to 
	 * the specified output stream.
	 * @param inputStream input stream from where to read the cipher text.
	 * @param outputStream output stream to where the decrypted message is written.
	 * @throws Exception if an error occurs in the execution of the operation.
	 */
	public void decrypt(final InputStream fis, final OutputStream fos) throws Exception {
		CipherOutputStream cos = null;
		try {
			cos = new CipherOutputStream(fos, decryptCipher);
			final byte[] buffer = new byte[1024];			
			int bytesRead = 0;
			while ((bytesRead = fis.read(buffer)) >= 0) {
				cos.write(buffer, 0, bytesRead);
			}
			cos.flush();
		} finally {
			try {
				fis.close();
			} catch (Exception ignore) { }
			try {
				cos.close();
			} catch (Exception ignore) { }
		}		
	}

	/**
	 * Creates a key that can be used with a cryptographic service provider. The key is computed from
	 * the specified password and protected with the specified salt. 
	 * @param password the password from which the key is computed.
	 * @param salt the salt that is used to protect the key from dictionary attacks.
	 * @return a key that can be used with a cryptographic service provider.
	 * @throws Exception if an error occurs in the execution of the operation.
	 */
	public static SecretKey generateKey(final String password, final byte[] salt) throws Exception {
		SecretKey secret;
		if (UNLIMITED_CRYPTOGRAPHY_AVAILABLE) {
			// bouncycastle equivalent: SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC")
			final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
			secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} else {
			// bouncycastle equivalent: SecretKeyFactory.getInstance("PBEWITHSHA256AND128BITAES-CBC-BC")
			final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
			secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		}
		return secret;
	}

	/**
	 * Creates a random salt of 8 bytes selected by a {@link SecureRandom} that can be passed as
	 * input parameter to a function that hashes a password to protect the generated password against 
	 * dictionary attacks.
	 * @return a random salt of 8 bytes selected by a {@link SecureRandom}.
	 */
	public static byte[] generateSalt() {
		final byte[] salt = new byte[8];
		new SecureRandom().nextBytes(salt);		
		return salt;
	}	

}