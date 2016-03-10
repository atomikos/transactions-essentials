/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;

public class CachedRepository implements Repository {

	private static final Logger LOGGER = LoggerFactory.createLogger(CachedRepository.class);
	private boolean corrupt = false; 
	private final InMemoryRepository inMemoryCoordinatorLogEntryRepository;

	private final Repository backupCoordinatorLogEntryRepository;

	private volatile long numberOfPutsSinceLastCheckpoint = 0;
	private long checkpointInterval;
	private long forgetOrphanedLogEntriesDelay;
	public CachedRepository(
			InMemoryRepository inMemoryCoordinatorLogEntryRepository,
			Repository backupCoordinatorLogEntryRepository) {
		this.inMemoryCoordinatorLogEntryRepository = inMemoryCoordinatorLogEntryRepository;
		this.backupCoordinatorLogEntryRepository = backupCoordinatorLogEntryRepository;
	}

	@Override
	public void init(ConfigProperties configProperties) {
		//populate inMemoryCoordinatorLogEntryRepository with backup data
		try {
			Collection<CoordinatorLogEntry> coordinatorLogEntries = backupCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
			for (CoordinatorLogEntry coordinatorLogEntry : coordinatorLogEntries) {
				inMemoryCoordinatorLogEntryRepository.put(coordinatorLogEntry.id, coordinatorLogEntry);
			}
			
			performCheckpoint();
		} catch (LogException e) {
			LOGGER.logError("Corrupted log file - restart JVM", e);
			corrupt = true;
		}
		
		checkpointInterval = configProperties.getCheckpointInterval();
		forgetOrphanedLogEntriesDelay = configProperties.getForgetOrphanedLogEntriesDelay();
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

	private synchronized void performCheckpoint() throws LogWriteException {
		try {
			Collection<CoordinatorLogEntry> coordinatorLogEntries =	purgeExpiredCoordinatorLogEntriesInStateAborting();
			backupCoordinatorLogEntryRepository.writeCheckpoint(coordinatorLogEntries);
			inMemoryCoordinatorLogEntryRepository.writeCheckpoint(coordinatorLogEntries);
			numberOfPutsSinceLastCheckpoint=0;
		} catch (LogWriteException corrupted) {
			LOGGER.logError("Corrupted log file - restart JVM", corrupted);
			corrupt = true;
			throw corrupted;	
		} catch (Exception corrupted) {
			LOGGER.logError("Corrupted log file - restart JVM", corrupted);
			corrupt = true;
			throw new LogWriteException(corrupted);
		}
	}

	private Collection<CoordinatorLogEntry> purgeExpiredCoordinatorLogEntriesInStateAborting() {
		Set<CoordinatorLogEntry> ret = new HashSet<CoordinatorLogEntry>();
		long now = System.currentTimeMillis();
		Collection<CoordinatorLogEntry> coordinatorLogEntries = inMemoryCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
		for (CoordinatorLogEntry coordinatorLogEntry : coordinatorLogEntries) {
			if (!canBeForgotten(now, coordinatorLogEntry)){
				ret.add(coordinatorLogEntry);
			}
		}
		return ret;
	}

	protected boolean canBeForgotten(long now,
			CoordinatorLogEntry coordinatorLogEntry) {
		boolean ret = false;
		if ((coordinatorLogEntry.expires()+forgetOrphanedLogEntriesDelay) < now) {
			TxState entryState = coordinatorLogEntry.getResultingState();
			if ( TxState.ABORTING == entryState ) {
				// pending ABORTING: happens during presumed abort on recovery, 
				// when rollback OK in resource but subsequent delete from log fails
				ret = true;
			}
			else if (!entryState.isHeuristic()) {
				LOGGER.logError("Unexpected long-lived entry found in log: " + coordinatorLogEntry );
			}
			
		}
		return ret;
	}

	private boolean needsCheckpoint() {
		return numberOfPutsSinceLastCheckpoint>=checkpointInterval;
	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) throws LogReadException  {
		assertNotCorrupted();
		return inMemoryCoordinatorLogEntryRepository.get(coordinatorId);
	}

	protected void assertNotCorrupted() throws LogReadException {
		if(corrupt){
			throw new LogReadException("Log corrupted - restart JVM");
		}
	}

	

	@Override
	public Collection<CoordinatorLogEntry> findAllCommittingCoordinatorLogEntries() throws LogReadException {
		assertNotCorrupted();
		return inMemoryCoordinatorLogEntryRepository.findAllCommittingCoordinatorLogEntries();
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
