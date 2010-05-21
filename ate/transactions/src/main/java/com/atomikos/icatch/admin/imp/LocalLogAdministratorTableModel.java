package com.atomikos.icatch.admin.imp;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.atomikos.icatch.admin.AdminTransaction;

class LocalLogAdministratorTableModel extends AbstractTableModel
{
    private Vector data_;

    LocalLogAdministratorTableModel ( Vector data )
    {
        data_ = data;
    }

    public int getRowCount ()
    {
        return data_.size ();
    }

    public int getColumnCount ()
    {
        return 3;
    }

    public Object getValueAt ( int row , int column )
    {
        AdminTransaction tx = (AdminTransaction) data_.elementAt ( row );
        if ( column == 0 )
            return tx.getTid ();
        else if ( column == 1 )
            return AdminTool.convertState ( tx.getState () );
        else {
            if ( AdminTool.hasDetails ( tx.getState () ) )
                return new String ( "Click row for details." );
            else
                return new String ( "" );
        }

    }

    public String getColumnName ( int col )
    {
        if ( col == 0 )
            return "Root transaction ID";
        else if ( col == 1 )
            return "2PC state";
        else if ( col == 2 )
            return "Remarks";
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

}
