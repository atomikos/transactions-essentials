package com.atomikos.vendor;

import java.util.Vector;

import com.atomikos.swing.AbstractPropertiesTableModel;

/**
 * 
 * Copyright &copy; 2003 Guy Pardon, Atomikos. All rights reserved.
 * 
 * @author guy
 *
 * A table model for node-locking IP addresses.
 */

public class HostsTableModel extends AbstractPropertiesTableModel
{

    /**
     * @param data
     * @param columnNames
     */
    
    public HostsTableModel(Vector data, String[] columnNames)
    {
        super(data, columnNames);
        
    }

   
    public Object getValueAt(int row, int column)
    {
    	Object ret = "UNKNOWN VALUE";
       	Vector data = getData();
       	if ( column == 0 ) ret = data.elementAt ( row );
       	return ret;
     }

}
