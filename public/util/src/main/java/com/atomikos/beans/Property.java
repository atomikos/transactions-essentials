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
  *A property allows details to be retrieved and set
  *for individual bean properties.
  */

public interface Property
{
      
      /**
       *Get the name of this property.
       *@return String The name.
       */
       
      public String getName();
      
       /**
        *Get the description of the property.
        *@return String A description, defaults
        *to the same value as getName, but 
        *may be more elaborate in some cases
        *(depending on the underlying bean 
        *implementation).
        */
        
      public String getDescription();
      
      /**
       *Get the class of this property.
       *@return Class The class.
       */
       
      public Class getType();
      
       /**
        *Test if the property is for expert users only.
        *@return boolean true if for experts only.
        */
        
      public boolean isExpert();
      
       /**
        *Test if the property should definitely be configured.
        *@return boolean true if the property should definitely be
        *configured.
        */
        
      public boolean isPreferred();
      
       /**
        *Test if the property is not for GUI configuration.
        *@return boolean True if a GUI should NOT show this.
        */
        
      public boolean isHidden();
      
       /**
        *Get an editor component for the property.
        *This allows easy, String-based interaction for get and set.
        *@return Editor The editor, or null if not available.
        *This may happen, for instance, if the type is a custom type, or if the
        *property is not meant to be changed.
        *@exception PropertyException On error.
        */
        
      public Editor getEditor()
      throws PropertyException;
      
       /**
        *Gets the allowed values for this property.
        *Useful in case of enummeration-restricted 
        *properties.
        *
        *@return String[] The allowed values, or null if not
        *applicable.
        */
        
      public String[] getAllowedValues();
     
      /**
       *Get the value of this property.
       *@return Object The current value, or null if not available.
       *For indexed properties, this will be the entire set of values.
       *@exception PropertyException On error.
       */
       
      public Object getValue()
      throws PropertyException;
      
       /**
        *Tests if a property is readonly.
        *@return boolean true if readonly.
        */
        
      public boolean isReadOnly();
      
       /**
        *Get an indexed property inspector if available.
        *@return IndexedProperty The indexed property,
        *or null for simple properties.
        */
        
      public IndexedProperty getIndexedProperty();
      
       /**
        *Set the value of the property to the given argument.
        *@param arg The argument. For indexed properties,
        *this is the entire array. The necessary conversion
        *from wrapper types to primitive types will happen
        *internally.
        *@exception ReadOnlyException If there is no setter
        *method with the required argument.
        *@exception PropertyException If the set fails.
        */
        
      public void setValue ( Object arg )
      throws ReadOnlyException, PropertyException;
}
