package org.grycap.gpf4med.model.util;

import static com.google.common.base.Preconditions.checkState;

import java.util.Locale;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {
	
	public static final DateTimeFormatter ES_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
	public static final DateTimeFormatter US_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");
	
	public static DateTime valueFromString(final String str) {
		checkState(StringUtils.isNotBlank(str), "Uninitialized or invalid string value");
		return ES_FORMATTER.parseDateTime(str);
	}

	public static String formattedString(final String dateValue, final @Nullable Locale locale) {
		checkState(dateValue != null, "Uninitialized or invalid date-time value");
		DateTime date = ES_FORMATTER.parseDateTime(dateValue);
		final String str;
		if (locale != null && "ES".equalsIgnoreCase(locale.getLanguage())) {
			str = dateValue;
		} else {
			str = US_FORMATTER.print(date);
		}
		return str;
	}

}
