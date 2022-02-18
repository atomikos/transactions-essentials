/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.util.Collection;

 /**
  * Handle to the transaction logs for recovery purposes.
  */

public interface RecoveryLog {
    
    /**
     * @return False if we don't have to do recovery because another instance is doing it.
     */
    boolean isActive();

    /**
     * Notification of JVM shutdown - allows another instance to take over.
     */
	void closing();
	
    Collection<PendingTransactionRecord> getIndoubtTransactionRecords() throws LogReadException;

	Collection<PendingTransactionRecord> getExpiredPendingCommittingTransactionRecordsAt(long time) throws LogReadException;

    void forgetTransactionRecords(Collection<PendingTransactionRecord> coordinators);

    /**
     * Mark the given transaction as committing. 
     * @param coordinatorId The transaction, previously logged as IN_DOUBT. 
     * For retries, the IN_DOUBT may no longer exist.
     * @throws LogException
     */
    void recordAsCommitting(String coordinatorId) throws LogException;

    void forget(String coordinatorId);
    
    PendingTransactionRecord get(String coordinatorId) throws LogReadException;
    
    Collection<PendingTransactionRecord> getPendingTransactionRecords() throws LogReadException;

    void closed();
	
}
