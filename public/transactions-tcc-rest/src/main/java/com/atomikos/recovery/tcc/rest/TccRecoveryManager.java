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
