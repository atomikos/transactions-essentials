/**
 *
 *
 *A table model for listing a set of heuristic messages of
 *the same state.
 */

package com.atomikos.icatch.admin.imp;

import javax.swing.table.AbstractTableModel;

import com.atomikos.icatch.HeuristicMessage;

class MessageTableModel extends AbstractTableModel
{
    private HeuristicMessage[] data_;

    MessageTableModel ( HeuristicMessage[] data )
    {
        data_ = data;
    }

    public int getRowCount ()
    {
        return data_.length;
    }

    public int getColumnCount ()
    {
        return 1;
    }

    public Object getValueAt ( int row , int column )
    {
        HeuristicMessage msg = data_[row];
        if ( column == 0 )
            return msg.toString ();
        else
            throw new RuntimeException ( "Invalid column" );
    }

    public String getColumnName ( int col )
    {
        if ( col == 0 )
            return "Descriptions";
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
        fireTableRowsUpdated ( 0, data_.length );
    }

}
