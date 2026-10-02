/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
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

    /**
     * @param recoveryDomainName A recovery domain other than our own.
     * @return Whether this recovery log recognizes the given domain, i.e. can resolve
     * that domain's transaction outcomes without depending on the exporting service
     * being reachable. Recovery logs that cannot recognize any foreign domain (e.g.
     * the default, non-shared log) should return false. Defaults to false so that
     * existing implementations remain source- and binary-compatible.
     */
    default boolean acceptsDomain(String recoveryDomainName) {
        return false;
    }

}
