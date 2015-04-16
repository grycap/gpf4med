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
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.template.ChildrenTemplate;
import org.grycap.gpf4med.model.template.CodeTemplate;
import org.grycap.gpf4med.model.template.ConceptNameTemplate;
import org.grycap.gpf4med.model.template.ContainerTemplate;
import org.grycap.gpf4med.model.template.DateTemplate;
import org.grycap.gpf4med.model.template.NumTemplate;
import org.grycap.gpf4med.model.template.Properties;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.template.TextTemplate;

/**
 * Utilities to handle TRENCADIS templates.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class TemplateUtils {

	public @Nullable static String getMeaning(final ConceptNameTemplate conceptName, final Template template,
			final @Nullable Locale locale) {
		checkArgument(conceptName != null, "Uninitialized or invalid concept name");
		final String id = conceptName.getCODEVALUE();
		checkState(StringUtils.isNotBlank(id), "Concept name id is invalid");
		checkArgument(template != null, "Uninitialized or invalid template");
		final ConceptNameTemplate found = find(conceptName, template.getCONTAINER());
		if (found != null) {
			return (locale != null && "ES".equalsIgnoreCase(locale.getLanguage()) 
					? found.getCODEMEANING() : found.getCODEMEANING2());				
		}
		return null;
	}

	private static @Nullable ConceptNameTemplate find(final ConceptNameTemplate conceptName,
			final ContainerTemplate item) {
		ConceptNameTemplate found = null;
		// base case
		if (equalsIgnoreNoReferenceable(conceptName, item.getCONCEPTNAME())) {
			found = item.getCONCEPTNAME();
		}
		if (found == null && item.getPROPERTIES() != null) {
			final Properties properties = item.getPROPERTIES();
			if (properties.getDEFAULTCODEVALUE() != null) {
				ConceptNameTemplate defaultConceptName = new ConceptNameTemplate();
				defaultConceptName.setCODESCHEMA(properties.getDEFAULTCODEVALUE().getCodeSchema());
				defaultConceptName.setCODEVALUE(properties.getDEFAULTCODEVALUE().getCodeValue());
				defaultConceptName.setCODEMEANING(properties.getDEFAULTCODEVALUE().getCodeMeaning());
				defaultConceptName.setCODEMEANING2(properties.getDEFAULTCODEVALUE().getCodeMeaning2());
				if (equalsIgnoreNoReferenceable(conceptName, defaultConceptName)) {
					found = defaultConceptName;	
				}
			}		
			if (found == null && properties.getCODEVALUES() != null) {
				for (final ConceptNameTemplate cn : properties.getCODEVALUES().getCONCEPTNAME()) {
					if (equalsIgnoreNoReferenceable(conceptName, cn)) {
						found = cn;
						break;
					}
				}
			}
		}
		if (found == null && item.getCHILDREN() != null) {
			ChildrenTemplate childrenTemplate = item.getCHILDREN();
			if (childrenTemplate.getCODE() != null) {
				for (final CodeTemplate code : childrenTemplate.getCODE()) {
					if (equalsIgnoreNoReferenceable(conceptName, code.getCONCEPTNAME())) {
						found = code.getCONCEPTNAME();
						break;
					}
					if (code.getPROPERTIES().getCODEVALUES() != null) {
						for (final ConceptNameTemplate cn : code.getPROPERTIES().getCODEVALUES().getCONCEPTNAME()) {
							if (equalsIgnoreNoReferenceable(conceptName, cn)) {
								found = cn;
								break;
							}
						}
					}
				}
			}
			if (childrenTemplate.getDATE() != null) {
				for (final DateTemplate date : childrenTemplate.getDATE()) {
					if (equalsIgnoreNoReferenceable(conceptName, date.getCONCEPTNAME())) {
						found = date.getCONCEPTNAME();
						break;
					}
				}			
			}
			if (childrenTemplate.getNUM() != null) {
				for (final NumTemplate num : childrenTemplate.getNUM()) {
					if (equalsIgnoreNoReferenceable(conceptName, num.getCONCEPTNAME())) {
						found = num.getCONCEPTNAME();
						break;
					}
					if (num.getPROPERTIES().getUNITMEASUREMENT() != null) {
						final ConceptNameTemplate conceptNameCode = num.getPROPERTIES().getUNITMEASUREMENT().getCONCEPTNAME();
						if (equalsIgnoreNoReferenceable(conceptName, conceptNameCode)) {
							found = conceptNameCode;
							break;
						}
					}
				}
			}
			if (childrenTemplate.getTEXT() != null) {
				for (final TextTemplate text : childrenTemplate.getTEXT()) {
					if (equalsIgnoreNoReferenceable(conceptName, text.getCONCEPTNAME())) {
						found = text.getCONCEPTNAME();
						break;
					}
				}
			}
		}
		// recursive case
		if (found == null && item instanceof ContainerTemplate) {
			for (final ContainerTemplate item2 : ((ContainerTemplate)item).getCHILDREN().getCONTAINER()) {
				found = find(conceptName, item2);
				if (found != null) {
					break;
				}
			}
		}
		return found;
	}
	
	public static boolean equalsIgnoreNoReferenceable(
			final ConceptNameTemplate current, final ConceptNameTemplate other) {
		if (other == null) {
			return false;
		}
		return Objects.equals(current.getCODEVALUE(), other.getCODEVALUE())
				&& Objects.equals(current.getCODESCHEMA(), other.getCODESCHEMA());
	}
}