package com.atomikos.recovery.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.ParticipantLogEntry;

public class InMemoryCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private  Map<String, CoordinatorLogEntry> storage = new ConcurrentHashMap<String, CoordinatorLogEntry>();

	
	@Override
	public void init(Properties properties) {
	}
	
	@Override
	public synchronized void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException {
		CoordinatorLogEntry existing = storage.get(id);
		if (existing != null && existing == coordinatorLogEntry) {
			throw new IllegalArgumentException("cannot put the same coordinatorLogEntry twice");
		}
		storage.put(id, coordinatorLogEntry);

	}

	@Override
	public synchronized void remove(String id) {
		storage.remove(id);

	}

	@Override
	public synchronized CoordinatorLogEntry get(String coordinatorId) {
		return storage.get(coordinatorId);
	}

	@Override
	public synchronized Collection<ParticipantLogEntry> findAllCommittingParticipants() {
		Set<ParticipantLogEntry> res = new HashSet<ParticipantLogEntry>();
		Collection<CoordinatorLogEntry> allCoordinatorLogEntry = storage.values();
		for (CoordinatorLogEntry coordinatorLogEntry : allCoordinatorLogEntry) {
			for (ParticipantLogEntry participantLogEntry : coordinatorLogEntry.participantDetails) {
				if(participantLogEntry.state==TxState.COMMITTING){
					res.add(participantLogEntry);
				}
			}
		}
		return res;
	}

	@Override
	public void close() {
		
	}


}
