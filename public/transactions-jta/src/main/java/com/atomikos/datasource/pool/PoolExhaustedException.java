/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


 /**
  * Exception signaling pool exhaustion.
  *
  */
public class PoolExhaustedException extends ConnectionPoolException {


	private static final long serialVersionUID = 7266245068986719051L;

	PoolExhaustedException ( String reason ) {
		super ( reason );
	}

	public PoolExhaustedException() {
		super();
	}

}
