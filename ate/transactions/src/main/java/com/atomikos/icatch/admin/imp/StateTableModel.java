package com.atomikos.icatch.admin.imp;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.atomikos.swing.PropertiesTableModel;

/**
 * 
 * 
 * A table model for heuristic and termination states in the AdminTool.
 */

class StateTableModel extends AbstractTableModel implements
        PropertiesTableModel
{
    private Vector data_;

    StateTableModel ( Vector data )
    {
        data_ = data;
    }

    Vector getData ()
    {
        return data_;
    }

    public TableModel getTableModel ()
    {
        return this;
    }

    public int getRowCount ()
    {
        return data_.size ();
    }

    public int getColumnCount ()
    {
        return 1;
    }

    public Object getValueAt ( int row , int column )
    {
        StateDescriptor rec = (StateDescriptor) data_.elementAt ( row );
        if ( column == 0 )
            return rec.state;
        else
            return null;
    }

    public String getColumnName ( int col )
    {
        if ( col == 0 )
            return "Termination State";
        else
            return "unknown";
    }

    public boolean isCellEditable ( int row , int col )
    {
        return false;
    }

    public void rowDeleted ( int row )
    {
        fireTableRowsDeleted ( row, row );
    }

    public void refresh ()
    {
        fireTableRowsUpdated ( 0, data_.size () );
    }

    public void rowInserted ()
    {
        fireTableRowsInserted ( 0, data_.size () );
    }

}
