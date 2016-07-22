/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;


 /**
  *
  *
  *
  * Exception signaling that the state has
  * been corrupted. Occurrences should almost
  * invariably cause the session handle to be
  * discarded.
  *
  *
  */

public class InvalidSessionHandleStateException
extends Exception
{

	private static final long serialVersionUID = 2838873552114439968L;


	InvalidSessionHandleStateException ( String msg )
	{
		super ( msg );
	}


	InvalidSessionHandleStateException ( String msg , Exception cause )
	{
		super ( msg , cause );
	}

}
