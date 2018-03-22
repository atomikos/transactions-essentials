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

import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.InterruptedExceptionHelper;

public class RecoveryLogImp implements RecoveryLog, AdminLog {

	private static final Logger LOGGER = LoggerFactory.createLogger(RecoveryLogImp.class);
	
	private Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@Override
	public void terminated(ParticipantLogEntry entry)  {
		if (LOGGER.isTraceEnabled()) LOGGER.logTrace("terminated: " + entry);

		try {
			CoordinatorLogEntry coordinatorLogEntry =null;
			coordinatorLogEntry = repository.get(entry.coordinatorId);
			if (coordinatorLogEntry == null) {
				LOGGER.logWarning("termination called on non existent Coordinator "+ entry.coordinatorId + " " + entry.uri);
			} else {	
				CoordinatorLogEntry updated = coordinatorLogEntry.terminated(entry);
				repository.put(updated.id, updated);
				if (coordinatorLogEntry.superiorCoordinatorId != null) {
					terminateParentTx(coordinatorLogEntry);
				}
			}
		} catch (LogException e) {
			LOGGER.logError("Unable to write to repository: "+entry+" - leaving cleanup to recovery housekeeping...", e);
		} catch (IllegalArgumentException e) {
			LOGGER.logError("Unexpected error while terminating participant entry - ignoring (may result in orphaned log entry)", e);
		} 
	}

	protected void terminateParentTx(CoordinatorLogEntry coordinatorLogEntry)
			throws LogReadException, LogWriteException {
		CoordinatorLogEntry parentCoordinatorLogEntry = repository.get(coordinatorLogEntry.superiorCoordinatorId);
		if (parentCoordinatorLogEntry != null) {
			CoordinatorLogEntry parentUpdated = parentCoordinatorLogEntry.terminated(createSubTransactionCoordinatorParticipant(coordinatorLogEntry));
			repository.put(parentUpdated.id, parentUpdated);	
		}
	}

	protected ParticipantLogEntry createSubTransactionCoordinatorParticipant(
			CoordinatorLogEntry subTransaction) {
		return new ParticipantLogEntry(subTransaction.superiorCoordinatorId, subTransaction.id, subTransaction.expires(), "subtransaction participant", subTransaction.getResultingState());
	}

	

