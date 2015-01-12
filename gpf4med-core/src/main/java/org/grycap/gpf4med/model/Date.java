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
import static com.google.common.base.Preconditions.checkState;

import java.util.Locale;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * TRENCADIS date.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Date extends BaseType {

	private DateTime value;

	public static final DateTimeFormatter ES_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
	public static final DateTimeFormatter US_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");

	public Date() { }	

	public DateTime getValue() {
		return value;
	}

	public void setValue(final DateTime value) {
		this.value = checkNotNull(value, "Uninitialized date");
	}

	public static DateTime valueFromString(final String str) {
		checkState(StringUtils.isNotBlank(str), "Uninitialized or invalid string value");
		return Date.ES_FORMATTER.parseDateTime(str);
	}

	public static String stringFromValue(final DateTime value, final @Nullable Locale locale) {
		checkState(value != null, "Uninitialized or invalid date-time value");
		final String str;
		if (locale != null && "ES".equalsIgnoreCase(locale.getLanguage())) {
			str = Date.ES_FORMATTER.print(value);
		} else {
			str = Date.US_FORMATTER.print(value);
		}
		return str;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.addValue(super.toString())
				.add("value", value)
				.toString();
	}

}