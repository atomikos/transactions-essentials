//$Id: LoginDialog.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: LoginDialog.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:44  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2002/01/29 12:55:40  guy
//Added files again; deleted by mistake.
//
//Revision 1.1.1.1  2001/10/05 13:22:18  guy
//GUI module
//

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
