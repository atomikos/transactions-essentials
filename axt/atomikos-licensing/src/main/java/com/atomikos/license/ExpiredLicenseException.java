package com.atomikos.license;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * Exception to indicate an expired license.
 */

public class ExpiredLicenseException extends LicenseException
{
	public ExpiredLicenseException() {}
	
	public ExpiredLicenseException ( String message ) 
	{
		super ( message );
	}
}
