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
