package com.atomikos.recovery.fs;


import java.util.Collection;
import java.util.HashSet;

import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.InterruptedExceptionHelper;

public class RecoveryLogImp implements RecoveryLog {

	private static final Logger LOGGER = LoggerFactory.createLogger(RecoveryLogImp.class);
	
	private Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	

	private void publishDomainEvent(
			TransactionHeuristicEvent transactionHeuristicEvent) {
		EventPublisher.publish(transactionHeuristicEvent);
	}

	public PendingTransactionRecord[] getPendingTransactionRecords() {
		try {
			Collection<PendingTransactionRecord> allCoordinatorLogEntries = repository.getAllCoordinatorLogEntries();
			return allCoordinatorLogEntries.toArray(new PendingTransactionRecord[allCoordinatorLogEntries.size()]);
		} catch (LogReadException e) {
			LOGGER.logError("Could not retrieve coordinators - returning empty array", e);	
		}
		
		return new PendingTransactionRecord[0];
	}


	@Override
	public void close(long maxWaitTime) {
		if ( maxWaitTime > 0 ) {
			waitForActiveTransactionsToFinish(maxWaitTime);
		}
		PendingTransactionRecord[] pendingCoordinatorLogEntries = getPendingTransactionRecords();
		if (pendingCoordinatorLogEntries.length>0) {
			LOGGER.logWarning("Shutdown leaves pending transactions in log - do NOT delete logfiles!");
		} else {
			LOGGER.logInfo("Shutdown leaves no pending transactions - ok to delete logfiles");
		}
		//don't close repository: OltpLog responsibility.
	}

	private synchronized void waitForActiveTransactionsToFinish(long maxWaitTime) {
		PendingTransactionRecord[] pendingCoordinatorLogEntries = getPendingTransactionRecords();
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
			pendingCoordinatorLogEntries = getPendingTransactionRecords();
		}
	}

	@Override
	public void forgetCommittingCoordinatorsExpiredSince(long expiry) {
		Collection<PendingTransactionRecord> expiredCommittingCoordinators = new HashSet<>();
		try {
			expiredCommittingCoordinators.addAll(getExpiredPendingTransactionRecordsAt(expiry));
		} catch (Exception e) {
			LOGGER.logWarning("Unexpected error while retrieving coordinators", e);
		}

		for (PendingTransactionRecord coordinatorLogEntry : expiredCommittingCoordinators) {
			PendingTransactionRecord terminated = coordinatorLogEntry.markAsTerminated();
			try {
				repository.put(terminated.id, terminated);
			} catch (Exception e) {
				LOGGER.logWarning("Unexpected error while forgetting coordinator: "+ terminated.id, e);
			}
		}
	}

	@Override
	public Collection<PendingTransactionRecord> getExpiredPendingTransactionRecordsAt(long time) throws LogReadException {
		Collection<PendingTransactionRecord> expiredCommittingCoordinatorsAt = new HashSet<>();
		Collection<PendingTransactionRecord> allCoordinatorLogEntries = repository.findAllCommittingCoordinatorLogEntries();
		for (PendingTransactionRecord pendingTransactionRecord : allCoordinatorLogEntries) {
				String superiorId = pendingTransactionRecord.superiorId;
				if (superiorId != null ) {
					Collection<PendingTransactionRecord> ret = new HashSet<>();
					collectExpiredCommittingSuperiorCoordinatorsAt(ret, superiorId, time);
					if(!ret.isEmpty()) {
						ret.add(pendingTransactionRecord);
					}
					expiredCommittingCoordinatorsAt.addAll(ret);	
				} else if(pendingTransactionRecord.expires<time && pendingTransactionRecord.state ==TxState.COMMITTING) {
					expiredCommittingCoordinatorsAt.add(pendingTransactionRecord);	
				
			}
		}
		return expiredCommittingCoordinatorsAt;
	}

	private void collectExpiredCommittingSuperiorCoordinatorsAt(Collection<PendingTransactionRecord> collector, String superiorId, long time) throws LogReadException {
		PendingTransactionRecord superior = repository.get(superiorId);
		if (superior != null) {
			if (superior.superiorId != null) {
				collectExpiredCommittingSuperiorCoordinatorsAt(collector, superior.superiorId, time);
			} else { // root found
				if(superior.expires < time && superior.state == TxState.COMMITTING){
					collector.add(superior);
				}
			}
		} 
	}

	@Override
	public void forgetIndoubtCoordinatorsExpiredSince(long momentInThePast) {
		try {
			Collection<PendingTransactionRecord> allCoordinatorLogEntries = repository.getAllCoordinatorLogEntries();
			for(PendingTransactionRecord entry : allCoordinatorLogEntries) {
				if (entry.expires < momentInThePast && entry.state == TxState.IN_DOUBT) {
					PendingTransactionRecord terminated = entry.markAsTerminated();
					repository.put(terminated.id, terminated);
				}
			}
		} catch (Exception e) {
			LOGGER.logDebug("Unexpected exception - ignoring...", e);
		}
		
	}
	
}