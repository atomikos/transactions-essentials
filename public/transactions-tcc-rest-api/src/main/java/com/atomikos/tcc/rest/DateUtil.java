package com.atomikos.tcc.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	
	public static String toDate(long timestamp) {
		Date date = new Date(timestamp);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		return format.format(date.getTime());
	}
	
	public static String never() {
		
		return toDate(Long.MAX_VALUE);
	}
}
