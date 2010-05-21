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

package  com.atomikos.beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

 /**
  *
  *
  *An adapter class for property change events.
  */

class PropertyChangeAdapter
implements PropertyChangeListener
{
    private PropertyChangeSupport support_;
    //the listeners to notify with an adapted event
  
    private Editor source_;
    //the source for adapted events
    
    PropertyChangeAdapter ( Editor source  )
    {
        source_ = source;
        support_ = new PropertyChangeSupport ( source );
    } 
    
    public void propertyChange ( PropertyChangeEvent event )
   {
        PropertyChangeEvent adapted = new PropertyChangeEvent ( source_ , null , null , null );
        support_.firePropertyChange ( adapted );
        //System.err.println ( "PropertyChangeAdapter: propertyChange" );
   }
   
   public void addPropertyChangeListener ( PropertyChangeListener l )
   {
        support_.addPropertyChangeListener ( l );
   }
   
   public void removePropertyChangeListener ( PropertyChangeListener l )
   {
        support_.removePropertyChangeListener ( l );
   }
   
   
}
