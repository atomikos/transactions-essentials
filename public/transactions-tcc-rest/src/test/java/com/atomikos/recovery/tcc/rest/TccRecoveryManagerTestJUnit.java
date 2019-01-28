/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;

public class TccRecoveryManagerTestJUnit {

	private static final String COORD = "coord";
	TccRecoveryManager tccRecoveryManager = new TccRecoveryManager();
	private RecoveryLog log;
	private TccTransport tccTransport;
	private InMemoryParticipantRepository repository = InMemoryParticipantRepository.INSTANCE;

	private static final String URI = "http://part";
	@Before
	public void configure() {
		log = Mockito.mock(RecoveryLog.class);
		tccTransport = Mockito.mock(TccTransport.class);
		tccRecoveryManager.setRecoveryLog(log);
		tccRecoveryManager.setTccTransport(tccTransport);
	}


	private void givenCommittingParticipant() throws HeurRollbackException, LogReadException {
		PendingTransactionRecord coord = new PendingTransactionRecord(COORD, TxState.COMMITTING, 0l);
		Collection<PendingTransactionRecord> confirmingCoordinators = new HashSet<PendingTransactionRecord>();
		confirmingCoordinators.add(coord);
		Mockito.when(log.getExpiredPendingTransactionRecordsAt((Mockito.anyLong()))).thenReturn(confirmingCoordinators);
		Set<String> uris = new HashSet<>();
		uris.add(URI);
		repository.save(coord.id, uris);
	}

	@Test
	public void confirmingUrisAreConfirmed() throws Exception {
		givenCommittingParticipant();
		whenRecovered();
		thenPutWasCalledOnParticipant();
		thenUriWasDeletedInRepository();
	}


	private void thenUriWasDeletedInRepository() {
		Assert.assertNull(repository.getParticipantLogEntries(COORD));
	}

	private void thenPutWasCalledOnParticipant() throws HeurRollbackException {
		VerificationMode uneFois = Mockito.times(1);
		Mockito.verify(tccTransport, uneFois).put(URI);
	}

	private void whenRecovered() throws HeurRollbackException {
		tccRecoveryManager.recover();
	}

}
