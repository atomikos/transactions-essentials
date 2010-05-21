package com.atomikos.vendor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * Copyright &copy; 2004, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A panel for developer licenses
 */
public class OemLicensePanel extends AbstractLicensePanel
{
	private JPanel panel_;
    
    public OemLicensePanel(
        String[] productNames,
        ResourceBundle resources)
    {
        super(productNames, resources);

        
    }

   


 
    protected void saveToFile(String file) throws IOException
    {
	
		String productName = getProductName();
		String key = getSecretKey();
    	String oemName = getOwner();
				
		try {
		
		CreateLicense.createUnlimitedLicense ( oemName , productName , file , key , getExpiryDate () , getFeatures() );
        
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

    }

}
