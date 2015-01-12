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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * TRENCADIS properties.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Properties {

	private Cardinality cardinality;
	private ConditionType conditionType;
	private Optional<String> integrityRestriction = Optional.absent();
	private Optional<String> defaultValue = Optional.absent();
	private Optional<MeasurementUnits> measurementUnits = Optional.absent();
	private Optional<ConceptName> defaultConceptName = Optional.absent();
	private Optional<List<ConceptName>> codeValues = Optional.absent();

	public Properties() { }	

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(final Cardinality cardinality) {
		this.cardinality = checkNotNull(cardinality, "Uninitialized cardinality");
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public void setConditionType(final ConditionType conditionType) {
		this.conditionType = checkNotNull(conditionType, "Uninitialized condition type");
	}

	public @Nullable String getIntegrityRestriction() {
		return integrityRestriction.orNull();
	}

	public void setIntegrityRestriction(final @Nullable String integrityRestriction) {
		this.integrityRestriction = Optional.fromNullable(integrityRestriction);
	}

	public @Nullable String getDefaultValue() {
		return defaultValue.orNull();
	}

	public void setDefaultValue(final @Nullable String defaultValue) {
		this.defaultValue = Optional.fromNullable(defaultValue);
	}

	public @Nullable MeasurementUnits getMeasurementUnits() {
		return measurementUnits.orNull();
	}

	public void setMeasurementUnits(final @Nullable MeasurementUnits measurementUnits) {
		this.measurementUnits = Optional.fromNullable(measurementUnits);
	}

	public @Nullable ConceptName getDefaultConceptName() {
		return defaultConceptName.orNull();
	}

	public void setDefaultConceptName(final @Nullable ConceptName defaultConceptName) {
		this.defaultConceptName = Optional.fromNullable(defaultConceptName);
	}

	public @Nullable List<ConceptName> getCodeValues() {
		return codeValues.orNull();
	}

	public void setCodeValues(final @Nullable List<ConceptName> codeValues) {
		if (codeValues != null) {
			final List<ConceptName> tmp = new ArrayList<ConceptName>();
			Collections.copy(tmp, codeValues);
			this.codeValues = Optional.fromNullable(tmp);
		} else {
			this.codeValues = Optional.absent();
		}		
	}

	@Override
	public String toString() {
		final ToStringHelper helper = toStringHelper(this);
		helper.add("cardinality", cardinality)
		.add("conditionType", conditionType)
		.add("integrityRestriction", integrityRestriction.orNull())
		.add("defaultValue", defaultValue.orNull())
		.add("measurementUnits", measurementUnits.orNull())
		.add("defaultConceptName", defaultConceptName.orNull());
		if (codeValues.isPresent()) {
			for (final ConceptName conceptName : codeValues.get()) {
				helper.add("codeValues", conceptName);
			}
		}
		return helper.toString();
	}

	/* Inner types */

	public static class Cardinality {
		private Optional<Integer> min = Optional.absent();
		private Optional<Integer> max = Optional.absent();

		public Cardinality() { }

		public @Nullable Integer getMin() {
			return min.orNull();
		}

		public void setMin(final @Nullable Integer min) {
			this.min = Optional.fromNullable(min);
		}

		public @Nullable Integer getMax() {
			return max.orNull();
		}

		public void setMax(final @Nullable Integer max) {
			this.max = Optional.fromNullable(max);
		}

		@Override
		public String toString() {
			return toStringHelper(this)
					.add("min", min.orNull())
					.add("max", max.orNull())
					.toString();
		}
	}

	public static class ConditionType {
		private ConditionTypeEnum conditionType;
		private @Nullable Optional<String> xpathExpression = Optional.absent();

		public ConditionType() { }

		public ConditionTypeEnum getConditionType() {
			return conditionType;
		}

		public void setConditionType(final ConditionTypeEnum conditionType) {
			this.conditionType = checkNotNull(conditionType, "Uninitialized condition type");
		}

		public @Nullable String getXpathExpression() {
			return xpathExpression.orNull();
		}

		public void setXpathExpression(final @Nullable String xpathExpression) {
			this.xpathExpression = Optional.fromNullable(xpathExpression);
		}		

		@Override
		public String toString() {
			return toStringHelper(this)
					.add("conditionType", conditionType)
					.add("xpathExpression", StringUtils.trimToNull(xpathExpression.orNull()))
					.toString();
		}

	}

	public static enum ConditionTypeEnum {
		M("M"),		
		U("U"),
		MC("MC");

		private final String value;

		private ConditionTypeEnum(final String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

		public static ConditionTypeEnum fromValue(final String value) {
			for (final ConditionTypeEnum item: ConditionTypeEnum.values()) {
				if (item.value.equals(value)) {
					return item;
				}
			}
			throw new IllegalArgumentException("Invalid value: " + value);
		}

		@Override
		public String toString() {
			String str = null;
			switch (ConditionTypeEnum.fromValue(value)) {
			case M:
				str = "Multiple";
				break;
			case U:
				str = "Unique";
				break;
			case MC:
				str = "Multiple compound";
				break;
			default:
				str = "Unknown";
				break;
			}
			return str;
		}
	}

}