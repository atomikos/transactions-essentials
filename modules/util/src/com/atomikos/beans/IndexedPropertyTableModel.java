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
