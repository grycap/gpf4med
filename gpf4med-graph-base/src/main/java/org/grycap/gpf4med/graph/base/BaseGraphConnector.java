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

package org.grycap.gpf4med.graph.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;

import org.grycap.gpf4med.TemplateManager;
import org.grycap.gpf4med.graph.base.rest.GraphBaseResource;
import org.grycap.gpf4med.graph.base.rest.GraphBaseResourceImpl;
import org.grycap.gpf4med.ext.GraphConnector;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.template.ConceptNameTemplate;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.util.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base graph connector.
 * @author Erik Torres <ertorser@upv.es>
 */
@Author(name = "GRyCAP, UPVLC, I3M, ES")
@Version(version = 10000)
@PluginImplementation
public class BaseGraphConnector implements GraphConnector {	

	private final static Logger LOGGER = LoggerFactory.getLogger(BaseGraphConnector.class);

	@Override
	public String path() {
		return GraphBaseResource.PATH_STRING;
	}

	@Override
	public Class<?> restResourceDefinition() {
		return GraphBaseResource.class;
	}

	@Override
	public Class<?> restResourceImplementation() {
		return GraphBaseResourceImpl.class;
	}	

	@Override
	public String getDescription() {
		return "Base graph implementation";
	}	

	@Override
	public void create() {
		// TODO
	}

	@Override
	public void clear() {
		// TODO		
	}

	@Override
	public void add(final Document document) {
		checkArgument(document != null && document.getCONTAINER() != null 
				&& document.getCONTAINER().getCONCEPTNAME() != null, 
				"Uninitialized or invalid document");
		// find the template for the type of document
		ConceptName conceptName = document.getCONTAINER().getCONCEPTNAME();
		ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(conceptName.getCODEVALUE())
																		   .withCODESCHEMA(conceptName.getCODESCHEMA())
																		   .withCODEMEANING(conceptName.getCODEMEANING())
																		   .withCODEMEANING2(conceptName.getCODEMEANING2());
		final Template template = TemplateManager.INSTANCE.getTemplate(conceptNameTemplate);
		checkState(template != null, "No document template was found for the concept name: " + conceptName);
		conceptNameTemplate = template.getCONTAINER().getCONCEPTNAME();
		LOGGER.info("Loading document of type: " + Id.getIdMeaning(conceptNameTemplate));
		// create a new entry in the graph
		if ("RID10357@RADLEX".equals(Id.getId(conceptName))) {
			new MammographyCreator().create(document, template);
		} else if ("RID10326@RADLEX".equals(Id.getId(conceptName))) {
			new EcographyCreator().create(document, template);
		} else if ("RID10312@RADLEX".equals(Id.getId(conceptName))) {
			new MagneticResonanceCreator().create(document, template);
		} else {
			throw new IllegalStateException("Unsupported document type: " + Id.getIdMeaning(conceptNameTemplate));
		}
	}

}