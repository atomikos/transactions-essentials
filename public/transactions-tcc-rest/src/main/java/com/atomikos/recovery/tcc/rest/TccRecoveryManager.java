/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import java.util.Collection;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;

public class TccRecoveryManager {

	private static final Logger LOGGER = LoggerFactory.createLogger(TccRecoveryManager.class);

	private static TccRecoveryManager instance;
	
	private RecoveryLog log;
	private TccTransport tccTransport;
	private InMemoryParticipantRepository repository;
	
	
	public TccRecoveryManager() {
		this.repository = InMemoryParticipantRepository.INSTANCE;
	}
	
	public TccRecoveryManager(RecoveryLog log, TccTransport tccTransport) {
		super();
		this.log = log;
		this.tccTransport = tccTransport;
		this.repository = InMemoryParticipantRepository.INSTANCE;
	}

	public void recover() {
		Collection<String> participantsToConfirm;
		Collection<PendingTransactionRecord> pendingTransactionRecords;
		long time = System.currentTimeMillis();
		try {
			pendingTransactionRecords = log.getExpiredPendingTransactionRecordsAt(time);
			for (PendingTransactionRecord pendingTransactionRecord : pendingTransactionRecords) {
				participantsToConfirm = repository.getParticipantLogEntries(pendingTransactionRecord.id);
				for (String uri : participantsToConfirm) {
					try {
						if (uri.startsWith("http")) retryConfirmOnParticipant(uri);
					} catch (HeurRollbackException e) {
						EventPublisher.publish(new TransactionHeuristicEvent(pendingTransactionRecord.id, TxState.HEUR_ABORTED.toString()));
					} 
				}
				repository.remove(pendingTransactionRecord.id);
			}
		} catch (LogReadException couldNotRetrieveCommittingParticipants) {
			LOGGER.logWarning("Could not retrieve committing participants - ignoring to retry later", couldNotRetrieveCommittingParticipants);
		} 
		
	}

	private void retryConfirmOnParticipant(String uri)
			throws HeurRollbackException {
		if (LOGGER.isDebugEnabled()) LOGGER.logDebug("Retrying confirm on participant: " + uri);
		tccTransport.put(uri);
	}
	
	public void setRecoveryLog(RecoveryLog log) {
		this.log = log;
	}
	
	public void setTccTransport(TccTransport tccTransport) {
		this.tccTransport = tccTransport;
	}
	
	
	public static TccRecoveryManager getInstance() {
		return instance;
	}
	
	public static void installTccRecoveryManager(RecoveryLog log, TccTransport tccTransport) {
			instance = new TccRecoveryManager(log, tccTransport);
	}
}
