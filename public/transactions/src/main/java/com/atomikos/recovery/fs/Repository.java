package com.atomikos.recovery.fs;

import java.util.Collection;

import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.PendingTransactionRecord;

public interface Repository {

	void init() throws LogException;

	void put(String id,PendingTransactionRecord pendingTransactionRecord) throws LogWriteException;
	
	PendingTransactionRecord get(String coordinatorId) throws LogReadException;

	Collection<PendingTransactionRecord> findAllCommittingCoordinatorLogEntries() throws LogReadException;
	
	Collection<PendingTransactionRecord> getAllCoordinatorLogEntries() throws LogReadException;

	void writeCheckpoint(Collection<PendingTransactionRecord> checkpointContent) throws LogWriteException;
	
	void close();
}
