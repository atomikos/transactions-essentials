//$Id: StateTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: StateTableModel.java,v $
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
//Revision 1.2  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/03/10 02:09:17  guy
//First version with inclusion of detailed heuristic and termination listing.
//

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
