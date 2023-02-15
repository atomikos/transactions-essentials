/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;

import java.util.Collection;

import com.atomikos.icatch.RecoveryService;
import com.atomikos.recovery.PendingTransactionRecord;


 /**
  * A Recoverable Resource is the abstraction of a resource 
  * that supports recoverable work (i.e., that supports Participant
  * instances). A recoverable resource is invoked at recovery time by the transaction core.
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
      * 
      * @param startOfRecoveryScan
      * @param expiredCommittingCoordinators 
      * @param indoubtForeignCoordinatorsToKeep 
      * @return True if this resource has no more participants for the supplied expiredCommittingCoordinators.
      */
      boolean recover(long startOfRecoveryScan, Collection<PendingTransactionRecord> expiredCommittingCoordinators, Collection<PendingTransactionRecord> indoubtForeignCoordinatorsToKeep);

      /**
       * 
       * @return True if this resource had pending participants in the last recovery scan.
       */
      boolean hasMoreToRecover();
     
}
