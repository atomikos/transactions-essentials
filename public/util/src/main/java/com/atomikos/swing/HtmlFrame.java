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
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 *
 *
 *A frame to display an HTML document, and respond to clicks on 
 *hyperlinks by loading the clicked link.
 */
 
public class HtmlFrame 
implements HyperlinkListener, ActionListener
{
    private JFrame win_;
    //the window to display in
    
    private JEditorPane pane_;
    //the editor pane to load URLs
    
    private Stack stack_;
    //to go back
    private JButton back_;
    
    private URL lastUrl_;
    //added to stack on new load
    
    private JDialog dialog_;
    //null if not dialog mode.
    
    /**
     *Creates a new window that displays the given URL.
     *
     *@param url The URL to load.
     *@param parent If in dialog mode, this indicates which parent.
     *Null means a standalone app.
     *This determines the behaviour for closing the window.
     */
    
    public HtmlFrame ( URL url , JFrame parent ) throws IOException 
    {
        pane_ = new JEditorPane();
        JScrollPane scrollpane = new JScrollPane ( pane_ );
        pane_.setEditable ( false );
        pane_.setPage ( url );
        
        pane_.addHyperlinkListener ( this );
        ImageIcon backImageIcon = new ImageIcon (
                this.getClass().getResource ( "/toolbarButtonGraphics/navigation/Back24.gif" ));
        back_ = new JButton ( backImageIcon );
        back_.setEnabled ( false );
        back_.addActionListener ( this );
        JPanel panel = new JPanel ();
        panel.add ( back_ );
        
       // if ( !standalone )
//            win_.setDefaultCloseOperation ( WindowConstants.DISPOSE_ON_CLOSE );
//        
       
        stack_ = new Stack();
        lastUrl_ = url;
        
        if ( parent == null ) {
                
                
                win_ = new JFrame ( "HtmlViewer" );
                win_.getContentPane().setLayout ( new BorderLayout () );
                win_.getContentPane().add ( scrollpane , BorderLayout.CENTER );
                win_.getContentPane().add ( panel, BorderLayout.NORTH );
                
                win_.addWindowListener ( new WindowAdapter () {
                public void windowClosing ( WindowEvent e ) {
                    System.exit ( 0 );
                }
                } );
               Dimension defaultSize = new Dimension ( 600, 700 );
               win_.setSize ( defaultSize );
               win_.setVisible ( true );
        }
        else {
            //pop up in dialog box
            win_ = parent; //to make wait cursor work!
            JPanel pane = new JPanel();
            pane.setLayout ( new BorderLayout() );
            pane.add ( scrollpane, BorderLayout.CENTER );
            pane.add ( panel , BorderLayout.NORTH );
            pane.setSize ( new Dimension ( 500, 500 ) );
            final JDialog dialog = new JDialog ( parent, "HtmlViewer" , true );
            //dialog.setContentPane ( optionPane );
            dialog.setContentPane ( pane );
            //dialog.setSize ( new Dimension ( 800, 900) );
            //dialog.getContentPane().setSize ( new Dimension ( 500, 500 )) ;
           
            dialog.pack();
            dialog.setSize ( new Dimension ( 500, 500 ) );
            dialog.setVisible ( true );         
            dialog_ = dialog;   

            //JOptionPane.showMessageDialog ( parent , pane , 
               // "URLViewer" , JOptionPane.PLAIN_MESSAGE );
                
         
        }
 
    }
    
    private void setWaitCursor ( boolean wait ) 
    {
          Cursor cursor = null;
          if ( wait )
              cursor = Cursor.getPredefinedCursor ( Cursor.WAIT_CURSOR );
          else
              cursor = Cursor.getPredefinedCursor ( Cursor.DEFAULT_CURSOR );
              
          if ( dialog_ != null )
              dialog_.getContentPane().setCursor ( cursor );
          else
              win_.getContentPane().setCursor( cursor );
      }
    
    private void load ( URL url , boolean addToStack )
    {
         try {
            setWaitCursor ( true );
            pane_.setPage ( url );
            //System.err.println ( "Loading: " + url );
   
            if ( addToStack ) {
                stack_.push ( lastUrl_ );
                back_.setEnabled ( true );
            }
            lastUrl_ = url;
            
            
        }
        catch ( IOException err ) {
            System.err.println ( "Bad URL: " + url ); 
        }
        finally {
            setWaitCursor ( false ); 
        }
    }
    
    /**
     *@see HyperlinkListener
     */
     
    public void hyperlinkUpdate ( HyperlinkEvent e )
    {
        JEditorPane pane = ( JEditorPane ) e.getSource();
        if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
            URL url = e.getURL();
            load ( url, true );
        }
    }
    
    /**
     *@see ActionListener
     */
     
    public void actionPerformed ( ActionEvent e ) 
    {
        if ( e.getSource() == back_  ) {
            load ( ( URL ) stack_.pop() , false );
            if ( stack_.empty() )
                back_.setEnabled ( false );
        }
    }
    
 
    /**
     *Allows simple retrieval of command-line URL
     */
     
    public static void main ( String[] args ) 
    {
        if ( args.length !=1 ) {
            System.err.println ( "Usage: specify the URL to load" );
            System.exit ( 1 );
         
        }
        try {

            HtmlFrame frame = new HtmlFrame ( new URL ( args [0] ) , null );
           
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
    }


}
