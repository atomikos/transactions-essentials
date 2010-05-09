package com.atomikos.vendor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.atomikos.license.License;
import com.atomikos.swing.AbstractPropertiesTableModel;
import com.atomikos.swing.PropertiesPanel;
import com.atomikos.swing.PropertiesTableModel;
import com.atomikos.swing.PropertyListener;

/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 *
 * Common panel for shared license properties.
 */

abstract class AbstractLicensePanel implements PropertyListener
{
	
	
	
	static class FeaturesTableModel extends AbstractPropertiesTableModel
	{
		
      
       
		public FeaturesTableModel ( Vector data , String parColName , String valColName )
		{
			super(data, new String[]{ parColName , valColName });
            
		}

		public Object getValueAt(int row, int column)
		{
			Vector data = getData();
			String[] rowdata = ( String[] ) data.elementAt ( row );
            
			return rowdata[column];
		}
		
	}	
	

	private JComboBox productNameList_;
	private JTextField ownerField_;
	
	private JPanel panel_;
	private ResourceBundle resources_;
	private String[] productNames_;
	private PropertiesPanel featuresPanel_;
	private Vector features_;
	
	AbstractLicensePanel ( String[] productNames , ResourceBundle resources )
	{
		resources_ = resources;
		productNames_ = productNames;
		panel_ = new JPanel();
		panel_.setLayout ( new BorderLayout ());
		JPanel textPanel = new JPanel();
		textPanel.setLayout ( new GridLayout ( 2 , 2 ));
		textPanel.add ( new JLabel ( resources.getString ("productNameLabel")));
		productNameList_ = new JComboBox( productNames_ );
		textPanel.add ( productNameList_ );
		textPanel.add ( new JLabel ( resources.getString ("ownerLabel")));
		ownerField_ = new JTextField();
		textPanel.add ( ownerField_ );
		panel_.add  ( textPanel , BorderLayout.NORTH );
		features_ = new Vector();
		String parColName = resources.getString ( "featureNameLabel" );
		String valColName = resources.getString ( "featureValueLabel");
		FeaturesTableModel m  = new FeaturesTableModel ( features_ , parColName , valColName );
		featuresPanel_ = new PropertiesPanel ( m );
		featuresPanel_.addPropertyListener(this);
		panel_.add ( featuresPanel_.getPanel() );
	}
	
	protected String getResource ( String name )
	{
		return resources_.getString ( name );
	}
	
	protected JPanel getJPanel() 
	{
		return panel_;
	}
	
	protected String getProductName()
	{
		return (String) productNameList_.getSelectedItem();
	}
	
	protected String getSecretKey()
	{
		//return new String ( keyField_.getPassword() );
		return License.PRODUCT_NAME_PROPERTY_NAME;
	}
	
	protected long getExpiryDate ( int months ) 
	{
		GregorianCalendar calendar = new GregorianCalendar();
		Date now = new Date(); 
     	calendar.setTime (  now );
     	calendar.add ( Calendar.MONTH , months );
     	Date expiryDate = calendar.getTime();
     	long time = expiryDate.getTime();
     	if  ( time <= now.getTime() ) {
     		throw new RuntimeException ( "Overflow for month range: " + months );
     	}
     	return time;
	}
	
	protected String getOwner()
	{
		return ownerField_.getText();
	}
	
	protected long getExpiryDate() 
	{
		return Long.MAX_VALUE;
	}
	
	protected Properties getFeatures()
	{
		Properties p = new Properties();
		Iterator it = features_.iterator();
		while ( it.hasNext() ) {
			String[] row = ( String[] ) it.next();
			p.setProperty ( row[0] , row[1]);
		}
		return p;
	}
	
	/**
	 * This method is called when the user confirms that the 
	 * license should be saved.
	 * 
	 * @param file The file where the license should go.
	 * 
	 * @throws java.io.IOException On IO errors.
	 */
	protected abstract void saveToFile ( java.lang.String file )
	throws java.io.IOException;
	
	
	private void editProperty ( String[] keyValuePair )
	{
		JPanel panel = new JPanel ( );
		panel.setLayout ( new GridLayout ( 2 , 2 ));
		JLabel nameLabel = new JLabel ( resources_.getString("featureNameLabel"));
		panel.add ( nameLabel);
		JTextField nameField = new JTextField ( keyValuePair[0]);
		panel.add ( nameField);
		JLabel valueLabel = new JLabel ( resources_.getString ( "featureValueLabel"));
		panel.add ( valueLabel);
		JTextField valueField = new JTextField ( keyValuePair[1] );
		panel.add ( valueField );
		
		JOptionPane.showMessageDialog(panel_,panel);
		keyValuePair[0] = nameField.getText();
		keyValuePair[1] = valueField.getText();
		
	}

	
   
    public void deleteProperty(PropertiesTableModel table, int index)
    {
    	features_.remove(index);
        table.rowDeleted(index);

    }

    public void editProperty(PropertiesTableModel table, int index)
    {
        editProperty ( ( String[]) features_.get( index));
		table.refresh();
    }

   
    public void newProperty(PropertiesTableModel table)
    {
    	String[] feature = { "name" , "value"};
        editProperty ( feature );
        features_.addElement ( feature);
		table.rowInserted();
    }

}


	