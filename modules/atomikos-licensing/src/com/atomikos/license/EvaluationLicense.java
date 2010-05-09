package com.atomikos.license;
import java.util.Properties;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * An evaluation license class.
 */

class EvaluationLicense extends License
{
	EvaluationLicense ( Properties properties )
	{
		super ( properties );
	}
	

	public void checkLocalHost ( String productName )
	throws java.net.UnknownHostException, CorruptLicenseException, 
		InvalidMachineLicenseException, ExpiredLicenseException, 
		WrongProductLicenseException
	{
		String type = getProperty ( LICENSE_TYPE_PROPERTY_NAME );
         
		if ( ! EVAL_TYPE_PROPERTY_VALUE.equals ( type ) ) {
			//no type specified -> defaults to false
			printMsg ( "License type should be " + EVAL_TYPE_PROPERTY_VALUE );
			
			throw new CorruptLicenseException ( 
			"License type should be " + EVAL_TYPE_PROPERTY_VALUE);
		}
		

		super.checkLocalHost( productName );
	}
	


}
