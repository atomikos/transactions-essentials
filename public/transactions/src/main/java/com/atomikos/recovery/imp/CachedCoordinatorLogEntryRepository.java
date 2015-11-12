package com.atomikos.recovery.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.atomikos.icatch.provider.ConfigProperties;
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
	public void init(ConfigProperties configProperties) {
		//populate inMemoryCoordinatorLogEntryRepository with backup data
		Collection<CoordinatorLogEntry> coordinatorLogEntries =	backupCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
		for (CoordinatorLogEntry coordinatorLogEntry : coordinatorLogEntries) {
			inMemoryCoordinatorLogEntryRepository.put(coordinatorLogEntry.coordinatorId, coordinatorLogEntry);
		}
			
	}

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException, LogWriteException {
		try {
			backupCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
			inMemoryCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
			staleInCache.remove(id);
		} catch (Exception e) {
			staleInCache.add(id);
			
		}
	}

	@Override
	public void remove(String id) {
		try {
			inMemoryCoordinatorLogEntryRepository.remove(id);
			backupCoordinatorLogEntryRepository.remove(id);
			staleInCache.remove(id);
		} catch (Exception e) {
			staleInCache.add(id);
		}
	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) {
		refreshInMemoryCoordinatorLogEntryRepository();
		return inMemoryCoordinatorLogEntryRepository.get(coordinatorId);
	}

	private void reloadFromBackupIfNecessary(String coordinatorId) {
		if (staleInCache.contains(coordinatorId)) {
			CoordinatorLogEntry backedUpCoordinatorLogEntry = backupCoordinatorLogEntryRepository.get(coordinatorId);
			if (backedUpCoordinatorLogEntry != null) {
				inMemoryCoordinatorLogEntryRepository.put(coordinatorId,backedUpCoordinatorLogEntry);
			} else  {
				inMemoryCoordinatorLogEntryRepository.remove(coordinatorId);
			}
		}
	}

	@Override
	public Collection<ParticipantLogEntry> findAllCommittingParticipants() {
		if(!staleInCache.isEmpty()){
			refreshInMemoryCoordinatorLogEntryRepository();			
		}
		return inMemoryCoordinatorLogEntryRepository.findAllCommittingParticipants();
	}

	private void refreshInMemoryCoordinatorLogEntryRepository() {
		for (String staleCoordinatorId : staleInCache) {
			reloadFromBackupIfNecessary(staleCoordinatorId);
		}
		staleInCache.clear();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() {
		throw new UnsupportedOperationException();
	}

}
