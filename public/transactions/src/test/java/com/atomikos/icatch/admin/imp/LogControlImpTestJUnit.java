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

package com.atomikos.icatch.admin.imp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.imp.LogControlImp;
import com.atomikos.recovery.AdminLog;

public class LogControlImpTestJUnit {

	private static final String TID = "TID";

	private LogControlImp sut;
	private AdminLog adminLog;

	private AdminTransaction adminTransaction;
	private ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[2];

	@Before
	public void configure() {
		adminLog = Mockito.mock(AdminLog.class);
		sut = new LogControlImp(adminLog);
		participantDetails[0] = new ParticipantLogEntry(TID, "one", 1000, "one", TxState.COMMITTING);
		participantDetails[1] = new ParticipantLogEntry(TID, "two", 1000, "two", TxState.COMMITTING);
	}

	@Test
	public void testGetAdminTransactions() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenActiveCoordinatorsRetrievedFromAdminLog();
	}

	@Test
	public void testGetTid() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenAdminTransactionHasCorrectTid();
	}

	@Test
	public void testGetFilteredAdminTransactions() throws Exception {
		givenPendingTransactionInLog();
		whenGetFilteredAdminTransactions();
		thenActiveCoordinatorsRetrievedFromAdminLog();
	}

	@Test
	public void testGetState() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenAdminTransactionHasCorrectState();
	}

	@Test
	public void testWasNotCommitted() throws Exception {
		givenPendingTransactionInLog(false);
		whenGetAdminTransactions();
		thenAdminTransactionWasCommitted(false);
	}

	@Test
	public void testWasCommitted() throws Exception {
		givenPendingTransactionInLog(true);
		whenGetAdminTransactions();
		thenAdminTransactionWasCommitted(true);
	}

	@Test
	public void testGetParticipantDetails() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenAdminTransactionHasCorrectParticipantDetails();
	}
	
	@Test
	public void testForceForget() {
		givenPendingTransactionInLog(true);
		whenForceForget();
		thenCoordinatorWasRemovedFromAdminLog();
	}

	private void thenCoordinatorWasRemovedFromAdminLog() {
		Mockito.verify(adminLog, Mockito.times(1)).remove(adminTransaction.getTid());
	}

	private void whenForceForget() {
		whenGetAdminTransactions();
		adminTransaction.forceForget();
	}

	private void thenAdminTransactionHasCorrectParticipantDetails() {

		Assert.assertEquals(2, adminTransaction.getParticipantDetails().length);
		Assert.assertEquals(participantDetails[0].toString(), adminTransaction.getParticipantDetails()[0]);
		Assert.assertEquals(participantDetails[1].toString(), adminTransaction.getParticipantDetails()[1]);

	}

	private void thenAdminTransactionWasCommitted(boolean value) {
		Assert.assertEquals(value, adminTransaction.wasCommitted());
	}

	private void givenPendingTransactionInLog(boolean wasCommitted) {
		CoordinatorLogEntry[] result = new CoordinatorLogEntry[1];

		result[0] = new CoordinatorLogEntry(TID, 
				wasCommitted, participantDetails);
		Mockito.when(adminLog.getCoordinatorLogEntries()).thenReturn(result);
	}

	private void thenAdminTransactionHasCorrectState() {
		Assert.assertEquals(TxState.COMMITTING,
				adminTransaction.getState());

	}

	private void whenGetFilteredAdminTransactions() {
		AdminTransaction[] adminTransactions = sut.getAdminTransactions(TID);
		adminTransaction = adminTransactions[0];
	}

	private void thenAdminTransactionHasCorrectTid() {
		Assert.assertEquals(TID, adminTransaction.getTid());
	}

	private void thenActiveCoordinatorsRetrievedFromAdminLog() {

		Mockito.verify(adminLog, Mockito.times(1)).getCoordinatorLogEntries();
		Assert.assertNotNull(adminTransaction);

	}

	private void whenGetAdminTransactions() {
		AdminTransaction[] adminTransactions = sut.getAdminTransactions();
		adminTransaction = adminTransactions[0];
	}

	private void givenPendingTransactionInLog() {
		CoordinatorLogEntry[] result = new CoordinatorLogEntry[1];
		result[0] = new CoordinatorLogEntry(TID, 
				participantDetails);
		Mockito.when(adminLog.getCoordinatorLogEntries()).thenReturn(result);
	}
}
