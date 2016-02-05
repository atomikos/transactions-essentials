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
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.tcc.rest.TccRecoveryManager;
import com.atomikos.recovery.tcc.rest.TccTransport;

public class TccRecoveryManagerTestJUnit {

	TccRecoveryManager tccRecoveryManager = new TccRecoveryManager();
	private RecoveryLog log;
	private ParticipantLogEntry entry;
	private TccTransport tccTransport;

	@Before
	public void configure() {
		log = Mockito.mock(RecoveryLog.class);
		tccTransport = Mockito.mock(TccTransport.class);
		tccRecoveryManager.setRecoveryLog(log);
		tccRecoveryManager.setTccTransport(tccTransport);
		entry = new ParticipantLogEntry("coord", "http://part", 0);
	}
	
	@Test
	public void nonHttpUrisAreIgnored() throws Exception {
		givenCommittingXaParticipant();
		whenRecovered();	
	}

	private void givenCommittingXaParticipant() throws HeurRollbackException {
		entry = new ParticipantLogEntry("coord", "branchQualifier", 0);
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
		Mockito.doThrow(new IllegalArgumentException()).when(tccTransport).put(entry.participantUri);
	}

	@Test
	public void confirmingUrisAreConfirmed() throws Exception {
		givenCommittingParticipant();
		whenRecovered();
		thenPutWasCalledOnParticipant();
		thenUriWasTerminatedInLog();
	}
	
	@Test
	public void heuristicRollbackReportedToLog() throws HeurRollbackException {
		givenExpiredCommittingParticipant();
		whenRecovered();
		thenPutWasCalledOnParticipant();
		thenHeuristicRollbackWasReportedToLog();
	}

	private void thenHeuristicRollbackWasReportedToLog() {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicRollback(entry);
	}

	private void givenExpiredCommittingParticipant() throws HeurRollbackException {
		Mockito.doThrow(new HeurRollbackException()).when(tccTransport).put(entry.participantUri);
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
	}

	private void thenUriWasTerminatedInLog() {
		Mockito.verify(log, Mockito.times(1)).terminated(entry);
	}

	private void thenPutWasCalledOnParticipant() throws HeurRollbackException {
		VerificationMode uneFois = Mockito.times(1);
		Mockito.verify(tccTransport, uneFois).put(entry.participantUri);
	}

	private void whenRecovered() throws HeurRollbackException {
		tccRecoveryManager.recover();
	}

	private void givenCommittingParticipant() {
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
	}

}
