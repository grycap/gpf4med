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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL manipulation utilities.
 * @author Erik Torres <ertorser@upv.es>
 */
public final class URLUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(URLUtils.class);

	public static final String FILE = "file";
	public static final String HTTP = "http";
	public static final String HTTPS = "https";

	/**
	 * Extracts the protocol from an URL.
	 * @param url the source URL.
	 * @return the protocol of the URL. In case that no protocol is found, {@link #FILE}
	 *         is assumed.
	 */
	public static String extractProtocol(final URL url) {
		checkArgument(url != null, "Uninitialized URL");
		String protocol = url.getProtocol();
		if (StringUtils.isBlank(protocol)) {
			protocol = FILE;
		}
		return protocol;
	}

	/**
	 * Checks whether the specified URL points to an object accessible through one of the 
	 * remote protocols supported by this application.
	 * @param url input URL.
	 * @return {@code true} if the specified URL points to an object accessible through one 
	 *         of the remote protocols supported by this application, otherwise {@code false}.
	 */
	public static boolean isRemoteProtocol(final URL url) {
		checkArgument(url != null, "Uninitialized URL");
		final String protocol = extractProtocol(url);
		return protocol.equals(HTTP) || protocol.equals(HTTPS);
	}

	/**
	 * Checks whether the specified URL points to a file in the local file-system.
	 * @param url input URL.
	 * @return {@code true} if the specified URL points to a file, otherwise {@code false}.
	 */
	public static boolean isFileProtocol(final URL url) {
		checkArgument(url != null, "Uninitialized URL");
		final String protocol = extractProtocol(url);
		return protocol.equals(FILE);
	}

	/**
	 * Reads the text document pointed by the specified URL and loads a list of URLs contained 
	 * in the document (one URL per line, lines starting with any number of spaces and then the
	 * character {@code #} are silently ignored).
	 * @param source the source URL.
	 * @return the list of URLs contained in the text file pointed by the source URL.
	 * @throws IOException If an input/output error occurs.
	 */
	public static URL[] readIndex(final URL source) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		final String content = readURLToString(source);
		checkState(content != null, "No content found");		
		final List<URL> urls = new ArrayList<URL>();
		final BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
		String line;
		while ((line = in.readLine()) != null) {
			if (StringUtils.isNotBlank(line) && !line.matches("^\\s*#.*")) {
				try {					
					final URL url = new URL(new StringTokenizer(line).nextToken());
					urls.add(url);
				} catch (Exception e) {
					LOGGER.warn("Ignoring invalid line: " + line);
				}
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	/**
	 * Reads a text object from a URL and writes it to a Java {@code String}.
	 * @param source Source URL.
	 * @return The content of the text object.
	 * @throws IOException If an input/output error occurs.
	 */
	public static String readURLToString(final URL source) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		String content = null;		
		File srcFile = null;
		boolean deleteFileOnExit = false;
		try {
			if (isFileProtocol(source)) {
				srcFile = FileUtils.toFile(source);
			} else {
				srcFile = File.createTempFile("URLUtils_readURLToString", null);					
				deleteFileOnExit = true;
				download(source, srcFile);
			}
			content = FileUtils.readFileToString(srcFile);
		} finally {
			if (deleteFileOnExit) {
				FileUtils.deleteQuietly(srcFile);
			}
		}
		return content;
	}

	/**
	 * Reads data from a URL and writes them to a local file.
	 * @param source Source URL.
	 * @param destination Destination pathname.
	 * @throws IOException If an input/output error occurs.
	 */
	public static void download(final URL source, final String destination) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		checkArgument(StringUtils.isNotBlank(destination), "Uninitialized or invalid destination");
		URLUtils.download(source, new File(destination));		
	}

	/**
	 * Reads data from a URL and writes them to a local file.
	 * @param source Source URL.
	 * @param destination Destination file.
	 * @throws IOException If an input/output error occurs.
	 */
	public static void download(final URL source, final File destination) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		checkArgument(destination != null, "Uninitialized destination");
		final URLConnection conn = source.openConnection();
		checkState(conn != null, "Cannot open connection to: " + source.toString());
		final String contentType = conn.getContentType();
		final int contentLength = conn.getContentLength();
		checkState(StringUtils.isNotEmpty(contentType), "Cannot determine the content type of: " + source.toString());
		if (contentType.startsWith("text/") || contentLength == -1) {
			URLUtils.downloadText(source, destination);
		} else {
			URLUtils.downloadBinary(conn, destination);
		}
	}

	/**
	 * Reads a text file from a URL and writes them to a local file. This method reads the remote file 
	 * line by line, writing the lines to the local file with the default codification.
	 * @param source Source URL.
	 * @param destination Destination file.
	 * @throws IOException If an input/output error occurs.
	 */
	private static void downloadText(final URL source, final File destination) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		checkArgument(destination != null, "Uninitialized destination");
		BufferedReader in = null;
		OutputStream out = null;
		try {
			FileUtils.forceMkdir(destination.getParentFile());
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			out = new FileOutputStream(destination);
			String lineInput;
			while ((lineInput = in.readLine()) != null) {
				out.write((lineInput + "\n").getBytes());
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignore) { }
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception ignore) { }
			}			
		}
	}

	/**
	 * Reads any kind of data (including binary) from a URL and writes them to a local file. This method 
	 * uses a buffer to read the remote file. It's designed with performance in mind.
	 * @param source Source URL.
	 * @param destination Destination file.
	 * @throws IOException If an input/output error occurs.
	 */
	private static void downloadBinary(final URLConnection source, final File destination) throws IOException {
		checkArgument(source != null, "Uninitialized source");
		checkArgument(destination != null, "Uninitialized destination");
		InputStream in = null;
		OutputStream out = null;
		try {
			FileUtils.forceMkdir(destination.getParentFile());
			in = new BufferedInputStream(source.getInputStream());
			out = new FileOutputStream(destination);
			final int contentLength = source.getContentLength();
			byte[] buffer = new byte[1024];
			int bytesRead = 0;			
			int totalBytesRead = 0;
			while ((bytesRead = in.read(buffer)) != -1) {   
				out.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			if (totalBytesRead != contentLength) {
				try {
					destination.delete();
				} catch (Exception ignore) { }
				throw new IOException("Only read " + totalBytesRead + " bytes; Expected " 
						+ contentLength + " bytes");
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignore) { }
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception ignore) { }
			}
		}
	}

	/**
	 * Parses a URL from a String. This method supports file-system paths 
	 * (e.g. /foo/bar).
	 * @param str String representation of an URL.
	 * @return an URL.
	 * @throws IOException If an input/output error occurs.
	 */
	public static URL parseURL(final String str) throws MalformedURLException {
		URL url = null;
		if (StringUtils.isNotBlank(str)) {
			try {
				url = new URL(str);
			} catch (MalformedURLException e) {
				url = null;
				if (!str.matches("^[a-zA-Z]+[/]*:[^\\\\]")) {
					// convert path to UNIX path
					String path = FilenameUtils.separatorsToUnix(str.trim());
					final Pattern pattern = Pattern.compile("^([a-zA-Z]:/)");
					final Matcher matcher = pattern.matcher(path);
					path = matcher.replaceFirst("/");
					// convert relative paths to absolute paths
					if (!path.startsWith("/")) {
						path = path.startsWith("~") ? path.replaceFirst("~", System.getProperty("user.home"))
								: FilenameUtils.concat(System.getProperty("user.dir"), path);
					}
					// normalize path
					path = FilenameUtils.normalize(path, true);
					if (StringUtils.isNotBlank(path)) {
						url = new File(path).toURI().toURL();
					} else {
						throw new MalformedURLException("Invalid path: " + path);
					}
				} else {
					throw e;
				}
			}
		}
		return url;
	}

	/**
	 * Checks whether or not a URL is valid.
	 * @param str input URL.
	 * @param strict when is set to {@code true}, an additional check is performed 
	 *        to verify that the URL is formatted strictly according to RFC2396.
	 * @return {@code true} if the specified URL is valid. Otherwise, {@code false}.
	 */
	public static boolean isValid(final String str, final boolean strict) {
		URL url = null;
		if (StringUtils.isNotBlank(str)) {
			try {
				url = new URL(str);
				if (strict) {
					url.toURI();
				}
			} catch (MalformedURLException | URISyntaxException e) {
				url = null;
			}			
		}
		return url != null;
	}	
	
}