package com.atomikos.beans;
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
