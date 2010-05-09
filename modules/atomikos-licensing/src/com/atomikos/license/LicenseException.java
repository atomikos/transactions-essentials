package com.atomikos.license;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * Base class for the licensing errors.
 */

public class LicenseException extends Exception
{
	
	public LicenseException() 
	{
		super();
	}
	
	public LicenseException ( String message )
	{
		super ( message );
	}
	
	

}
