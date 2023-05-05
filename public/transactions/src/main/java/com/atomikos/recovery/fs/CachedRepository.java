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
import java.util.Set;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.TxState;

public class CachedRepository  implements Repository {

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
	public void init() {
		//populate inMemoryCoordinatorLogEntryRepository with backup data
		
		ConfigProperties configProperties =	Configuration.getConfigProperties();
		checkpointInterval = configProperties.getCheckpointInterval();
		forgetOrphanedLogEntriesDelay = configProperties.getForgetOrphanedLogEntriesDelay();
		
		try {
			Collection<PendingTransactionRecord> coordinatorLogEntries = backupCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
			for (PendingTransactionRecord coordinatorLogEntry : coordinatorLogEntries) {
				inMemoryCoordinatorLogEntryRepository.put(coordinatorLogEntry.id, coordinatorLogEntry);
			}
			
			performCheckpoint();
		} catch (LogException e) {
			LOGGER.logFatal("Corrupted transaction log cache - restart JVM", e);
			corrupt = true;
		}
		
	}

	@Override
	public synchronized void put(String id, PendingTransactionRecord coordinatorLogEntry)
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
			Collection<PendingTransactionRecord> coordinatorLogEntries =	purgeExpiredCoordinatorLogEntriesInStateAborting();
			backupCoordinatorLogEntryRepository.writeCheckpoint(coordinatorLogEntries);
			inMemoryCoordinatorLogEntryRepository.writeCheckpoint(coordinatorLogEntries);
			numberOfPutsSinceLastCheckpoint=0;
			corrupt = false;
		} catch (LogWriteException corrupted) {
			LOGGER.logWarning("Failed to write checkpoint - will try again later", corrupted);
			corrupt = true;
			throw corrupted;	
		} catch (Exception corrupted) {
			LOGGER.logWarning("Failed to write checkpoint - will try again later", corrupted);
			corrupt = true;
			throw new LogWriteException(corrupted);
		}
	}

	private Collection<PendingTransactionRecord> purgeExpiredCoordinatorLogEntriesInStateAborting() {
		Set<PendingTransactionRecord> ret = new HashSet<PendingTransactionRecord>();
		long now = System.currentTimeMillis();
		Collection<PendingTransactionRecord> coordinatorLogEntries = inMemoryCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
		for (PendingTransactionRecord coordinatorLogEntry : coordinatorLogEntries) {
			if (!canBeForgotten(now, coordinatorLogEntry)){
				ret.add(coordinatorLogEntry);
			}
		}
		return ret;
	}

	protected boolean canBeForgotten(long now,
			PendingTransactionRecord coordinatorLogEntry) {
		boolean ret = false;
		if ((coordinatorLogEntry.expires+forgetOrphanedLogEntriesDelay) < now) {
			TxState entryState = coordinatorLogEntry.state;
			if (!entryState.isHeuristic()) {
				//can happen for commits or aborts where the 'terminated' does not make it to the log after the resource aborted/committed
				LOGGER.logWarning("Purging orphaned entry from log: " + coordinatorLogEntry);
				ret = true;
			}
			
		}
		return ret;
	}

	private boolean needsCheckpoint() {
		return numberOfPutsSinceLastCheckpoint>=checkpointInterval || corrupt;
	}

	@Override
	public PendingTransactionRecord get(String coordinatorId) throws LogReadException  {
		return inMemoryCoordinatorLogEntryRepository.get(coordinatorId);
	}


	@Override
	public Collection<PendingTransactionRecord> findAllCommittingCoordinatorLogEntries() throws LogReadException {
		return inMemoryCoordinatorLogEntryRepository.findAllCommittingCoordinatorLogEntries();
	}

	

	@Override
	public void close() {
		backupCoordinatorLogEntryRepository.close();
		inMemoryCoordinatorLogEntryRepository.close();
	}

	@Override
	public Collection<PendingTransactionRecord> getAllCoordinatorLogEntries() {
		return inMemoryCoordinatorLogEntryRepository.getAllCoordinatorLogEntries();
	}

	@Override
	public void writeCheckpoint(
			Collection<PendingTransactionRecord> checkpointContent) {
		throw new UnsupportedOperationException();
	}
}
