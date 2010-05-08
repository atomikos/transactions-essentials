//$Id: LocalLogAdministratorTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Id: LocalLogAdministratorTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: LocalLogAdministratorTableModel.java,v $
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
//Revision 1.2.6.1  2004/06/14 08:09:12  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.8.1  2002/10/25 10:06:59  guy
//Merged with JTA110 changes.
//$Id: LocalLogAdministratorTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.2  2003/03/11 06:38:58  guy
//$Id: LocalLogAdministratorTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: LocalLogAdministratorTableModel.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.1.4.1  2002/10/09 17:13:33  guy
//Improved functionality.
//
//Revision 1.1  2002/01/23 11:39:42  guy
//Added admin package to CVS.
//

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
