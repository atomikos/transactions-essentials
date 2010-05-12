package com.atomikos.license;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * Exception to denote an invalid machine (for which there is no license right).
 */

public class InvalidMachineLicenseException extends LicenseException
{
	public InvalidMachineLicenseException() {}
	
	public InvalidMachineLicenseException ( String message )
	{
		super ( message );
	}

}
