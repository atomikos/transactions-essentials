/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


 /**
  * Common superclass for all exceptions thrown by the
  * pooling mechanism.
  *
  */

public class ConnectionPoolException extends Exception
{

	private static final long serialVersionUID = 1L;

	ConnectionPoolException ( String reason , Exception cause )
	{
		super ( reason , cause );
	}
	ConnectionPoolException ( String reason )
	{
		super ( reason );
	}
	public ConnectionPoolException() {
	}
}
