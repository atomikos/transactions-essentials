/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

/**
 * Exception signaling heuristic commit.
 */

public class HeurCommitException extends Exception {

	private static final long serialVersionUID = 1L;

	public HeurCommitException() {
		super("Heuristic Commit Exception");
	}

}
