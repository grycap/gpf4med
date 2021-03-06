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
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.model.template.ConceptNameTemplate;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.util.Id;
import org.grycap.gpf4med.util.TRENCADISUtils;
import org.grycap.gpf4med.util.NamingUtils;
import org.grycap.gpf4med.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Manages the DICOM-SR templates.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public enum TemplateManager implements Closeable2 {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(TemplateManager.class);

	private Collection<URL> urls = null;
	private ImmutableMap<String, Template> dont_use = null;

	@Override
	public void setup(final @Nullable Collection<URL> urls) {
		this.dont_use = null;
		this.urls = urls;
		
	}

	@Override
	public void preload() {
		// lazy load, so initial access is needed
		final ImmutableCollection<Template> templates = listTemplates();		
		if (templates != null && templates.size() > 0) {
			LOGGER.info(templates.size() + " DICOM-SR templates loaded");
		} else {
			LOGGER.warn("No DICOM-SR templates loaded");
		}
	}

	@Override
	public void close() throws IOException {
		// nothing to do
	}	

	public @Nullable Template getTemplate(final ConceptNameTemplate conceptName) {
		checkArgument(conceptName != null, "Uninitialized or invalid concept name");
		final ImmutableMap<String, Template> templates = templates(null);
		return templates != null ? templates.get(Id.getId(conceptName)) : null;
	}

	public ImmutableCollection<Template> listTemplates() {
		final ImmutableMap<String, Template> templates = templates(null);
		return templates != null ? templates.values() : new ImmutableList.Builder<Template>().build();
	}
	public ImmutableCollection<Template> listTemplates(String idOntology) {
		final ImmutableMap<String, Template> templates = templates(idOntology);
		return templates != null ? templates.values() : new ImmutableList.Builder<Template>().build();
	}

	/**
	 * Lazy load -> Adapted to consider TRENCADIS storage
	 * @return the list of available templates.
	 */
	private ImmutableMap<String, Template> templates(String idOntology) {
		if (dont_use == null) {
			synchronized (TemplateManager.class) {
				if (dont_use == null) {
					// templates can be loaded from class-path, local files, through HTTP or using TRENCADIS plug-in
					File templatesCacheDir = null;
					try {
						// prepare local cache directory
						templatesCacheDir = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), 
								"templates" + File.separator + ConfigurationManager.INSTANCE.getTemplatesVersion());
						// read index
						if (urls == null && ConfigurationManager.INSTANCE.getTrencadisConfigFile() == null) {
							LOGGER.trace("TRENCADIS configuration file is null");
							final URL index = ConfigurationManager.INSTANCE.getTemplatesIndex();
							urls = Arrays.asList(URLUtils.readIndex(index));
						}
						if (urls == null && ConfigurationManager.INSTANCE.getTrencadisConfigFile() != null
							&& ConfigurationManager.INSTANCE.getTrencadisPassword() != null) {
							urls = null;
						}
						FileUtils.deleteQuietly(templatesCacheDir);
						FileUtils.forceMkdir(templatesCacheDir);
						// get a local copy of the connectors
						if (urls != null) {
							for (final URL url : urls) {
								try {
									final File destination = new File(templatesCacheDir, NamingUtils
											.genSafeFilename(new String[] { url.toString() }, null, ".xml"));
									URLUtils.download(url, destination);
								} catch (Exception e2) {
									LOGGER.warn("Failed to get template from URL: " + url.toString(), e2);
								}
							}
						}
						else {
							try {
								if (idOntology != null)
									TRENCADISUtils.INSTANCE.downloadOntology(idOntology, templatesCacheDir.getAbsolutePath());
								else	
									TRENCADISUtils.INSTANCE.downloadAllOntologies(templatesCacheDir.getAbsolutePath());
							} catch (Exception e3) {
								LOGGER.warn("Failed to get templates from TRENCADIS" , e3);
							}
						}
					} catch (Exception e) {
						LOGGER.error("Failed to prepare templates for access", e);
					}
					// load available templates
					checkArgument(templatesCacheDir != null, "Uninitialized templates local cache directory");
					final ImmutableMap.Builder<String, Template> builder = new ImmutableMap.Builder<String, Template>();
					for (final File file : FileUtils.listFiles(templatesCacheDir, TrueFileFilter.INSTANCE, null)) {
						String filename = null;
						try {
							filename = file.getCanonicalPath();
							final Template template = TemplateLoader.create(file).load();
							checkState(template != null && template.getCONTAINER() != null 
									&& template.getCONTAINER().getCONCEPTNAME() != null, "No template found");
							final String id = Id.getId(template.getCONTAINER().getCONCEPTNAME());
							checkState(StringUtils.isNotBlank(id), "Uninitialized or invalid concept name");
							builder.put(id, template);
							LOGGER.trace("New template " + template.getDescription() + ", ontology " + template.getIDOntology() 
									+ ", loaded from: " + filename);
						} catch (Exception e) {						
							LOGGER.error("Failed to load template: " + filename, e);
						}
					}
					dont_use = builder.build();
				}
			}
		}
		return dont_use;
	}

}