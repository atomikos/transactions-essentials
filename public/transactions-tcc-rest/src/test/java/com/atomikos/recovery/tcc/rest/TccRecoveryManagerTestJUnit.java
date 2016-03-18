/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;

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
		entry = new ParticipantLogEntry("coord", "http://part", 0,"xx",TxState.COMMITTING);
	}
	
	@Test
	public void nonHttpUrisAreIgnored() throws Exception {
		givenCommittingXaParticipant();
		whenRecovered();	
	}

	private void givenCommittingXaParticipant() throws HeurRollbackException, LogReadException {
		entry = new ParticipantLogEntry("coord", "branchQualifier", 0,"xx",TxState.COMMITTING);
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
		Mockito.doThrow(new IllegalArgumentException()).when(tccTransport).put(entry.uri);
	}

	@Test
	public void confirmingUrisAreConfirmed() throws Exception {
		givenCommittingParticipant();
		whenRecovered();
		thenPutWasCalledOnParticipant();
		thenUriWasTerminatedInLog();
	}
	
	@Test
	public void heuristicRollbackReportedToLog() throws HeurRollbackException, LogException {
		givenExpiredCommittingParticipant();
		whenRecovered();
		thenPutWasCalledOnParticipant();
		thenHeuristicRollbackWasReportedToLog();
	}

	private void thenHeuristicRollbackWasReportedToLog() throws LogException {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicRollback(entry);
	}

	private void givenExpiredCommittingParticipant() throws HeurRollbackException, LogReadException {
		Mockito.doThrow(new HeurRollbackException()).when(tccTransport).put(entry.uri);
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
	}

	private void thenUriWasTerminatedInLog() {
		Mockito.verify(log, Mockito.times(1)).terminated(entry);
	}

	private void thenPutWasCalledOnParticipant() throws HeurRollbackException {
		VerificationMode uneFois = Mockito.times(1);
		Mockito.verify(tccTransport, uneFois).put(entry.uri);
	}

	private void whenRecovered() throws HeurRollbackException {
		tccRecoveryManager.recover();
	}

	private void givenCommittingParticipant() throws LogReadException {
		Collection<ParticipantLogEntry> confirmingParticipantAdapters = new HashSet<ParticipantLogEntry>();
		confirmingParticipantAdapters.add(entry);
		Mockito.when(log.getCommittingParticipants()).thenReturn(confirmingParticipantAdapters);
	}

}