	@Override
	public void terminatedWithHeuristicRollback(ParticipantLogEntry entry) throws LogException {
		LOGGER.logDebug("terminatedWithHeuristicRollback: " + entry);
		CoordinatorLogEntry coordinatorLogEntry = repository.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicRollback called on non existent Coordinator " + entry.coordinatorId + " " + entry.uri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry.terminatedWithHeuristicRollback(entry);
			repository.put(updated.id, updated);
			if (coordinatorLogEntry.superiorCoordinatorId!=null) {
				ParticipantLogEntry subTransaction = createSubTransactionCoordinatorParticipant(coordinatorLogEntry);
				terminatedWithHeuristicRollback(subTransaction);
			}
			publishDomainEvent(new TransactionHeuristicEvent(entry.coordinatorId));
		}
	}

	private void publishDomainEvent(
			TransactionHeuristicEvent transactionHeuristicEvent) {
		EventPublisher.publish(transactionHeuristicEvent);
	}

	@Override
	public Collection<ParticipantLogEntry> getCommittingParticipants()
			throws LogReadException {
		Collection<ParticipantLogEntry> committingParticipants = new HashSet<ParticipantLogEntry>();
		Collection<CoordinatorLogEntry> committingCoordinatorLogEntries = repository.findAllCommittingCoordinatorLogEntries();
		
		for (CoordinatorLogEntry coordinatorLogEntry : committingCoordinatorLogEntries) {
			for (ParticipantLogEntry participantLogEntry : coordinatorLogEntry.participants) {
				committingParticipants.add(participantLogEntry);
			}
		}
		return committingParticipants;
	}

	private void write(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalStateException, LogException {
		if (!entryAllowed(coordinatorLogEntry)) {
			throw new IllegalStateException();
		}
		repository.put(coordinatorLogEntry.id, coordinatorLogEntry);
	}

	private boolean entryAllowed(CoordinatorLogEntry coordinatorLogEntry) throws LogReadException {
		CoordinatorLogEntry existing = repository
				.get(coordinatorLogEntry.id);
		return coordinatorLogEntry.transitionAllowedFrom(existing);
	}


	@Override
	public void presumedAborting(ParticipantLogEntry entry)
			throws IllegalStateException, LogException {
		if (entry == null || entry.state != TxState.IN_DOUBT) {
			throw new IllegalArgumentException();
		}
		
		CoordinatorLogEntry coordinatorLogEntry = repository.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) { 
			//first time this participant is found in resource: write IN_DOUBT entry in log to do presumed abort on next scan
			//this gives any concurrent OLTP 2-phase commit a reasonable delay to proceed without interference of recovery
			coordinatorLogEntry = createCoordinatorLogEntry(entry);
			write(coordinatorLogEntry);
			throw new IllegalStateException();
		} else {
			if (coordinatorLogEntry.superiorCoordinatorId != null) {		
				CoordinatorLogEntry parentCoordinatorLogEntry =	repository.get(coordinatorLogEntry.superiorCoordinatorId );
				if (parentCoordinatorLogEntry != null && parentCoordinatorLogEntry.getResultingState() == TxState.IN_DOUBT) {
					ParticipantLogEntry subTransaction = createSubTransactionCoordinatorParticipant(coordinatorLogEntry);
					presumedAborting(subTransaction);
				}
				
			}
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace("Attempting presumed abort for existing " + coordinatorLogEntry);
			}
			CoordinatorLogEntry updated = coordinatorLogEntry.presumedAborting(entry);			
			write(updated);
		
		}
	}

	private CoordinatorLogEntry createCoordinatorLogEntry(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry coordinatorLogEntry;
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[1];
		participantDetails[0] = entry;
		coordinatorLogEntry = new CoordinatorLogEntry(entry.coordinatorId, participantDetails);
		return coordinatorLogEntry;
	}

	@Override
	public void terminatedWithHeuristicCommit(ParticipantLogEntry entry) throws LogException {
		LOGGER.logDebug("terminatedWithHeuristicCommit: " + entry);
		CoordinatorLogEntry coordinatorLogEntry = repository.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicCommit called on non existent Coordinator " + entry.coordinatorId + " " + entry.uri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry.terminatedWithHeuristicCommit(entry);
			repository.put(updated.id, updated);
			if (coordinatorLogEntry.superiorCoordinatorId!=null) {
				ParticipantLogEntry subTransaction = createSubTransactionCoordinatorParticipant(coordinatorLogEntry);
				terminatedWithHeuristicCommit(subTransaction);
			}
			publishDomainEvent(new TransactionHeuristicEvent(entry.coordinatorId));
		}

	}

	@Override
	public void terminatedWithHeuristicHazard(ParticipantLogEntry entry) {
		LOGGER.logDebug("terminatedWithHeuristicHazard " + entry);
		publishDomainEvent(new TransactionHeuristicEvent(entry.coordinatorId));
	}

	@Override
	public void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogException {
		LOGGER.logDebug("terminatedWithHeuristicMixed " + entry);
		CoordinatorLogEntry coordinatorLogEntry = repository.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicMixed called on non existent Coordinator " + entry.coordinatorId + " " + entry.uri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry.terminatedWithHeuristicMixed(entry);
			repository.put(updated.id, updated);
			if (coordinatorLogEntry.superiorCoordinatorId!=null) {
				ParticipantLogEntry subTransaction = createSubTransactionCoordinatorParticipant(coordinatorLogEntry);
				terminatedWithHeuristicMixed(subTransaction);
			}
			publishDomainEvent(new TransactionHeuristicEvent(entry.coordinatorId));
		}
	}

	@Override
	public CoordinatorLogEntry[] getCoordinatorLogEntries() {
		try {
			Collection<CoordinatorLogEntry> allCoordinatorLogEntries = repository.getAllCoordinatorLogEntries();
			return allCoordinatorLogEntries.toArray(new CoordinatorLogEntry[allCoordinatorLogEntries.size()]);
		} catch (LogReadException e) {
			LOGGER.logError("Could not retrieve coordinators - returning empty array", e);	
		}
		
		return new CoordinatorLogEntry[0];
	}

	@Override
	public void remove(String coordinatorId) {
		CoordinatorLogEntry toRemove = null;
		try {
			 toRemove =	repository.get(coordinatorId);
		} catch (LogReadException e) {
			LOGGER.logWarning("Could not retrieve coordinator to remove: "+coordinatorId+" - ignoring", e);	
		}
		if (toRemove != null) {
			toRemove = toRemove.markAsTerminated();
			try {
				repository.put(coordinatorId, toRemove);
			} catch (Exception e) {
				LOGGER.logWarning("Could not remove coordinator: "+coordinatorId+" - ignoring", e);
			} 
		}
		
	}

	@Override
	public void close(long maxWaitTime) {
		if ( maxWaitTime > 0 ) {
			waitForActiveTransactionsToFinish(maxWaitTime);
		}
		CoordinatorLogEntry[] pendingCoordinatorLogEntries = getCoordinatorLogEntries();
		if (pendingCoordinatorLogEntries.length>0) {
			LOGGER.logWarning("Shutdown leaves pending transactions in log - do NOT delete logfiles!");
		} else {
			LOGGER.logInfo("Shutdown leaves no pending transactions - ok to delete logfiles");
		}
		//don't close repository: OltpLog responsibility.
	}

	private synchronized void waitForActiveTransactionsToFinish(long maxWaitTime) {
		CoordinatorLogEntry[] pendingCoordinatorLogEntries = getCoordinatorLogEntries();
		long accumulatedWaitTime = 0;
		int waitTime = 1000;
		while (pendingCoordinatorLogEntries.length>0 && (accumulatedWaitTime < maxWaitTime)) {
			LOGGER.logInfo("Waiting for termination of pending coordinators...");
			synchronized(this) {
				try {
					this.wait (waitTime);
				} catch (InterruptedException ex) {
					InterruptedExceptionHelper.handleInterruptedException ( ex );
					// ignore
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": interrupted during wait" , ex );
				}
			}
			accumulatedWaitTime+=waitTime;
			pendingCoordinatorLogEntries = getCoordinatorLogEntries();
		}
	}

}
