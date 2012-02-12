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

/**
 *
 *
 *A table model for listing a set of heuristic messages of
 *the same state.
 */

package com.atomikos.icatch.admin.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.swing.table.AbstractTableModel;

import com.atomikos.icatch.HeuristicMessage;

class MessageTableModel extends AbstractTableModel
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(MessageTableModel.class);

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
