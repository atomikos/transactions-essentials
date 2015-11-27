package com.atomikos.recovery.imp;

import java.util.Collection;

import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;

public class CachedCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private boolean corrupt = false; 
	private final InMemoryCoordinatorLogEntryRepository inMemoryCoordinatorLogEntryRepository;

	private final CoordinatorLogEntryRepository backupCoordinatorLogEntryRepository;

	private volatile long numberOfPutsSinceLastCheckpoint = 0;
	private long checkpointInterval;
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
		checkpointInterval = configProperties.getCheckpointInterval();		
	}

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException, LogWriteException {
		
		try {
			if(needsCheckpoint()){
				performCheckpoint();
			}
			backupCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
			inMemoryCoordinatorLogEntryRepository.put(id, coordinatorLogEntry);
			numberOfPutsSinceLastCheckpoint++;
		} catch (Exception e) {
			performCheckpoint();
		}
	}

	private void performCheckpoint() throws LogWriteException {
		try {
			backupCoordinatorLogEntryRepository.writeCheckpoint(inMemoryCoordinatorLogEntryRepository.getAllCoordinatorLogEntries());
			numberOfPutsSinceLastCheckpoint=0;
		} catch (LogWriteException corrupted) {
			corrupt = true;
			throw corrupted;	
		} catch (Exception corrupted) {
			corrupt = true;
			throw new LogWriteException(corrupted);
		}
	}

	private boolean needsCheckpoint() {
		return numberOfPutsSinceLastCheckpoint>=checkpointInterval;
	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) throws LogReadException  {
		if(corrupt){
			throw new LogReadException();
		}
		return inMemoryCoordinatorLogEntryRepository.get(coordinatorId);
	}

	

	@Override
	public Collection<ParticipantLogEntry> findAllCommittingParticipants() throws LogReadException {
		if(corrupt){
			throw new LogReadException();
		}
		return inMemoryCoordinatorLogEntryRepository.findAllCommittingParticipants();
	}

	

	@Override
	public void close() {
		backupCoordinatorLogEntryRepository.close();
		inMemoryCoordinatorLogEntryRepository.close();
	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() {
		return inMemoryCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
	}

	@Override
	public void writeCheckpoint(
			Collection<CoordinatorLogEntry> checkpointContent) {
		throw new UnsupportedOperationException();
	}

}
