/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.beans;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.atomikos.swing.PropertiesTableModel;

 /**
  *
  *
  *A table model for the editing of index property values.
  */

class IndexedPropertyTableModel 
extends AbstractTableModel
implements PropertiesTableModel
{
    private Vector data_;
    
    private String header_;
    //what to display as name
    
    IndexedPropertyTableModel ( Vector data , String header )
    {
        data_ = data; 
        header_ = header;
    }
    
    public TableModel getTableModel () 
    {
        return this; 
    }
    public int getRowCount()
    {
        return data_.size();
    }
    public int getColumnCount()
    {
        return 1; 
    }
    public Object getValueAt ( int row, int column )
    {
        if ( column >0 )
            throw new IllegalArgumentException ( "No such column" );
            
        return data_.elementAt ( row );
    }
    
    public String getColumnName ( int col )
    {
        if ( col != 0 ) 
            throw new IllegalArgumentException ( "No such column" );
            
        return header_;
    }
    
    public boolean isCellEditable ( int row, int col )
    {
        return false; 
    }

    public void rowDeleted ( int row )
    {
        fireTableRowsDeleted ( row, row );
    }
    
    public void refresh()
    {
        fireTableRowsUpdated ( 0, data_.size() ); 
    }
    
    public void rowInserted ()
    {
        fireTableRowsInserted ( 0, data_.size() );
    }
  
}
