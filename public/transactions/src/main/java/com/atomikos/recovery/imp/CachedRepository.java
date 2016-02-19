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
			e.printStackTrace();
			LOGGER.logWarning("Corrupted log file - restart JVM", e);
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
			LOGGER.logWarning("Corrupted log file - restart JVM", corrupted);
			corrupt = true;
			throw corrupted;	
		} catch (Exception corrupted) {
			LOGGER.logWarning("Corrupted log file - restart JVM", corrupted);
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
				LOGGER.logWarning("Unexpected long-lived entry found in log: " + coordinatorLogEntry );
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
