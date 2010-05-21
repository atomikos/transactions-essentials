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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.persistence.LogException;
import com.atomikos.swing.ExtensionsFileFilter;
import com.atomikos.swing.PropertiesPanel;
import com.atomikos.swing.PropertiesTableModel;
import com.atomikos.swing.PropertyListener;

/**
 * 
 * 
 * An inspection tool for viewing and editing log contents. To be used with
 * care, since editing can influence the 2PC interactions in the system!
 */

class AdminTool implements PropertyListener, ListSelectionListener
{
    private LogControl control_;
    // the tx service, null if standalone

    private JFrame frame_;

    private LocalLogAdministratorTableModel model_;

    private Vector data_;
    // the data, for easy editing of coordinator states
    // and easy inspection on selection of a row in the table

    private javax.swing.Timer timer_;
    // for refreshing table contents

    private JFileChooser fc_;
    // for keeping context of where to archive

    private PropertyListener pListener_;
    // the property listener
    // that listens on the state list on transaction inspection

    private ResourceBundle messages_;

    // locale-specific text

    /**
     * Check whether more info should be displayed for the given state. Based on
     * this value, a GUI indication will be shown that marks the row as having
     * details.
     */

    static boolean hasDetails ( int state )
    {
        boolean ret = false;
        switch ( state ) {
        case AdminTransaction.STATE_PREPARED:
            ret = true;
            break;
        case AdminTransaction.STATE_HEUR_MIXED:
            ret = true;
            break;
        case AdminTransaction.STATE_HEUR_HAZARD:
            ret = true;
            break;
        case AdminTransaction.STATE_HEUR_COMMITTED:
            ret = true;
            break;
        case AdminTransaction.STATE_HEUR_ABORTED:
            ret = true;
            break;
        case AdminTransaction.STATE_COMMITTING:
            ret = false;
            break;
        case AdminTransaction.STATE_ABORTING:
            ret = false;
            break;
        case AdminTransaction.STATE_TERMINATED:
            ret = false;
            break;

        default:
            break;
        }

        return ret;
    }

    /**
     * Convert the given int state.
     * 
     * @param state
     *            The given int state.
     * @return Object The object state, or null if not found.
     */

    static Object convertState ( int state )
    {
        Object ret = TxState.ACTIVE;

        switch ( state ) {
        case AdminTransaction.STATE_PREPARED:
            ret = TxState.IN_DOUBT;
            break;
        case AdminTransaction.STATE_HEUR_MIXED:
            ret = TxState.HEUR_MIXED;
            break;
        case AdminTransaction.STATE_HEUR_HAZARD:
            ret = TxState.HEUR_HAZARD;
            break;
        case AdminTransaction.STATE_HEUR_COMMITTED:
            ret = TxState.HEUR_COMMITTED;
            break;
        case AdminTransaction.STATE_HEUR_ABORTED:
            ret = TxState.HEUR_ABORTED;
            break;
        case AdminTransaction.STATE_COMMITTING:
            ret = TxState.COMMITTING;
            break;
        case AdminTransaction.STATE_ABORTING:
            ret = TxState.ABORTING;
            break;
        case AdminTransaction.STATE_TERMINATED:
            ret = TxState.TERMINATED;
            break;

        default:
            break;
        }

        return ret;
    }

    private static StateDescriptor getStateDescriptor ( AdminTransaction tx ,
            int heuristicState )
    {
        Object state = convertState ( heuristicState );
        HeuristicMessage[] msgs = tx.getHeuristicMessages ( heuristicState );
        return new StateDescriptor ( state, msgs );
    }

    private static Vector getStateDescriptors ( Object admintx )
    {
        AdminTransaction tx = (AdminTransaction) admintx;
        Vector ret = new Vector ();
        StateDescriptor desc = null;

        desc = getStateDescriptor ( tx, AdminTransaction.STATE_HEUR_MIXED );
        if ( !(desc.messages == null || desc.messages.length == 0) )
            ret.addElement ( desc );

        desc = getStateDescriptor ( tx, AdminTransaction.STATE_HEUR_HAZARD );
        if ( !(desc.messages == null || desc.messages.length == 0) )
            ret.addElement ( desc );

        desc = getStateDescriptor ( tx, AdminTransaction.STATE_HEUR_COMMITTED );
        if ( !(desc.messages == null || desc.messages.length == 0) )
            ret.addElement ( desc );

        desc = getStateDescriptor ( tx, AdminTransaction.STATE_HEUR_ABORTED );
        if ( !(desc.messages == null || desc.messages.length == 0) )
            ret.addElement ( desc );

        desc = getStateDescriptor ( tx, AdminTransaction.STATE_TERMINATED );
        if ( !(desc.messages == null || desc.messages.length == 0) )
            ret.addElement ( desc );

        return ret;
    }

