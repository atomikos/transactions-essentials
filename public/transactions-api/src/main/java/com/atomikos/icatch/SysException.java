/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * An exception for unexpected system errors with nested information.
 */


public class SysException extends RuntimeException
{

	private static final long serialVersionUID = -9183281406145817016L;

	public SysException (String msg)
	{
		super(msg);
	}
	
	public SysException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
}
