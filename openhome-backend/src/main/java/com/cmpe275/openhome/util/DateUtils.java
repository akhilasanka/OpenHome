package com.cmpe275.openhome.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd MMM 'yy");
	private static final SimpleDateFormat SIMPLE_MONTH_NAME = new SimpleDateFormat("MMM 'yy");
	private static final DateTimeFormatter MONTH_NAME = DateTimeFormatter.ofPattern("MMM 'yy");

	public static String formatForDisplay(Date date) {
		return SIMPLE_DATE_FORMAT.format(date);
	}

	public static String formatMonthForDisplay(Date date) {
		return SIMPLE_MONTH_NAME.format(date);
	}

	public static String formatMonthForDisplay(LocalDate date) {
		return date.format(MONTH_NAME);
	}

	public static LocalDate convertDateToLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
	public static Date convertLocalDateToDate(LocalDate dateToConvert) {
	    return java.sql.Date.valueOf(dateToConvert);
	}
	
	public static LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
	    return dateToConvert.toInstant()
	  	      .atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
