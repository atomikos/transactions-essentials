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

package com.atomikos.icatch.admin.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.atomikos.icatch.admin.AdminTransaction;

class LocalLogAdministratorTableModel extends AbstractTableModel
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(LocalLogAdministratorTableModel.class);

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
