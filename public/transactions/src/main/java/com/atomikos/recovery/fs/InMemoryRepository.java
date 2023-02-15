/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.fs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.TxState;

public class InMemoryRepository implements Repository {

private  Map<String, PendingTransactionRecord> storage = new ConcurrentHashMap<String, PendingTransactionRecord>();

	
	private boolean closed = true;
	@Override
	public void init() {
		closed=false;
	}


	@Override
	public synchronized void put(String id, PendingTransactionRecord coordinatorLogEntry)
			throws IllegalArgumentException {
		PendingTransactionRecord existing = storage.get(id);
		if (existing != null && existing == coordinatorLogEntry) {
			throw new IllegalArgumentException("cannot put the same coordinatorLogEntry twice");
		}
		if(coordinatorLogEntry.state.isFinalState()){
			storage.remove(id);
		} else {
			storage.put(id, coordinatorLogEntry);	
		}
	}

	@Override
	public synchronized PendingTransactionRecord get(String coordinatorId) {
		return storage.get(coordinatorId);
	}

	@Override
	public synchronized Collection<PendingTransactionRecord> findAllCommittingCoordinatorLogEntries() {
		Set<PendingTransactionRecord> res = new HashSet<PendingTransactionRecord>();
		Collection<PendingTransactionRecord> allCoordinatorLogEntry = storage.values();	
		for (PendingTransactionRecord coordinatorLogEntry : allCoordinatorLogEntry) {
			TxState state = coordinatorLogEntry.state;
			if(state == TxState.COMMITTING) {
					res.add(coordinatorLogEntry);
			} else if (state == TxState.IN_DOUBT) {
				if(hasCommittingSuperiorCoordnator(coordinatorLogEntry)) {
					res.add(coordinatorLogEntry);
				}
					
			}
		}
		return res;
	}

	private boolean hasCommittingSuperiorCoordnator(PendingTransactionRecord coordinatorLogEntry) {
		if(coordinatorLogEntry.superiorId == null) return false;
		
		PendingTransactionRecord superiorCoordinator = storage.get(coordinatorLogEntry.superiorId);
		if (superiorCoordinator == null) {
			return false;
		}
		if (superiorCoordinator.state == TxState.COMMITTING) {
			return true;
		} else  {
			return hasCommittingSuperiorCoordnator(superiorCoordinator);
		}
	}

	@Override
	public void close() {
		storage.clear();
		closed=true;
	}

	@Override
	public Collection<PendingTransactionRecord> getAllCoordinatorLogEntries() {
		return storage.values();
	}

	@Override
	public void writeCheckpoint(
			Collection<PendingTransactionRecord> checkpointContent) {
		storage.clear();
		for (PendingTransactionRecord coordinatorLogEntry : checkpointContent) {
			storage.put(coordinatorLogEntry.id, coordinatorLogEntry);
		}
		
	}

	

	public boolean isClosed() {
		return closed;
	}

}
