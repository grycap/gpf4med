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

package org.grycap.gpf4med.model;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.LinkedHashSet;

import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * TRENCADIS container template.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ContainerTemplate extends BaseTemplate {

	// preserves insertion order of entries
	private final LinkedHashSet<BaseTemplate> childrenTemplate = new LinkedHashSet<BaseTemplate>();

	public ContainerTemplate() { }

	public LinkedHashSet<BaseTemplate> getChildrenTemplate() {
		return childrenTemplate;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = toStringHelper(this);
		helper.addValue(super.toString());
		for (final BaseTemplate template : childrenTemplate) {
			helper.addValue(template);
		}
		return helper.toString();
	}

}