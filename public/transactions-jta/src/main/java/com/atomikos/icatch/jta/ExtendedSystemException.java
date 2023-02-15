/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import javax.transaction.SystemException;

/**
 * A better system exception, containing throwable cause.
 */

public class ExtendedSystemException extends SystemException {

	private static final long serialVersionUID = 1475357523769839371L;

	public ExtendedSystemException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}

}
