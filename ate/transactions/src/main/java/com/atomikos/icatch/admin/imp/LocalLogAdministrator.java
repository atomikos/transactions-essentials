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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.config.UserTransactionService;

/**
 * 
 * 
 * An administration tool for the TM, allowing log inspection. The inspection
 * window for viewing active transactions will <b>not show transactions that
 * start afterwards</b>. Only a (periodically refreshed) snapshot of those
 * transactions already present is shown. This is to prevent high transaction
 * throughputs from cluttering the display with update events.
 */

public class LocalLogAdministrator implements ActionListener, LogAdministrator
{

    private JFrame frame_;
    // the frame for displaying

    private UserTransactionService service_;
    // the tx service, needed for close shutdown

    private JMenuItem inspect_;
    // the log inspection menu

    private JMenuItem exit_;
    // exit menu, null if not standalone

    private LogControl control_;

    private ResourceBundle messages_;

    
    /**
     * Creates a new instance in standalone mode.
     */
    
    public LocalLogAdministrator () 
    {
    	this ( "Atomikos LogAdministrator" , true );
    }
    
    /**
     * Construct a new LocalLogAdministrator utility.
     * 
     * @param title
     *            The title for the tool window.
     * @param standalone
     *            True if this is the main window. If so, an Exit option will be
     *            presented that exits the VM.
     */

    public LocalLogAdministrator ( String title , boolean standalone )
    {
        frame_ = new JFrame ( title );

        frame_.setDefaultCloseOperation ( WindowConstants.DO_NOTHING_ON_CLOSE );
        messages_ = ResourceBundle
                .getBundle ( "com.atomikos.icatch.admin.imp.AdminToolResourceBundle" );

        JMenuBar mb = new JMenuBar ();
        JMenu m = new JMenu ( messages_.getString ( "mainMenuName" ) );
        m.setMnemonic ( KeyEvent.VK_M );
        inspect_ = new JMenuItem ( messages_
                .getString ( "showActiveTransactionsMenuItemName" )
                + "...", messages_.getString (
                "showActiveTransactionsMenuItemName" ).charAt ( 0 ) );
        inspect_.addActionListener ( this );
        // enable at register time only
        inspect_.setEnabled ( false );
        m.add ( inspect_ );
        if ( standalone ) {
            exit_ = new JMenuItem ( messages_.getString ( "exitMenuItemName" ),
                    messages_.getString ( "exitMenuItemName" ).charAt ( 0 ) );
            exit_.addActionListener ( this );
            m.add ( exit_ );
        }
        mb.add ( m );

        frame_.setJMenuBar ( mb );
        Dimension defaultSize = new Dimension ( 486, 380 );
        // size of background logo
        frame_.setSize ( defaultSize );

        // try {
        // //URL url = new URL ( "http://www.atomikos.com/Logo.GIF" );
        // //ImageIcon img = new ImageIcon ( url );
        // InputStream in = getClass().getResourceAsStream ( "Logo.GIF" );
        // BufferedInputStream bin = new BufferedInputStream ( in );
        // byte[] bytes = new byte [ bin.available() ];
        // bin.read ( bytes );
        // ImageIcon img = new ImageIcon ( bytes );
        // JLabel label = new JLabel ( img );
        // frame_.getContentPane().add ( label );
        //              
        // }
        // catch ( Exception e ) {
        // e.printStackTrace();
        // }

        frame_.setVisible ( true );
    }

    /**
     * @see LogAdministrator
     */

    public void registerLogControl ( LogControl control )
    {
        if ( control_ != null )
            throw new IllegalStateException ( "Second control registered" );
        control_ = control;
        // enable inspection menu
        inspect_.setEnabled ( true );
    }

    /**
     * @see LogAdministrator
     */

    public void deregisterLogControl ( LogControl control )
    {
        if ( control_ == control )
            control_ = null;
        inspect_.setEnabled ( false );
        frame_.dispose ();

    }

    /**
     * Initialize the tool to use a given recovery manager.
     * 
     * @param service
     *            The transaction service being used.
     */

    public void init ( UserTransactionService service )
    {
        service_ = service;
        frame_.show ();
    }

    /**
     * Get the frame we are using.
     * 
     * @return JFrame The swing frame.
     */

    public JFrame getJFrame ()
    {
        return frame_;
    }

    /**
     * @see ActionListener
     */

    public void actionPerformed ( ActionEvent ev )
    {
        try {
            if ( ev.getSource () == inspect_ ) {
                if ( control_ != null ) {
                    // System.err.println ( "Active txs event" );
                    try {
                        AdminTool tool = new AdminTool ( control_ );

                    } catch ( Exception e ) {
                        e.printStackTrace ();
                    }
                } else
                    System.err
                            .println ( "LogAdministrator: no control registered?" );
            } else if ( ev.getSource () == exit_ ) {

                try {
                    if ( service_ != null ) {
                        // null if error during startup and init not yet called!
                        service_.shutdown ( true );
                    }
                    System.exit ( 0 );
                } catch ( IllegalStateException il ) {
                    int n = JOptionPane.showConfirmDialog ( frame_, messages_
                            .getString ( "shutdownMessage" ), messages_
                            .getString ( "shutdownTitle" ),
                            JOptionPane.YES_NO_OPTION );
                    if ( n == JOptionPane.YES_OPTION ) {
                        service_.shutdown ( true );
                        System.exit ( 0 );
                    }
                }

            }
        } catch ( Exception e ) {
            e.printStackTrace ();
        }
    }
}
