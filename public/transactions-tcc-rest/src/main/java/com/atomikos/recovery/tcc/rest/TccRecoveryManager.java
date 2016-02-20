/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import java.util.Collection;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.RecoveryLog;

public class TccRecoveryManager {

	private static final Logger LOGGER = LoggerFactory.createLogger(TccRecoveryManager.class);
	
	private RecoveryLog log;
	private TccTransport tccTransport;
	
	public void recover() {
		Collection<ParticipantLogEntry> participantsToConfirm;
		
		try {
			participantsToConfirm = log.getCommittingParticipants();
			for (ParticipantLogEntry entry : participantsToConfirm) {
				try {
					if (entry.uri.startsWith("http")) retryConfirmOnParticipant(entry.uri);
				} catch (HeurRollbackException e) {
					try {
						log.terminatedWithHeuristicRollback(entry);
					} catch (LogException couldNotUpdateLog) {
						LOGGER.logWarning("Could not update log for "+entry+" - ignoring to retry later", couldNotUpdateLog);
					}
				} 
				log.terminated(entry); 
			}
		} catch (LogReadException couldNotRetrieveCommittingParticipants) {
			LOGGER.logWarning("Could not retrieve committing participants - ignoring to retry later", couldNotRetrieveCommittingParticipants);
		} 
		
	}

	private void retryConfirmOnParticipant(String uri)
			throws HeurRollbackException {
		tccTransport.put(uri);
	}
	
	public void setRecoveryLog(RecoveryLog log) {
		this.log = log;
	}
	
	public void setTccTransport(TccTransport tccTransport) {
		this.tccTransport = tccTransport;
	}
}
