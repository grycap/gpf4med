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
import static org.grycap.gpf4med.xml.TemplateXmlBinder.TEMPLATE_XMLB;

import java.io.File;
import java.io.IOException;

import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.xml.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TRENCADIS template loader. This class uses a StAX parser to load the template from
 * an XML document.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class TemplateLoader extends XmlParser<Template> implements TemplateLoaderIf {

	private final static Logger LOGGER = LoggerFactory.getLogger(TemplateLoader.class);

	public static TemplateLoader create(final File file) {
		return new TemplateLoader(file);
	}

	private TemplateLoader(final File file) {
		super(file);
	}

	@Override
	public Template load() throws IOException {		
		checkArgument(file != null, "Uninitialized or invalid file");
		Template template = null;
		try {
			final String filename = file.getCanonicalPath();
			LOGGER.trace("Loading template from file: " + filename);
			template = TEMPLATE_XMLB.typeFromFile(file);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException("Failed to load template", e);
		}
		return template;
	}	

}