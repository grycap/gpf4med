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

import javax.annotation.Nullable;

import org.grycap.gpf4med.model.document.ConceptName;

import com.google.common.base.Optional;

/**
 * Provides a helper to load values from TRENCADIS templates.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Value {

	private @Nullable Optional<String> value = Optional.absent();
	private @Nullable Optional<ConceptName> conceptName = Optional.absent();

	public Value() { }

	public @Nullable String getValue() {
		return value.orNull();
	}

	public void setValue(final String value) {
		this.value = Optional.fromNullable(value);
	}

	public @Nullable ConceptName getConceptName() {
		return conceptName.orNull();
	}

	public void setConceptName(final ConceptName conceptName) {
		this.conceptName = Optional.fromNullable(conceptName);
	}

}