package com.cmpe275.openhome.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

	public static LocalDate convertDateToLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
	public static Date convertLocalDateToDate(LocalDate dateToConvert) {
	    return java.sql.Date.valueOf(dateToConvert);
	}
	
	public static LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
	    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static Date convertLocalDateTimeToDate(LocalDateTime dateToConvert) {
	    return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}
}
