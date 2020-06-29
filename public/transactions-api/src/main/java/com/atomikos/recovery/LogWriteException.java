/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

public class LogWriteException extends LogException {

	private static final long serialVersionUID = 5648208124041649641L;

	public LogWriteException() {
		super();
	}
	public LogWriteException(Throwable cause) {
		super(cause);
	}
	
}
