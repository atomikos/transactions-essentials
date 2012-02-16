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
import java.beans.PropertyChangeListener;

 /**
  *
  *
  *An Editor for getting and setting properties 
  *based on  UI interaction.
  *Note that editors do NOT operate on the original property
  *but on a copy; in particular, any changes made through
  *the editor have to be explicitly retrieved by calling
  *getEditedObject and then set on the original property.
  *For indexed properties, the editor should deal with the
  *whole set at once.
  */
  
  public interface Editor
  {
       /**
        *Get the property for which we are editing.
        *
        *@return Property The property.
        */
        
      public Property getProperty();
      
       /**
        *Retrieves the edited object. 
        *@return Object The object that reflects any
        *changes made through the editor interface.
        */
        
      public Object getEditedObject();
      
       /**
        *Set the object to edit.
        *@param value The object to edit.
        */
        
      public void setEditedObject ( Object value );
      
       /**
        *Sets the value as a String.
        *Should only be called if getStringValue()
        *returns a value different from null.
        *
        *@param val The value.
        *@exception PropertyException If not supported.
        *
        */
        
      public void setStringValue ( String val )
      throws PropertyException;
      
      
       /**
        *Gets the value as a String.
        *@return String The value as a string, 
        *or null if not available. 
        *@exception PropertyException On error.
        */
      
      public String getStringValue()
      throws PropertyException;
      
    
       /**
        *Get any GUI component for editing.
        *@return Component The component,
        *or null if not available.
        */
        
      public Component getComponent();
      
      
       /**
        *Add a listener for property changes.
        *Clients should register as listeners for detecting when the
        *<b>local copy</b> of the property is set to a new value.
        *
        *@param PropertyChangeListener l The listener.
        */
        
      public void addPropertyChangeListener ( PropertyChangeListener l );
      
       /**
        *Removes a property change listener.
        *@param l The listener.
        */
        
      public void removePropertyChangeListener ( PropertyChangeListener l );
  }
