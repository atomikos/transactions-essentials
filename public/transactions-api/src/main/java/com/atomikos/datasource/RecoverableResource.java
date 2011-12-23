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

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;


 /**
  * A Recoverable Resource is the abstraction of a resource 
  * that supports recoverable work (i.e., that supports Participant
  * instances). Its primary use is for Participant instances that are
  * not self-containing, as for instance in XA transactions
  * (where the Participant can not contain the entire transaction
  * context of the server, but only an external reference to it in the
  * form of an Xid instance).
  * A recoverable resource is invoked at recovery time by its
  * own Participant instances (typically in the readExternal 
  * method), who iteratively ask each resource 
  * in the com.atomikos.icatch.Configuration whether or not they 
  * can be recovered by them. At the end of recovery, 
  * the TM will invoke the endRecovery method, to indicate to the
  * resource that whatever private logs it has, any remaining 
  * and non-recovered participants should be aborted.
  */
  
public interface RecoverableResource
{
	
	/**
	 * Initializes this resource with the recovery service.
	 * This method is called by the transaction service during
	 * intialization of the transaction service or when the
	 * resource is added, whichever comes last. If the 
	 * resource wants to recover, it should subsequently 
	 * ask the recoveryService
	 * to do so.
	 * @param recoveryService The recovery service. This instance
	 * can be used by the resource to ask recovery from the 
	 * transaction engine. 
	 * @throws ResourceException On errors.
	 */

	public void setRecoveryService ( RecoveryService recoveryService )
	throws ResourceException;
	
    /**
     * Recovers the partially reconstructed Participant.
     * @param participant A partially recovered Participant.
     * @exception ResourceException On failure.
     * @return boolean True iff reconstruction was successful.
     * If the resource is not responsible for the given participant,
     * then this will return false.
     * A Participant can use this to iterate over all resources in order
     * to eventually recover itself. This is particularly
     * useful if the Participant instance can not serialize 
     * its full state, because some of it is on its backside 
     * resource (as, for instance, in XA).
     * This way, the TransactionalResource can be used to 
     * assist in reconstruction of the Participant's state.
     */
     
    public boolean recover ( Participant participant ) 
        throws ResourceException;
    
    /**
     * Notifies the resource that recovery is ended. 
     * Called by TM at end of recovery; any remaining
     * resourcetransactions (i.e., that have not been 
     * associated with any recover call) should be rolled back.
     * This is because if the were not recovered by the TM, 
     * then surely they are not supposed to be indoubt
     * (the TM recovers ALL indoubt work!) and should be
     * rolled back.
     *
     * @exception ResourceException On failure.
     */
     
    public void endRecovery () throws ResourceException;
    
    /**
     * Closes the resource for shutdown.
     * This notifies the resource that it is no longer needed.
     */

    public void close() throws ResourceException;
    
    /**
     * Gets the name of the resource. Names should be unique 
     * within one TM domain.
     * @return String The name.
     */
     
    public String getName();
    
    /**
     * Tests if a resource is the same as another one.
     */

    public boolean isSameRM(RecoverableResource res) 
        throws ResourceException;

    /**
     * Tests if the resource is closed.
     * @return boolean True if the resource is closed.
     */
    public boolean isClosed();
    
        

}
