package com.atomikos.beans;

 /**
  *
  *
  *This class describes information of a bean's
  *indexed properties.
  */

public interface IndexedProperty
extends Property
{
     /**
      *Get the value of the indexed element.
      *@param i The index.
      *@return Object The value, or null if no getter or not set.
      *@exception PropertyException On error.
      */
      
    public Object getValue ( int i )
    throws PropertyException;
    
     /**
      *Test if readonly at index level.
      *@return boolean true if there is no index-level
      *setter method.
      */
      
    public boolean isIndexReadOnly();
    
//     /**
//      *Get an editor for this property.
//      *@return IndexedEditor An indexed editor.
//      */
//      
//    public IndexedEditor getIndexedEditor();
    
     /**
      *Set the value of the element with given index.
      *@param i The index.
      *@param arg The element value.
      *@exception ReadOnlyException If no appropriate setters.
      *@exception PropertyException If the set fails.
      */
      
    public void setValue ( int i , Object arg )
    throws ReadOnlyException, PropertyException;
    
     /**
      *Get the class of the indexed elements.
      *@return Class The class for the elements of the property.
      */
      
    public Class getIndexedType();
    
    
}
