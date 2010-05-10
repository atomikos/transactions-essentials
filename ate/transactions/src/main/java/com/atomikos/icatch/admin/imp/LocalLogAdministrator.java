//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: LocalLogAdministrator.java,v $
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
//Revision 1.6  2005/08/09 15:23:49  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.5  2004/10/11 13:39:31  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.4  2004/03/22 15:37:33  guy
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.3.2.2  2004/03/16 16:51:28  guy
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Corrected Resourcebundle getKey
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.3.2.1  2003/08/21 20:31:31  guy
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//:redesign
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.3  2003/03/11 06:38:58  guy
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: LocalLogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.2.2.2  2002/10/07 11:12:54  guy
//Changed Exit: now shutdown is in force mode.
//
//Revision 1.2.2.1  2002/09/30 06:40:51  guy
//Removed loading of background image, and added comments.
//
//Revision 1.2  2002/03/21 17:47:00  guy
//Changed to use UserTransactionService for shutdown, in order to propagate
//shutdown to all resources.
//
//Revision 1.1  2002/01/23 11:39:42  guy
//Added admin package to CVS.
//
//Revision 1.5  2002/01/03 10:13:44  guy
//Corrected comments.
//
//Revision 1.4  2001/11/28 13:47:34  guy
//Added getJFrame method to use for progress monitor.
//
//Revision 1.3  2001/11/28 12:52:22  guy
//Changed LogInspector to work with TransactionService, since
//working with rec. mgr. duplicates active coordinators and causes inconsistent
//results.
//
//Revision 1.1  2001/11/16 16:14:58  guy
//Added TmAdminTool GUI tool, for inspecting the local TM's transactions.
//

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
