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

import java.beans.IndexedPropertyDescriptor;
import java.lang.reflect.Method;

  /**
   *
   *
   *An implementation of indexed property.
   */

class IndexedPropertyImp
extends PropertyImp
implements IndexedProperty
{

    private IndexedPropertyDescriptor descriptor_;

    /**
      *Creates a new instance for a given editor
      *to delegate to.
      *@param property The property.
      *@param editor The delegate, or null if none.
      */

    IndexedPropertyImp ( Object bean , IndexedPropertyDescriptor descriptor )
    throws PropertyException
    {
        super ( bean , descriptor );
        descriptor_ = descriptor;
    }

     /**
      *@see IndexedProperty
      */

    public Object getValue ( int i )
    throws PropertyException
    {
         Object ret = null;

         try {
             Method m = descriptor_.getIndexedReadMethod();
             Integer index = new Integer ( i );
             Object[] args = new Object[1];
             args[0] = index;
             ret = m.invoke ( getBean() , args );
         }
         catch ( Exception e ) {
              throw new PropertyException ( "Error getting property" , e );
         }

         return ret;
    }

     /**
      *@see IndexedProperty
      */

    public boolean isIndexReadOnly()
    {
        return descriptor_.getIndexedWriteMethod() == null;
    }

    /**
     *@see IndexedProperty
     */

    public void setValue ( int i , Object arg )
    throws ReadOnlyException, PropertyException
    {
        if ( isIndexReadOnly() ) {
              throw new ReadOnlyException ( "Property is readonly" );
          }

          try {
              Object[] args = new Object[2];
              args[0] = new Integer ( i );
              args[1] = arg;
              Method method = descriptor_.getIndexedWriteMethod();
              method.invoke ( getBean() , args );
          }
          catch ( Exception e ) {
              throw new PropertyException ( "Error in setting value" , e );
          }
    }

     /**
      *@see IndexedProperty
      */

    public Class getIndexedType()
    {
        return descriptor_.getIndexedPropertyType();
    }

    /**
     *@see Property
     */

    public IndexedProperty getIndexedProperty()
    {
        return this;

    }

    //     /**
//      *@see IndexedProperty
//      */
//
//    public IndexedEditor getIndexedEditor()
//    {
//          PropertyEditor pedit =
//              PropertyEditorManager.findEditor ( descriptor_.getPropertyType() );
//          return new IndexedEditorImp ( this , pedit );
//    }
}
