//$Id: PropertiesPanel.java,v 1.2 2006/09/19 08:03:57 guy Exp $
//$Log: PropertiesPanel.java,v $
//Revision 1.2  2006/09/19 08:03:57  guy
//FIXED 10050
//
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
//Revision 1.5  2004/03/22 15:39:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.4.10.1  2003/08/21 20:32:09  guy
//*** empty log message ***
//
//Revision 1.4  2002/03/09 23:51:40  guy
//Added readonly mode.
//
//Revision 1.3  2002/01/29 12:55:40  guy
//Added files again; deleted by mistake.
//
//Revision 1.1.1.1  2001/10/05 13:22:18  guy
//GUI module
//

package com.atomikos.swing;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

 /**
  *
  *
  *A Properties Panel is a GUI tool component for displaying a table
  *of properties, and provides for creation, deletion and editing of
  *such properties.
  */
  
  public class PropertiesPanel implements ActionListener
  {
      private static final int NONE_SELECTED = -1;
      //indicates that no rows are selected
      
      private JPanel panel_;
      //the panel that contains the GUI elements
      
      private JButton newButton_, deleteButton_, editButton_; 
      //the three functions that the user can choose
      
      private PropertiesTableModel model_;
      //the underlying table model, for insertion/edit/delete
      
      private Vector listeners_;
      //the registered property listeners.
      
      private int currentRow_;
      //keeps track of selection
      //needed for edit/delete
      
      
      /**
       *Creates a new instance.
       *
       *@param model The PropertiesTableModel to use.
       */
       
      public PropertiesPanel ( PropertiesTableModel model )
      {
            this ( model , false );

      }
      
       /**
       *Creates a new instance.
       *
       *@param model The PropertiesTableModel to use.
       *@param readonly If true, then there will be no new or delete buttons,
       *and the edit button will be a view button.
       */
       
      public PropertiesPanel ( PropertiesTableModel model , boolean readonly )
      {
          
          
          panel_ = new JPanel ( );
          ResourceBundle messages =
                  ResourceBundle.getBundle (
                          "com.atomikos.swing.PropertiesPanelResourceBundle");
          panel_.setLayout ( new BorderLayout() );
          JTable table = new JTable ( model.getTableModel() );
          Dimension dim = new Dimension ( 200, 90 );
          table.setPreferredScrollableViewportSize ( dim );
          JScrollPane scroller = new JScrollPane ( table );
          panel_.add ( scroller , BorderLayout.CENTER );
          model_ = model;
          JPanel buttons = new JPanel();
          buttons.setLayout ( new FlowLayout() );
          newButton_ = new JButton ( messages.getString ( "newButtonName") );
          newButton_.addActionListener ( this );
          deleteButton_ = new JButton ( messages.getString ("deleteButtonName") );
          deleteButton_.setEnabled ( false );
          deleteButton_.addActionListener ( this );
          if ( ! readonly ) {
              buttons.add ( newButton_ );
              buttons.add ( deleteButton_ );
              editButton_ = new JButton ( messages.getString ( "editButtonName") );
          }
          else {
              editButton_ = new JButton ( messages.getString ( "viewButtonName" ) );
          }
          editButton_.setEnabled ( false );
          editButton_.addActionListener ( this );
          buttons.add ( editButton_ );
          panel_.add ( buttons , BorderLayout.SOUTH );
          
          listeners_ = new Vector();
          currentRow_ = NONE_SELECTED;
          
          
          table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          ListSelectionModel rowSM = table.getSelectionModel();
	
          rowSM.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    //Ignore extra messages.
		    if (e.getValueIsAdjusting()) 
			return;
		    
		    ListSelectionModel lsm =
			(ListSelectionModel)e.getSource();
		    if (lsm.isSelectionEmpty()) {
			//no rows are selected
			currentRow_ = NONE_SELECTED;
			editButton_.setEnabled ( false );
			deleteButton_.setEnabled ( false );
		    } else {
			currentRow_ = lsm.getMinSelectionIndex();
			editButton_.setEnabled ( true );
			deleteButton_.setEnabled ( true );
		    }
                }
            });
      }
      
      /**
       *@see ActionListener
       */
       
      public synchronized void actionPerformed ( ActionEvent e ) 
      {
          //determine which button, and call listeners for that button
          int i = -1;
          if ( e.getSource() == newButton_ )
              i = 0;
          else if ( e.getSource() == editButton_ )
              i = 1;
          else if ( e.getSource() == deleteButton_ )
              i = 2;
              
          Enumeration enumm = listeners_.elements();
          while ( enumm.hasMoreElements() ) {
              PropertyListener l = ( PropertyListener ) enumm.nextElement();
              switch ( i )  {
                  case 0: l.newProperty ( model_ );
                              break;
                  case 1: l.editProperty ( model_ , currentRow_ );
                              break;
                  case 2: l.deleteProperty ( model_ , currentRow_ );
                              deleteButton_.setEnabled ( false );
                              editButton_.setEnabled ( false );
                              break;
                              
                  default: break; 
              }
          }
      }
      
      /**
       *Adds a property listener.
       *@param l The listener to add.
       */
       
      public synchronized void addPropertyListener ( PropertyListener l )
      {
           listeners_.addElement ( l );
      }
      
      /**
       *Removes the given property listener.
       *@param l The listener to remove.
       */
       
      public synchronized void removePropertyListener ( PropertyListener l )
      {
          listeners_.remove ( l );
      }
      
      /**
       *Gets the actual Swing panel.
       *@return JPanel The panel.
       */
       
      public JPanel getPanel() 
      {
          return panel_; 
      }
      
  }
