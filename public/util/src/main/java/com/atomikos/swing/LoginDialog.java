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

package com.atomikos.swing;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 *
 *A generic login dialog that allows username and passwd to
 *be entered by the user.
 */


public class LoginDialog extends JDialog
{

    private JTextField name_;
    private JPasswordField passwd_;
    private boolean valid_;


    /**
     *Constructs a new instance with a given title.
     *@param frame The parent frame.
     *@param title The title for the frame.
     *@param message A description or name of the source that is
     *being logged into.
     */

    public LoginDialog ( JFrame frame , String title, String message  )
    {
         super ( frame, title, true );

        name_ = new JTextField ( 8 );
        JLabel nameLabel = new JLabel ( "Username" );
        passwd_ = new JPasswordField ( 8 );
        JLabel passwdLabel = new JLabel ( "Password" );


        JPanel panel = new JPanel();
        panel.setLayout ( new GridLayout ( 0 , 1 ) );
        //JLabel label1 = new JLabel ( "Login for" );
        JLabel label2 = new JLabel ( message );
        //panel.add ( label1 );
        panel.add ( label2 );
        panel.add ( nameLabel );panel.add ( name_ );
        panel.add ( passwdLabel );  panel.add ( passwd_ );
        valid_ = false;
        final String connect = "Connect" ;
        final String cancel = "Cancel";
        String [] options = { connect, cancel };
        final JOptionPane optionPane =
            new JOptionPane ( panel, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options [0] );
        setContentPane ( optionPane );
        setDefaultCloseOperation ( DO_NOTHING_ON_CLOSE );
        optionPane.addPropertyChangeListener (
            new PropertyChangeListener () {
                public void propertyChange ( PropertyChangeEvent e ) {
                    if ( isVisible() && e.getSource() == optionPane ) {
                        if ( optionPane.getValue() == connect )
                            valid_ = true;
                        setVisible ( false );
                        dispose();
                    }
                }
            }
         );
         setSize ( 250,220 );
    }



    /**
     *Gets the user name entered by the user.
     *
     *@return String the username as entered.
     */

    public String getUserName()
    {
        return name_.getText();
    }

    /**
     *Gets the password entered by the user.
     *
     *@return String The password as entered by the user.
     */

    public String getPassword()
    {
        return new String ( passwd_.getPassword() );
    }

    /**
     *Checks if the Connect button has been pressed.
     *
     *@return boolean True iff the Connect button was pressed.
     */

    public boolean isValid()
    {
        return valid_;
    }


    public static void main ( String[] args )
    {
        try {
            JFrame frame = new JFrame ( "Test" );
            LoginDialog dialog = new LoginDialog ( frame, "Test", "Test" );
            dialog.show();
            if ( dialog.isValid() )
                System.out.println ( "User entered " + dialog.getUserName() + "/" +
                    dialog.getPassword() );

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            System.exit ( 0 );
        }
    }

}
