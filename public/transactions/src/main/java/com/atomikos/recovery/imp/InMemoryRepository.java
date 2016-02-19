/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.recovery.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.Repository;

public class InMemoryRepository implements
		Repository {

	private  Map<String, CoordinatorLogEntry> storage = new ConcurrentHashMap<String, CoordinatorLogEntry>();

	
	private boolean closed = true;
	@Override
	public void init(ConfigProperties configProperties) {
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