    private static JPanel getMessagePanel ( HeuristicMessage[] msgs )
    {
        MessageTableModel model = new MessageTableModel ( msgs );
        JTable table = new JTable ( model );
        table.setPreferredScrollableViewportSize ( new Dimension ( 300, 70 ) );
        JScrollPane scrollPane = new JScrollPane ( table );
        JPanel ret = new JPanel ();
        ret.setLayout ( new BorderLayout () );
        ret.add ( scrollPane, BorderLayout.CENTER );
        return ret;
    }

    AdminTool ( LogControl control )
    {
        messages_ = ResourceBundle
                .getBundle ( "com.atomikos.icatch.admin.imp.AdminToolResourceBundle" );
        frame_ = new JFrame ( messages_.getString ( "adminToolTitle" ) );
        control_ = control;
        // add timer to reflect updated tx states
        timer_ = new javax.swing.Timer ( 7000, new ActionListener () {
            public void actionPerformed ( ActionEvent evt )
            {
                // System.out.println ( "Timer refresh event" );
                model_.refresh ();
                // System.out.println ( "Timer refresh done" );
            }
        } );
        timer_.start ();
        init ();
    }

    private void init ()
    {
        JTable table = null;
        try {
            table = getJTable ();
        } catch ( LogException e ) {
            throw new RuntimeException ( e.getMessage () );
        }
        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane ( table );
        File tmpfile = new File ( "archive.txt" );
        try {
            tmpfile.createNewFile ();
        } catch ( IOException io ) {
            // file creation failed; this merely implies that no default archive
            // file exists
        }
        fc_ = new JFileChooser ( tmpfile );
        String[] extensions = { "txt" };
        fc_.setFileFilter ( new ExtensionsFileFilter ( extensions ) );
        fc_.setDialogTitle ( messages_.getString ( "appendDialogTitle" ) );
        fc_.setFileSelectionMode ( JFileChooser.FILES_ONLY );
        // Add the scroll pane to this window.
        frame_.getContentPane ().add ( scrollPane, BorderLayout.CENTER );

        frame_.addWindowListener ( new WindowAdapter () {

            public void windowClosing ( WindowEvent e )
            {
                if ( timer_ != null )
                    timer_.stop ();

            }
        } );
        frame_.pack ();
        frame_.setVisible ( true );
        pListener_ = this;
    }

    private void forget ( Object part , int row )
    {

        AdminTransaction p = (AdminTransaction) part;
        p.forceForget ();
        model_.rowDeleted ( row );

    }

    private void commit ( Object part , int row )
    {
        try {
            AdminTransaction p = (AdminTransaction) part;
            p.forceCommit ();
            model_.refresh ();
        } catch ( Exception e ) {
            e.printStackTrace ();
        }
    }

    private void rollback ( Object part , int row )
    {
        try {
            AdminTransaction p = (AdminTransaction) part;
            p.forceRollback ();
            model_.refresh ();
        } catch ( Exception e ) {
            e.printStackTrace ();
        }
    }

    private String getHeuristicDetails ( AdminTransaction p , int heuristicState )
    {
        StringBuffer ret = new StringBuffer ();
        HeuristicMessage[] msgs = p.getHeuristicMessages ( heuristicState );
        for ( int i = 0; i < msgs.length; i++ ) {
            if ( i == 0 )
                ret.append ( convertState ( heuristicState ) + ": " );
            ret.append ( msgs[i] );
            if ( i < msgs.length - 1 )
                ret.append ( " -- " );
        }
        return ret.toString ();
    }

