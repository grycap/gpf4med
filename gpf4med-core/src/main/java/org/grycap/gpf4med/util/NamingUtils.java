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

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * Utilities to work with names.
 * @author Erik Torres <ertorser@upv.es>
 */
public final class NamingUtils {

	/**
	 * Joins the tokens and dates provided as parameters, computes a digest and returns a Base64 
	 * representation of the string produced that can be used to identify a cache element or to 
	 * store a file in the disk.
	 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC2045</a>
	 * @param tokens The list of input tokens that have to be used to compute the name.
	 * @param dates The list of input dates that have to be used to compute the name.
	 * @return A Base64 representation of the computed key.
	 * @throws IOException is thrown if the computation of the key fails.
	 */
	public static String genSafeKey(final @Nullable String[] tokens, final @Nullable Date[] dates) throws IOException {
		try {
			final byte[] digest = digest(tokens, dates);
			// encode with Base64
			return new String(Base64.encodeBase64(digest, false, false));
		} catch (Exception e) {
			throw new IOException("Key generation has failed", e);
		}
	}

	/**
	 * Joins the tokens and dates provided as parameters, computes a digest and returns a Base32 
	 * representation of the string produced that can be used to identify a cache element. The 
	 * generated key will only contain the characters A-Z and 2-7.
	 * @see <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC4648</a>
	 * @param tokens The list of input tokens that have to be used to compute the key.
	 * @param dates The list of input dates that have to be used to compute the key.
	 * @param extension Optional file-name extension.
	 * @return A Base32 representation of the computed file-name.
	 * @throws IOException is thrown if the computation of the file-name fails.
	 */
	public static String genSafeFilename(final @Nullable String[] tokens, final @Nullable Date[] dates,
			final @Nullable String extension) throws IOException {
		try {			
			final byte[] digest = digest(tokens, dates);
			// encode with Base32
			return new Base32().encodeAsString(digest) + (StringUtils.isNotBlank(extension) ? extension.trim() : "");
		} catch (Exception e) {
			throw new IOException("Filename generation has failed", e);
		}
	}

	private static byte[] digest(final @Nullable String[] tokens, @Nullable final Date[] dates) throws IOException {
		try {
			String mixName = "";
			if (tokens != null) {
				for (final String token : tokens) {
					mixName += (token != null ? token.trim() : "");					
				}
			}
			if (dates != null) {
				for (final Date date : dates) {
					mixName += (date != null ? Long.toString(date.getTime()) : "");
				}
			}
			// compute digest
			final byte[] bytesOfMixName = mixName.getBytes("UTF-8");
			final MessageDigest md = MessageDigest.getInstance("SHA");
			return md.digest(bytesOfMixName);			
		} catch (Exception e) {			
			throw new IOException("Digest computation has failed", e);
		}
	}

}