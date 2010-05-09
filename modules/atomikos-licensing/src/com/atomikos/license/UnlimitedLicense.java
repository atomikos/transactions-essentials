package com.atomikos.license;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * A license for all machines.
 */

class UnlimitedLicense extends License
{
	UnlimitedLicense ( Properties properties)
	{
		super ( properties );
	}

	public void checkLocalHost ( String productName )
		throws java.net.UnknownHostException, CorruptLicenseException, 
				InvalidMachineLicenseException, ExpiredLicenseException, 
				WrongProductLicenseException
		{
			String type = getProperty ( LICENSE_TYPE_PROPERTY_NAME );
         
			if ( ! UNLIMITED_TYPE_PROPERTY_VALUE.equals ( type ) ) {
					
				printMsg ( "License type should be " + UNLIMITED_TYPE_PROPERTY_VALUE );
			
				throw new CorruptLicenseException ( 
				"License type should be " + UNLIMITED_TYPE_PROPERTY_VALUE);
			}
			
			super.checkLocalHost ( productName );
		}

 
    public void printInfo(PrintWriter out)
    {
       	super.printInfo ( out );
       
       	out.println ( "LICENSE TYPE: UNLIMITED LICENSE" );
       	
    }
}
