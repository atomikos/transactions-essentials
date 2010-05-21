package com.atomikos.vendor;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.atomikos.swing.PropertiesPanel;
import com.atomikos.swing.PropertiesTableModel;
import com.atomikos.swing.PropertyListener;

/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A custom panel for node-locked licenses. 
 */

public class NodeLicensePanel extends AbstractLicensePanel
implements PropertyListener
{

	JPanel panel_;
	Vector hosts_;
	int expiryInMonths_;
	HostsTableModel model_;
	
    /**
     * @param resources
     */
    public NodeLicensePanel(String[] products,ResourceBundle resources, int expiryInMonths)
    {
        super(products,resources);
        hosts_ = new Vector();
        expiryInMonths_ = expiryInMonths;
        String[] colNames = { getResource ( "hostsColumnName")};
        model_ = new HostsTableModel ( hosts_ , colNames);
       	PropertiesPanel properties = new PropertiesPanel ( model_ );
       	properties.addPropertyListener ( this );
       	panel_ = new JPanel();
       	panel_.setLayout ( new BorderLayout() );
       	panel_.add ( super.getJPanel() , BorderLayout.NORTH );
       	panel_.add ( properties.getPanel() );
    }
    
    protected JPanel getJPanel()
    {
    	return panel_;
    }

   
    protected void saveToFile(java.lang.String file) throws IOException
    {
		String productName = getProductName();
		String key = getSecretKey();
    	String owner = getOwner();
				
		try {
		
		CreateLicense.createNodeLockedLicense ( 
			owner , productName , file , key , getExpiryDate ( expiryInMonths_ ) ,
			(String[]) hosts_.toArray ( new String[0]),
			getFeatures() );
        
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

    }
    
	private String editParameters ( String old )
	{
		JPanel panel = new JPanel ();
		JTextField alias = new JTextField ( old , 12 );
		panel.add ( alias );
		int answ = JOptionPane.showConfirmDialog (
				null , panel, getResource ( "editHostTitle") ,
				JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE );


		return alias.getText();
	}

   
    public void newProperty(PropertiesTableModel table)
    {
    	if ( table != model_ ) super.newProperty ( table );
    	else {String alias = editParameters( "" );
			if ( !alias.equals ( "" ) ) {
				hosts_.addElement ( alias );
				table.rowInserted();
			}
    	}
        
    }

    
    public void editProperty(PropertiesTableModel table, int index)
    {
    	if ( table != model_ ) super.editProperty ( table , index );
    	else {
    		String oldAlias  =
    			( String ) hosts_.elementAt ( index );
    		String alias = editParameters ( oldAlias );

    		hosts_.removeElement ( oldAlias );
    		hosts_.addElement ( alias );
    		table.refresh();
    	}
        
    }

    
    public void deleteProperty(PropertiesTableModel table, int index)
    {
    	if ( table != model_ ) super.deleteProperty( table , index);
    	else {
    		String alias  =
    			( String ) hosts_.elementAt ( index );
    		hosts_.remove ( alias );
    		table.rowDeleted ( index );
    	}
    }

}
