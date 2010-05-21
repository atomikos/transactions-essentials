package com.atomikos.vendor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A custom panel for the evaluation license.
 */
public class EvaluationLicensePanel extends AbstractLicensePanel
{

    /**
     * @param resources
     */
    
    public EvaluationLicensePanel(String[] products,ResourceBundle resources)
    {
        super(products,resources);
        
        
    }

    protected void saveToFile(java.lang.String file) throws IOException
    {
    	String productName = getProductName();
    	String key = getSecretKey();
    	
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime ( new Date() );
		calendar.add ( Calendar.MONTH , 3 );
		long expiryDate = calendar.getTime().getTime();
		try {
		
        CreateLicense.createEvaluationLicense ( 
        	getOwner() , productName , file , key , expiryDate, getFeatures() );
        
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

    }

}
