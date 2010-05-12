package com.atomikos.license;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Properties;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * A license for an entire Internet domain name.
 */

class DomainLicense extends License
{
	DomainLicense ( Properties properties)
	{
		super ( properties );
	}

	public void checkLocalHost ( String productName )
		throws java.net.UnknownHostException, CorruptLicenseException, 
				InvalidMachineLicenseException, ExpiredLicenseException, 
				WrongProductLicenseException
		{
			String type = getProperty ( LICENSE_TYPE_PROPERTY_NAME );
         
			if ( ! DOMAIN_TYPE_PROPERTY_VALUE.equals ( type ) ) {
					
				printMsg ( "License type should be " + DOMAIN_TYPE_PROPERTY_VALUE );
			
				throw new CorruptLicenseException ( 
				"License type should be " + DOMAIN_TYPE_PROPERTY_VALUE);
			}
			String dom = InetAddress.getLocalHost().getHostName(); 
		  	int dot = dom.indexOf ( "." );
		  	dom = dom.substring ( dot + 1 );
		  	dom = dom.trim();
					
			if ( ! dom.endsWith ( getProperty ( 
				DOMAIN_PROPERTY_NAME ) ) ) {
	
				printMsg ( "Local host not in license domain: " + dom );
				throw new InvalidMachineLicenseException ( 
				"Local host not in license domain: " + dom );
			 }
		
			super.checkLocalHost ( productName );
		}

 
    public void printInfo(PrintWriter out)
    {
       	super.printInfo ( out );
       	String domain = getProperty ( DOMAIN_PROPERTY_NAME );
       	out.println ( "LICENSE TYPE: SITE LICENSE" );
       	out.println ( "DOMAIN: " + domain );
    }
}
