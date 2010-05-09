package com.atomikos.license;

import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * 
 * Copyright &copy; 2003 Atomikos. All rights reserved.
 * 
 * 
 *
 * A node locked license class.
 */

class NodeLockedLicense extends License
{
	NodeLockedLicense ( java.util.Properties properties )
	{
		super ( properties );
	}
	
	public void checkLocalHost ( String productName )
	throws java.net.UnknownHostException, CorruptLicenseException, 
			InvalidMachineLicenseException, ExpiredLicenseException, 
			WrongProductLicenseException
	{
		String type = getProperty ( LICENSE_TYPE_PROPERTY_NAME );
         
		if ( ! HOST_TYPE_PROPERTY_VALUE.equals ( type ) ) {
					
			printMsg ( "License type should be " + HOST_TYPE_PROPERTY_VALUE );
			
			throw new CorruptLicenseException ( 
			"License type should be " + HOST_TYPE_PROPERTY_VALUE);
		}
		
		String ipList = getProperty ( HOSTS_PROPERTY_NAME );
		
		
		String ip = java.net.InetAddress.getLocalHost().getHostAddress();

		if ( ipList == null || ( ipList.indexOf ( ip ) < 0 ) ) {                  
			printMsg ( "Local IP not in license file -> denying license rights" );
			throw new InvalidMachineLicenseException ( 
					"Local IP not in license file -> denying license rights" );
		}
	
		super.checkLocalHost( productName );
		
	}
	
	public void printInfo(PrintWriter out)
	{
		super.printInfo ( out );
		out.println ( "LICENSE TYPE: NODE-LOCKED LICENSE" );
		out.println ( "ALLOWED HOSTS: ");
		
		String ipList = getProperty ( HOSTS_PROPERTY_NAME );
		StringTokenizer parser = new StringTokenizer(ipList);
		while ( parser.hasMoreTokens() ) {
			String host = parser.nextToken();
			out.println ( host );
		}
		
		
	}
}
