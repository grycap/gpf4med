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

import java.util.Locale;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.BaseTemplate;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.ContainerTemplate;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.model.Properties;

/**
 * Utilities to handle TRENCADIS templates.
 * @author Erik Torres <ertorser@upv.es>
 */
public class TemplateUtils {

	public @Nullable static String getMeaning(final ConceptName conceptName, final DocumentTemplate template,
			final @Nullable Locale locale) {
		checkArgument(conceptName != null, "Uninitialized or invalid concept name");
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Concept name id is invalid");
		checkArgument(template != null, "Uninitialized or invalid template");
		final ConceptName found = find(conceptName, template.getContainerTemplate());
		if (found != null) {
			return (locale != null && "ES".equalsIgnoreCase(locale.getLanguage()) 
					? found.getCodeMeaning() : found.getCodeMeaning2());				
		}
		return null;
	}

	private static @Nullable ConceptName find(final ConceptName conceptName, final BaseTemplate item) {
		ConceptName found = null;
		// base case
		if (conceptName.equalsIgnoreNoReferenceable(item.getConceptName())) {
			found = item.getConceptName();
		}
		if (found == null && item.getProperties() != null) {
			final Properties properties = item.getProperties();
			if (properties.getDefaultConceptName() != null 
					&& conceptName.equalsIgnoreNoReferenceable(properties.getDefaultConceptName())) {
				found = properties.getDefaultConceptName();					
			}			
			if (found == null && properties.getCodeValues() != null) {
				for (final ConceptName cn : properties.getCodeValues()) {
					if (conceptName.equalsIgnoreNoReferenceable(cn)) {
						found = cn;
						break;
					}
				}
			}
		}
		// recursive case
		if (found == null && item instanceof ContainerTemplate) {
			for (final BaseTemplate item2 : ((ContainerTemplate)item).getChildrenTemplate()) {
				found = find(conceptName, item2);
				if (found != null) {
					break;
				}
			}
		}
		return found;
	}

}