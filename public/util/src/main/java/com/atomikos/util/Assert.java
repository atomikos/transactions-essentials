/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

public abstract class Assert {

	
	public static void notNull(String message, Object o) {
		if (o == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
