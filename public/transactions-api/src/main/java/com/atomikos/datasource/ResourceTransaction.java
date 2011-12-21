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

package com.atomikos.datasource;

import com.atomikos.icatch.HeuristicMessage;
/**
 *
 *
 *The notion of a local transaction executed on a resource.
 *Serves as a handle  towards the transaction management module.
 */
 
public interface ResourceTransaction 
{
   
    
    
    /**
     *Get identifier for this tx.
     *Should be unique in system.
     *
     *@return String The identifier, as determined by resource.
     */

    public java.lang.String getTid();
   

    /**
     *Add heuristic resolution information.
     *@param mesg The heuristic message.
     *@exception IllegalStateException If no longer active.
     */

    public void addHeuristicMessage(HeuristicMessage mesg)
        throws IllegalStateException;
    
   
    /**
     *Get heuristic context info.
     *
     *@return HeuristicMessage[] An array of messages, or null if none.
     */

    public HeuristicMessage[] getHeuristicMessages();


    /**
     *Suspend the resourcetransaction, so that underlying resources can
     *be used for a next (sibling) invocation.
     *This is also the recommended method for adding the 
     *resourcetx to the coordinator object, but ONLY if 
     *the transaction has not been set for rollback.
     *NOTE: suspend is NOT the same as XAResource's suspension!
     *The XAResource version is specific to the XA protocol, 
     *and does not belong in the composite system framework.
     *As mentioned in the JTA specs., the APPLICATION SERVER is 
     *responsible for XAsuspension and XAresume ( page 11 of JTA
     *API, version of May 12, 1999 ).
     *
     *@exception IllegalStateException If wrong state.
     */

    public void suspend() throws IllegalStateException,ResourceException;

    /**
     *Resume a suspended tx.
     *
     *@exception IllegalStateException If not right state.
     */


    public void resume() throws IllegalStateException,ResourceException;
       
}
