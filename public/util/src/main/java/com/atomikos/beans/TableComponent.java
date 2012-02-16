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

package com.atomikos.beans;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.atomikos.swing.PropertiesPanel;
import com.atomikos.swing.PropertiesTableModel;
import com.atomikos.swing.PropertyListener;

 /**
  *
  *
  *A component for editing indexed properties as a whole.
  */

public class TableComponent
extends AbstractPropertyEditorComponent
implements PropertyListener
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(TableComponent.class);

    private Vector data_;
    //the underlying data for the table view
  
    private PropertiesPanel panel_;
    //the GUI component to return
    
    private IndexedProperty property_;
    
     /**
      *Helper method to construct a new object.
      *@param clazz The class to construct a new object for.
      *Should be a primitive class.
      *@return Object The new object, or null if cancelled by user.
      *@exception PropertyException If the class is not a primitive class.
      */
      
    private static Object constructNewObject ( Class clazz )
    throws PropertyException
    {
         Object ret = null;
         JPanel panel = new JPanel();
         JTextField text = null;
         JCheckBox checkbox = null;
         Class wrapperClass = PrimitiveClasses.getWrapperClass ( clazz );
         if ( wrapperClass == null && !clazz.equals ( String.class ) ) {
              throw new PropertyException ( 
              "Not a supported class: " + clazz.getName() , null );
         }
         if ( wrapperClass != null && wrapperClass.equals ( Boolean.class ) ) {
            //show a boolean dialog
            checkbox = new JCheckBox ( "Check if true" , false );
            panel.add ( checkbox );
         }
         else {
            //show a text field dialog
            text = new JTextField ( "Enter value" );
            panel.add ( text );
         }
         int answer = JOptionPane.showConfirmDialog ( 
            null , panel , "New Element Property" ,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE );
        
        if ( answer == JOptionPane.OK_OPTION ) {
            if ( checkbox != null ) {
                ret = new Boolean ( checkbox.isSelected() ).toString();
            }
            else {
                ret = text.getText();
            }
        }
        
        return ret;

    }
    
    public TableComponent ( IndexedProperty property )
    throws PropertyException
    {
        super();
        property_ = property;
        Object[] data =  ( Object[] ) property.getValue();
        data_ = new Vector();
        for ( int i = 0 ; i < data.length ; i++ ) {
             data_.addElement ( data[i] );
        }
        IndexedPropertyTableModel table = 
            new IndexedPropertyTableModel ( data_ , property.getName() );
        panel_ = new PropertiesPanel ( table , false );
        panel_.getPanel().setToolTipText ( property.getDescription() );
        panel_.addPropertyListener ( this );
    }
    
    public Component getComponent()
    {
        return panel_.getPanel(); 
    }
    
    public void newProperty ( PropertiesTableModel table )
    {
        Object object = null;
        try {
            object = 
                constructNewObject ( property_.getIndexedType() ) ;
        }
        catch ( PropertyException pe ) {
            throw new RuntimeException ( pe.getMessage() );
        }
        if ( object != null ) {
            data_.addElement ( object );
            table.rowInserted();
            Object value = java.lang.reflect.Array.newInstance ( property_.getIndexedType() , data_.size() );
            getPropertyEditor().setValue ( value );
        }
    }
    
    public void deleteProperty ( PropertiesTableModel table , int index )
    {
        Object toDelete = data_.elementAt ( index );
        data_.remove ( toDelete );
        table.rowDeleted ( index );
        Object value = java.lang.reflect.Array.newInstance ( property_.getIndexedType() , data_.size() );
        getPropertyEditor().setValue ( value );
    }
    
    public void editProperty ( PropertiesTableModel table, int index ) 
    {
        //do nothing: editing is not supported; delete and insert instead
        JOptionPane.showMessageDialog ( null , "To edit, please delete and create a new value..." );
    }
}
