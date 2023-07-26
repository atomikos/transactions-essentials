/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

public class LogException extends Exception {

	private static final long serialVersionUID = -7801364632919026307L;

	public LogException() {
		super();
	}

	public LogException(String message) {
		super(message);
	}

	public LogException(Throwable cause) {
		super(cause);
	}

	public LogException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
