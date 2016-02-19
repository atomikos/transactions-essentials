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

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.thread.InterruptedExceptionHelper;

public class RecoveryLogImp implements RecoveryLog, AdminLog {

	private static final Logger LOGGER = LoggerFactory.createLogger(RecoveryLogImp.class);
	
	private Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@Override
	public void terminated(ParticipantLogEntry entry)  {
		
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
			LOGGER.logWarning("Unable to write to repository: "+entry+" - leaving cleanup to recovery housekeeping...", e);
		} catch (IllegalArgumentException e) {
			LOGGER.logWarning("Unexpected error while terminating participant entry - ignoring (may result in orphaned log entry)", e);
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
		}
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
		}

	}

	@Override
	public void terminatedWithHeuristicHazard(ParticipantLogEntry entry) {
		LOGGER.logWarning("terminatedWithHeuristicHazard " + entry);
	}

	@Override
	public void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogException {
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
		}
	}

	@Override
	public CoordinatorLogEntry[] getCoordinatorLogEntries() {
		try {
			Collection<CoordinatorLogEntry> allCoordinatorLogEntries = repository.getAllCoordinatorLogEntries();
			return allCoordinatorLogEntries.toArray(new CoordinatorLogEntry[allCoordinatorLogEntries.size()]);
		} catch (LogReadException e) {
			LOGGER.logWarning("Could not retrieve coordinators - returning empty array", e);	
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
			LOGGER.logWarning("Waiting for termination of pending coordinators...");
			synchronized(this) {
				try {
					this.wait (waitTime);
				} catch (InterruptedException ex) {
					InterruptedExceptionHelper.handleInterruptedException ( ex );
					// ignore
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": interrupted during wait" , ex );
				}
			}
			accumulatedWaitTime+=waitTime;
			pendingCoordinatorLogEntries = getCoordinatorLogEntries();
		}
	}

}
