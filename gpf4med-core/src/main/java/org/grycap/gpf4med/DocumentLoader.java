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

import static com.google.common.base.Preconditions.checkArgument;
import static org.grycap.gpf4med.xml.ReportXmlBinder.REPORT_XMLB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.grycap.gpf4med.util.MimeUtils;
import org.grycap.gpf4med.xml.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TRENCADIS report loader. This class uses a StAX parser to load the report from
 * an XML document.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class DocumentLoader extends XmlParser<Document> implements DocumentLoaderIf {

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentLoader.class);

	public static DocumentLoader create(final File file) {
		return new DocumentLoader(file);
	}

	private DocumentLoader(final File file) {
		super(file);
	}

	@Override
	public Document load() throws IOException {
		checkArgument(file != null && file.canRead(), "Uninitialized or invalid file");
		File clearFile = file;		
		Document document = null;
		try {			
			// get input from encrypted file			
			if (!MimeUtils.isTextFile(file) && ConfigurationManager.INSTANCE.getEncryptLocalStorage()) {
				clearFile = new File(file.getPath() + ".tmp");
				final FileEncryptionProvider encryptionProvider = ConfigurationManager.INSTANCE.getFileEncryptionProvider();
				encryptionProvider.decrypt(new FileInputStream(file), new FileOutputStream(clearFile));
			}
			final String filename = clearFile.getCanonicalPath();
			LOGGER.trace("Loading document from file: " + filename);			
			document = REPORT_XMLB.typeFromFile(clearFile);			
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException("Failed to load document", e);
		} finally {
			if (clearFile != file) {
				FileUtils.deleteQuietly(clearFile);
			}
		}
		return document;
	}

}