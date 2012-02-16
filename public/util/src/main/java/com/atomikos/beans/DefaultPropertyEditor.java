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

import java.awt.Component;
import java.beans.PropertyEditorSupport;

 /**
  *
  *
  *A default Property Editor. Instances can be constructed
  *with custom component and tags.
  */

public class DefaultPropertyEditor
extends PropertyEditorSupport
{

    private String[] tags_;
    private PropertyEditorComponent component_;


    /**
     *Constructs a new instance. Note that the JavaBeans contract
     *strictly requires a no-argument constructor for third-party
     *tools to instantiate instances. However, since this class is
     *used internally only, it is not required to follow that convention.
     *
     *@param component The GUI component to use. Null if none.
     *@param tags A list of allowed values, null if not applicable.
     */

    public DefaultPropertyEditor (
      PropertyEditorComponent component , String[] tags )
    {
        super();
        tags_ = tags;
        component_ = component;
        component_.init ( this );
    }


     /**
      *Any restricted value list.
      *@return String[] null by default.
      */

    public String[] getTags()
    {
        return tags_;
    }

     /**
      *Check if paintable.
      *@return boolean false by default.
      */

    public boolean isPaintable()
    {
        return false;
    }

     /**
      *Sets the value as text.
      *@param text The text.
      */

    public void setAsText ( String text )
    {
        boolean allowedValue = true;

        if ( tags_ != null ) {
            allowedValue = false;
            for ( int i = 0 ; i < tags_.length ; i++ ) {
                if ( tags_[i].equals ( text ) )
                    allowedValue = true;
            }
        }

        if ( allowedValue ) {
            super.setValue ( text );
        }
        else {
            throw new IllegalArgumentException ( "Value not allowed: " + text );
        }
        //firePropertyChange();
    }

    public String getJavaInitializationString()
    {
        throw new RuntimeException ( "Not implemented" );
    }

    public boolean supportsCustomEditor()
    {
        return component_ != null ;
    }

    public Component getCustomEditor()
    {
        Component ret = null;

        if ( component_ != null ) {
            ret = component_.getComponent();
        }

        return ret;
    }


}
