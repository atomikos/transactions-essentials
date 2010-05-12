package com.atomikos.license;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * 
 * Copyright &copy; 2004 Atomikos. All rights reserved.
 * 
 * 
 *
 * A developer license.
 */

public class DeveloperLicense extends License
{

    
    public DeveloperLicense(Properties properties)
    {
        super(properties);
       
    }
    

	public void checkLocalHost ( String productName )
			throws java.net.UnknownHostException, CorruptLicenseException, 
					InvalidMachineLicenseException, ExpiredLicenseException, 
					WrongProductLicenseException
		{
				String type = getProperty ( LICENSE_TYPE_PROPERTY_NAME );
				

         
				if ( ! DEVELOPER_TYPE_PROPERTY_VALUE.equals ( type ) ) {
					
					printMsg ( "License type should be " + DEVELOPER_TYPE_PROPERTY_VALUE );
			
					throw new CorruptLicenseException ( 
					"License type should be " + DEVELOPER_TYPE_PROPERTY_VALUE);
				}
		
			printMsg ( "WARNING: YOU ARE RUNNING PRODUCT " 
							+ getProperty ( PRODUCT_NAME_PROPERTY_NAME) +"\n"+
							"WHICH IS LICENSED FOR DEVELOPMENT ONLY. " + "\n"+
							"PLEASE CONTACT sales@atomikos.com IF YOU WANT\n" +
							"TO PURCHASE A PRODUCTION LICENSE FOR THIS PRODUCT." ); 				


				
				super.checkLocalHost ( productName );
		}

 
		public void printInfo(PrintWriter out)
		{


			out.println ( "LICENSE TYPE: DEVELOPMENT ONLY. PRODUCTION USE IS FORBIDDEN!" );
			out.println ( "CONTACT sales@atomikos.com IF YOU NEED A PRODUCTION LICENSE.");
			super.printInfo ( out );
       
			
       	
		}

}
