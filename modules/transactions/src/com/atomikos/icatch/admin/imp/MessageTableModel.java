//$Id: MessageTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: MessageTableModel.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:35  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:07  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:03:34  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/03/10 12:48:46  guy
//Updated admintool facility to use lists for all messages.
//

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
