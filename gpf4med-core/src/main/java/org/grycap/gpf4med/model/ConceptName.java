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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Optional;

/**
 * TRENCADIS concept name.
 * @author Erik Torres <ertorser@upv.es>
 */
public class ConceptName {

	private CodeValue codeValue;
	private CodeSchema codeSchema;	
	private Optional<String> codeMeaning = Optional.absent();
	private Optional<String> codeMeaning2 = Optional.absent();

	public ConceptName() { }

	public CodeValue getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(final CodeValue codeValue) {
		this.codeValue = checkNotNull(codeValue, "Uninitialized code value");
	}

	public CodeSchema getCodeSchema() {
		return codeSchema;
	}

	public void setCodeSchema(final CodeSchema codeSchema) {
		this.codeSchema = checkNotNull(codeSchema, "Uninitialized code schema");
	}

	public @Nullable String getCodeMeaning() {
		return codeMeaning.orNull();
	}

	public void setCodeMeaning(final String codeMeaning) {
		this.codeMeaning = Optional.fromNullable(StringUtils.trimToNull(codeMeaning));
	}

	public @Nullable String getCodeMeaning2() {
		return codeMeaning2.orNull();
	}

	public void setCodeMeaning2(final @Nullable String codeMeaning2) {
		this.codeMeaning2 = Optional.fromNullable(StringUtils.trimToNull(codeMeaning2));
	}

	public String id() {
		return codeValue.getValue().trim() + "@" + codeSchema.name().trim();
	}

	public String idMeaning() {
		return id() + "=" + codeMeaning2.or("NULL");
	}

	public boolean equalsIgnoreNoReferenceable(final ConceptName other) {
		if (other == null) {
			return false;
		}
		return Objects.equals(codeValue, other.codeValue)
				&& Objects.equals(codeSchema, other.codeSchema);						
	}

	@Override
	public String toString() {		
		return toStringHelper(this)
				.add("codeValue", codeValue)
				.add("codeSchema", codeSchema)
				.add("codeMeaning", StringUtils.trimToNull(codeMeaning.orNull()))
				.add("codeMeaning2", StringUtils.trimToNull(codeMeaning2.orNull()))
				.toString();			
	}

	/* Inner types */

	public static class CodeValue {
		private String value;

		public CodeValue() { }

		public String getValue() {
			return value;
		}

		public void setValue(final String value) {
			final String tmp = checkNotNull(StringUtils.trimToNull(value), "Uninitialized or invalid value");
			checkArgument(tmp.matches("\\w+"), "Invalid value: " + value);
			this.value = tmp;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			final CodeValue other = (CodeValue)obj;
			return Objects.equals(value, other.value);
		}

		@Override
		public String toString() {
			return toStringHelper(this)
					.add("value", value)
					.toString();			
		}

	}

	public static enum CodeSchema {
		SNOMED_CT("SNOMED-CT"),
		RADLEX("RADLEX"),
		UNIT_MEASUREMENT("UNIT_MEASUREMENT"),
		TRENCADIS_MAMO("TRENCADIS_MAMO"),
		UNKNOWN("UNKNOWN");

		private final String value;

		private CodeSchema(final String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

		public static CodeSchema fromValue(final String value) {
			for (final CodeSchema item: CodeSchema.values()) {
				if (item.value.equals(value)) {
					return item;
				}
			}
			throw new IllegalArgumentException("Invalid value: " + value);
		}

		@Override
		public String toString() {
			String str = null;
			switch (CodeSchema.fromValue(value)) {
			case SNOMED_CT:
				str = "SNOMED-CT";
				break;
			case RADLEX:
				str = "RadLex";
				break;
			case UNIT_MEASUREMENT:
				str = "Measurement Units";
				break;
			case TRENCADIS_MAMO:
				str = "TRENCADIS-Mammography";
				break;
			case UNKNOWN:
			default:
				str = "Unknown";
				break;
			}
			return str;
		}
	}

}