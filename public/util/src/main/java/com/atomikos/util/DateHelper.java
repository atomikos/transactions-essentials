/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

	private static ThreadLocal<SimpleDateFormat> threadSafeSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {

			return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");

		};

	};

	public static String format(Date date) {
		return threadSafeSimpleDateFormat.get().format(date);
	}
}
