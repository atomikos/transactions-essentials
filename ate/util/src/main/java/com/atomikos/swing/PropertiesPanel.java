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
