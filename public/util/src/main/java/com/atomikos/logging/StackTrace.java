/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

public class StackTrace {

	private static final String TAB = "\t";
	static final String EMPTY_STRING = "";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static String toString(StackTraceElement[] stackTrace) {
		if (stackTrace != null) {
			StringBuffer stackTraces = new StringBuffer();
			String lineSeparator = EMPTY_STRING;
			for (StackTraceElement ste : stackTrace) {
				stackTraces.append(lineSeparator);
				lineSeparator = LINE_SEPARATOR;
				stackTraces.append(TAB);
				stackTraces.append(ste);
			}
			return stackTraces.toString();
		}
		return EMPTY_STRING;
	}

}