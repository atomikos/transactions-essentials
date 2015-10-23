package com.atomikos.recovery.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;

public class CachedCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private List<String> staleInCache = Collections.synchronizedList(new ArrayList<String>()) ; 
	private final InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository;

	private final CoordinatorLogEntryRepository backupCoordinatorLogEntryRepository;

	public CachedCoordinatorLogEntryRepository(
			InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository,
			CoordinatorLogEntryRepository backupCoordinatorLogEntryRepository) {
		this.inMemoryCoordinatorLogEntryRepository = inMemoryCoordinatorLogEntryRepository;
		this.backupCoordinatorLogEntryRepository = backupCoordinatorLogEntryRepository;
	}

	@Override
	public void init(Properties properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException, LogWriteException {
		try {
			backupCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
			inMemoryCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
		} catch (Exception e) {
			staleInCache.add(id);
			
		}
	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) {
		if (staleInCache.contains(coordinatorId)) {
			CoordinatorLogEntry backedUpCoordinatorLogEntry = backupCoordinatorLogEntryRepository.get(coordinatorId);
			if (backedUpCoordinatorLogEntry != null) {
				inMemoryCoordinatorLogEntryRepository.put(coordinatorId,backedUpCoordinatorLogEntry);
			}
			staleInCache.remove(coordinatorId);
		}
		return inMemoryCoordinatorLogEntryRepository.get(coordinatorId);
	}

	@Override
	public Collection<ParticipantLogEntry> findAllCommittingParticipants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
