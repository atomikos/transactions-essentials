/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

/**
 * An exception signaling that the transaction's work has been rolled back
 * heuristically.
 */

public class HeurRollbackException extends HeuristicException {

	private static final long serialVersionUID = 1L;

	public HeurRollbackException() {
		super("Heuristic Rollback Exception");
	}

}
