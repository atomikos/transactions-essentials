/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.text.Format;
import java.util.Date;

public class DateHelper {
	
	private static Format dateFormatter = FastDateFormat.getInstance("yyyy.MM.dd HH:mm:ss:SSS");

	public static String format(Date date) {
		return dateFormatter.format(date);
	}
}
