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

package org.grycap.gpf4med.model.util;

import java.util.List;

/**
 * Available DICOM-SR templates.
 * @author Erik Torres <ertorser@upv.es>
 *
 */
public class AvailableTemplates {

	private List<AvailableTemplate> templates;	
	
	public AvailableTemplates() { }

	public List<AvailableTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(final List<AvailableTemplate> templates) {
		this.templates = templates;
	}	
	
}