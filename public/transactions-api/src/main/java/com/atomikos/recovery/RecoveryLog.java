/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
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

	void close(long timeout);
	
	/**
     * 
     * @return The set of log records from a different domain, i.e., where we don't know the commit decision (if any)
     * and have to wait for recovery by the remote party.
     * 
     * @throws LogReadException
     */
    Collection<PendingTransactionRecord> getForeignIndoubtTransactionRecords() throws LogReadException;

	public void forgetCommittingCoordinatorsExpiredSince(long expiry);

	Collection<PendingTransactionRecord> getExpiredPendingCommittingTransactionRecordsAt(long time) throws LogReadException;

	/**
	 * Forgets all NON-FOREIGN expired indoubts because they have been terminated in each resource by presumed abort.
	 * 
	 * @param momentInThePast
	 */
	void forgetNativeIndoubtCoordinatorsExpiredSince(long momentInThePast);

    void forgetTransactionRecords(Collection<PendingTransactionRecord> coordinators);

    void recordAsCommitting(String coordinatorId) throws LogException;

    void forget(String coordinatorId) throws LogException;
    
    PendingTransactionRecord get(String coordinatorId) throws LogReadException;
	
}
