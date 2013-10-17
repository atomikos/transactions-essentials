package com.atomikos.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");

	public static String format(Date date) {
		return dateFormatter.format(date);
	}
}
