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

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Optional;

/**
 * TRENCADIS numeric.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Num extends BaseType {

	private Optional<Double> value = Optional.absent();

	public Num() { }

	public @Nullable Double getValue() {
		return value.orNull();
	}

	public void setValue(final @Nullable Double value) {
		this.value = Optional.fromNullable(value);
	}

	public static @Nullable Double valueFromString(final String str) {
		return StringUtils.isNotBlank(str) ? Double.parseDouble(str) : null;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.addValue(super.toString())
				.add("value", value)
				.toString();
	}

}