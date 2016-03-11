/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;

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

	void setRecoveryService ( RecoveryService recoveryService )
	throws ResourceException;
	
    
    /**
     * Closes the resource for shutdown.
     * This notifies the resource that it is no longer needed.
     */

     void close() throws ResourceException;
    
    /**
     * Gets the name of the resource. Names should be unique 
     * within one TM domain.
     * @return String The name.
     */
     
     String getName();
    
    /**
     * Tests if a resource is the same as another one.
     */

     boolean isSameRM(RecoverableResource res) 
        throws ResourceException;

    /**
     * Tests if the resource is closed.
     * @return boolean True if the resource is closed.
     */
     boolean isClosed();

    /**
     * Instructs the resource to recover. 
     * When this method returns, all pending participants of this resource have been recovered.
     */
     void recover();
    
}
