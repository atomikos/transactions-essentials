package com.atomikos.vendor;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 
 * Copyright &copy; 2004, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A panel for developer licenses
 */
public class DeveloperLicensePanel extends AbstractLicensePanel
{

    
    public DeveloperLicensePanel(
        String[] productNames,
        ResourceBundle resources)
    {
        super(productNames, resources);
        
    }

 
    protected void saveToFile(String file) throws IOException
    {
	
		String productName = getProductName();
		String key = getSecretKey();
    	String owner = getOwner();
				
		try {
		
		CreateLicense.createDeveloperLicense ( owner , productName , file , key , getExpiryDate() , getFeatures() );
        
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

    }

}