    private String getMessages ( Object part )
    {
        HeuristicMessage[] msgs = null;
        StringBuffer ret = new StringBuffer ();

        AdminTransaction p = (AdminTransaction) part;
        Object state = convertState ( p.getState () );

        // in the general case, no distinction has to be made between different
        // outcomes of different participants.
        msgs = p.getHeuristicMessages ();

        if ( p.wasCommitted () )
            ret.append ( messages_.getString ( "commitAttemptedMessage" ) );
        else if ( p.getState () != AdminTransaction.STATE_PREPARED )
            ret.append ( messages_.getString ( "rollbackAttemptedMessage" ) );

        for ( int i = 0; i < msgs.length; i++ ) {
            ret.append ( msgs[i] );
            if ( i < msgs.length - 1 )
                ret.append ( " -- " );
        }
        if ( p.getState () == AdminTransaction.STATE_HEUR_MIXED
                || p.getState () == AdminTransaction.STATE_HEUR_HAZARD ) {

            // in this case, we should add extra information about
            // which task was in a different outcome
            ret.append ( messages_.getString ( "ofWhichMessage" ) );
            ret.append ( getHeuristicDetails ( p,
                    AdminTransaction.STATE_HEUR_COMMITTED ) );
            ret.append ( getHeuristicDetails ( p,
                    AdminTransaction.STATE_HEUR_ABORTED ) );
            ret.append ( getHeuristicDetails ( p,
                    AdminTransaction.STATE_HEUR_MIXED ) );
            ret.append ( getHeuristicDetails ( p,
                    AdminTransaction.STATE_HEUR_HAZARD ) );

        }

        return ret.toString ();

    }

    /**
     * Gets a JTable with 2 cols (1 for root ID, 1 for the global tx state). The
     * table has a row for each coordinator in the log.
     * 
     * @return JTable The table.
     */

    private JTable getJTable () throws LogException
    {
        JTable table = null;
        // this will be the returned table
        Vector coordinators = new Vector ();

        AdminTransaction[] txs = control_.getAdminTransactions ();
        if ( txs != null && txs.length > 0 ) {
            for ( int i = 0; i < txs.length; i++ ) {
                coordinators.addElement ( txs[i] );
            }
        }
        data_ = coordinators;

        LocalLogAdministratorTableModel model = new LocalLogAdministratorTableModel (
                coordinators );

        table = new JTable ( model );
        table.setSelectionMode ( ListSelectionModel.SINGLE_SELECTION );
        // only a single row can be selected at a time
        model_ = model;

        ListSelectionModel rowSM = table.getSelectionModel ();

        rowSM.addListSelectionListener ( this );

        table.setPreferredScrollableViewportSize ( new Dimension ( 500, 70 ) );

        return table;
    }

    /**
     * @see ListSelectionListener
     */

    public void valueChanged ( ListSelectionEvent e )
    {
        processEvent ( e );
    }

    /**
     * Utility method to handle display event of one particular tx.
     * 
     */

