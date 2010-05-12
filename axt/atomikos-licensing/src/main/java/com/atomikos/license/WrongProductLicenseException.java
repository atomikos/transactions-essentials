package com.atomikos.license;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * Exception to signal that the license is not for the product in question.
 */

public class WrongProductLicenseException extends LicenseException
{

	public WrongProductLicenseException()
	{
		super();
	}
	
	public WrongProductLicenseException ( String message )
	{
		super ( message );
	}
}
