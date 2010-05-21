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
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A custom panel for domain licenses.
 */

public class DomainLicensePanel extends AbstractLicensePanel
{

	private JTextField domainField_;
	private JPanel panel_;
	private int expiryInMonths_;
	
    /**
     * @param resources
     */
    
    public DomainLicensePanel( String[] products , ResourceBundle resources , int expiryInMonths )
    {
        super(products,resources);
        domainField_ = new JTextField();
        JLabel domainLabel = new JLabel ( getResource ( "domainLabel"));
        panel_ = new JPanel();
        panel_.setLayout ( new BorderLayout());
        panel_.add ( super.getJPanel() );
        JPanel domainPanel = new JPanel();
        domainPanel.setLayout ( new GridLayout ( 1 ,2));
        domainPanel.add ( domainLabel );
        domainPanel.add ( domainField_ );
        panel_.add ( domainPanel , BorderLayout.SOUTH );
        expiryInMonths_ = expiryInMonths;
        
    }
    
    protected JPanel getJPanel()
    {
    	return panel_;
    }

	protected String getDomain()
	{
		return domainField_.getText();
	}
	
    protected void saveToFile(java.lang.String file) throws IOException
    {
		String productName = getProductName();
		String key = getSecretKey();
    	String owner = getOwner();
				
		try {
		
		CreateLicense.createDomainLicense ( owner , productName , file , key , getExpiryDate ( expiryInMonths_ ) , getDomain(), getFeatures() );
        
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

    }

}
