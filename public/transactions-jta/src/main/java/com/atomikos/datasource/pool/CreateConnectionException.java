/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


 /**
  * An exception to signal errors during the creation of connections.
  *
  *
  */

public class CreateConnectionException extends ConnectionPoolException
{

	private static final long serialVersionUID = 1858243647893576738L;
	public CreateConnectionException ( String reason , Exception cause )
	{
		super ( reason , cause );
	}
	public CreateConnectionException ( String reason )
	{
		super ( reason );
	}
}
