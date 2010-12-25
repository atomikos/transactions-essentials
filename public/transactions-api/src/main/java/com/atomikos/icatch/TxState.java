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

package com.atomikos.icatch;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

/**
*
*
*A state implementation for a distributed transaction system.
*/

public class TxState implements java.io.Serializable
{
    //force UID for backward log compatibilty
    static final long serialVersionUID = 648321112075712930L;
    
    // public static final TxState INIT = new TxState("INIT");
    //initialized only
    public static final TxState ACTIVE = new TxState("ACTIVE");
    //tx is doing stuff
    public static final TxState MARKED_ABORT = new TxState("MARKED_ABORT");
    //for local subtransactions: indicates that the local parent tx should not commit
    public static final TxState LOCALLY_DONE = new TxState("LOCALLY_DONE");
    //tx is locally finished (if compensatable: preliminary committed
    public static final TxState PREPARING = new TxState("PREPARING");
    //waiting for votes in 2PC prepare phase
    public static final TxState IN_DOUBT = new TxState("IN_DOUBT");
    //in-doubt
    public static final TxState ABORTING = new TxState("ABORTING");
    //in process of aborting
    public static final TxState COMMITTING  =  new TxState("COMMITTING");
    //in process of committing
    
    public static final TxState SUSPENDED = new TxState("SUSPENDED");
    
  
    public static final TxState HEUR_COMMITTED = new TxState("HEUR_COMMITTED");
    public static final TxState HEUR_ABORTED = new TxState("HEUR_ABORTED");
    public static final TxState HEUR_MIXED = new TxState("HEUR_MIXED");
    public static final TxState HEUR_HAZARD = new TxState("HEUR_HAZARD");

    public static final TxState TERMINATED = new TxState("TERMINATED");
    //all done with, can be forgotten about
    
    public static Enumeration getStates()
    {
        
        Vector v = new Vector();
        Class myClass = TxState.class;
        Field[] fields = myClass.getFields();
        for ( int i = 0 ; i < fields.length ; i++ ) {
	  try {
	      v.addElement(fields[i].get(null));
	  }
	  catch (Exception e) {}
        }//for
        
        return v.elements();
    }
    
  
    
    private final String myName;
    

    
    private TxState(String s){
        myName = s.intern();
    }
    public String toString(){
    	return myName;
    }
	
	public int hashCode() {
		return myName.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TxState other = (TxState) obj;
		if (myName == null) {
			if (other.myName != null)
				return false;
		} else if (!myName.equals(other.myName))
			return false;
		return true;
	}
    
    
    
//
//    public boolean equals ( Object o ) 
//    {
//        if ( o == null || !(o instanceof TxState) )
//	  return false;
//        TxState state = (TxState ) o;
//        return state.toString().equals(toString());
//    }
//
//    public int hashCode() 
//    {
//        return myName.hashCode();
//    }
//    
    
    
}
