/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

 /**
  * Common superclass for heuristics.
  */

public class HeuristicException extends Exception {

	private static final long serialVersionUID = 1L;

	public HeuristicException(String message) {
		super(message);
	}
	
}