    void processEvent ( ListSelectionEvent e )
    {
        // Ignore extra messages.
        if ( e.getValueIsAdjusting () )
            return;

        try {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource ();
			if ( lsm.isSelectionEmpty () ) {
			    // no rows are selected
			} else {
			    int selectedRow = lsm.getMinSelectionIndex ();
			    AdminTransaction rec = (AdminTransaction) data_
			            .elementAt ( selectedRow );
			    Object id = null;
			    Object state = null;

			    id = rec.getTid ();
			    state = convertState ( rec.getState () );

			    if ( !state.equals ( TxState.IN_DOUBT ) ) {
			        if ( hasDetails ( rec.getState () ) ) {
			            Object[] options = {
			                    messages_.getString ( "forgetNoArchiveOption" ),
			                    messages_.getString ( "forgetAndArchiveOption" ),
			                    messages_.getString ( "keepInLogOption" ) };
			            Vector descriptors = getStateDescriptors ( rec );
			            StateTableModel table = new StateTableModel ( descriptors );
			            PropertiesPanel panel = new PropertiesPanel ( table, true );
			            String outcome = null;
			            if ( rec.wasCommitted () )
			                outcome = messages_.getString ( "commitOutcomeMessage" );
			            else
			                outcome = messages_
			                        .getString ( "rollbackOutcomeMessage" );
			            panel.addPropertyListener ( pListener_ );
			            int n = JOptionPane.showOptionDialog ( frame_, panel
			                    .getPanel (), outcome + id.toString (),
			                    JOptionPane.YES_NO_CANCEL_OPTION,
			                    JOptionPane.QUESTION_MESSAGE, null, options,
			                    options[2] );

			            if ( n == JOptionPane.YES_OPTION
			                    || n == JOptionPane.NO_OPTION ) {
			                // System.out.println ( "Terminating transaction" );
			                if ( n == JOptionPane.NO_OPTION ) {
			                    // ask user where to archive
			                    fc_.setDialogTitle ( messages_
			                            .getString ( "appendDialogTitle" ) );
			                    int ret = fc_.showOpenDialog ( frame_ );
			                    if ( ret == JFileChooser.APPROVE_OPTION ) {
			                        try {
			                            File file = fc_.getSelectedFile ();
			                            FileWriter writer = new FileWriter ( file
			                                    .getPath (), true );
			                            writer
			                                    .write ( messages_
			                                            .getString ( "rootTransactionMessage" )
			                                            + id.toString () + " " );
			                            writer.write ( getMessages ( data_
			                                    .elementAt ( selectedRow ) ) );
			                            writer.write ( "\r" );
			                            writer.flush ();
			                            writer.close ();
			                            forget ( data_.elementAt ( selectedRow ),
			                                    selectedRow );
			                        } catch ( Exception err ) {
			                            err.printStackTrace ();
			                        }
			                    }
			                }

			                else {
			                    // Forget no archive
			                    forget ( data_.elementAt ( selectedRow ),
			                            selectedRow );
			                }
			            } else {
			                // CANCEL option -> just invalidate selection, to allow
			                // next pop-up to happen
			                lsm.removeSelectionInterval ( selectedRow, selectedRow );
			            }
			        } else {
			        	//no details available -> show warning for the user
			        	String message = messages_.getString ( "noDetailsAvailableMessage" );
			        	String title = messages_.getString( "noDetailsAvailableTitle" );
			        	JOptionPane.showMessageDialog (
			                    frame_ , message , title , JOptionPane.INFORMATION_MESSAGE );
			        }
			    } else {
			        // indoubt instance -> present commit/rollback/keep option
			        Object[] options = { messages_.getString ( "commitOption" ),
			                messages_.getString ( "rollbackOption" ),
			                messages_.getString ( "keepInLogOption" ) };
			        AdminTransaction tx = (AdminTransaction) data_
			                .elementAt ( selectedRow );
			        JPanel panel = getMessagePanel ( tx.getHeuristicMessages () );

			        int n = JOptionPane
			                .showOptionDialog ( frame_, panel, messages_
			                        .getString ( "selectedTransactionMessage" )
			                        + id.toString (),
			                        JOptionPane.YES_NO_CANCEL_OPTION,
			                        JOptionPane.QUESTION_MESSAGE, null, options,
			                        options[2] );

			        if ( n == JOptionPane.YES_OPTION ) {
			            commit ( data_.elementAt ( selectedRow ), selectedRow );
			        } else if ( n == JOptionPane.NO_OPTION ) {
			            rollback ( data_.elementAt ( selectedRow ), selectedRow );
			        }
			        // CANCEL option -> just invalidate selection, to allow next
			        // pop-up to happen
			        lsm.removeSelectionInterval ( selectedRow, selectedRow );
			    }

			    // clear the selection in order to allow new selection events
			    // IMPORTANT to allow re-selection events!
			    lsm.clearSelection ();
			}
		} catch ( Exception err ) {
			err.printStackTrace();
		}

    }

    /**
     * @see PropertyListener
     */

    public void newProperty ( PropertiesTableModel model )
    {
        throw new RuntimeException ( "Should not be called" );
    }

    /**
     * @see PropertyListener
     */

    public void deleteProperty ( PropertiesTableModel model , int index )
    {
        throw new RuntimeException ( "Should not be called" );
    }

    /**
     * @see PropertyListener
     */

    public void editProperty ( PropertiesTableModel model , int index )
    {
        Vector data = ((StateTableModel) model).getData ();
        StateDescriptor desc = (StateDescriptor) data.elementAt ( index );
        JPanel panel = getMessagePanel ( desc.messages );
        int answer = JOptionPane.showConfirmDialog ( frame_, panel, messages_
                .getString ( "stateDetailsTitle" ), JOptionPane.PLAIN_MESSAGE );

    }

}
