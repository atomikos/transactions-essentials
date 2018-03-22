/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.TxState;

public class InMemoryRepository implements
		Repository {

	private  Map<String, CoordinatorLogEntry> storage = new ConcurrentHashMap<String, CoordinatorLogEntry>();

	
	private boolean closed = true;
	@Override
	public void init() {
		closed=false;
	}
	
	@Override
	public synchronized void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException {
		CoordinatorLogEntry existing = storage.get(id);
		if (existing != null && existing == coordinatorLogEntry) {
			throw new IllegalArgumentException("cannot put the same coordinatorLogEntry twice");
		}
		if(coordinatorLogEntry.getResultingState().isFinalState()){
			storage.remove(id);
		} else {
			storage.put(id, coordinatorLogEntry);	
		}
	}

	@Override
	public synchronized CoordinatorLogEntry get(String coordinatorId) {
		return storage.get(coordinatorId);
	}

	@Override
	public synchronized Collection<CoordinatorLogEntry> findAllCommittingCoordinatorLogEntries() {
		Set<CoordinatorLogEntry> res = new HashSet<CoordinatorLogEntry>();
		Collection<CoordinatorLogEntry> allCoordinatorLogEntry = storage.values();	
		for (CoordinatorLogEntry coordinatorLogEntry : allCoordinatorLogEntry) {
			if(coordinatorLogEntry.getResultingState() == TxState.COMMITTING){
					res.add(coordinatorLogEntry);
			}
		}
		return res;
	}

	@Override
	public void close() {
		storage.clear();
		closed=true;
	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() {
		return storage.values();
	}

	@Override
	public void writeCheckpoint(
			Collection<CoordinatorLogEntry> checkpointContent) {
		storage.clear();
		for (CoordinatorLogEntry coordinatorLogEntry : checkpointContent) {
			storage.put(coordinatorLogEntry.id, coordinatorLogEntry);
		}
		
	}

	

	public boolean isClosed() {
		return closed;
	}


}
