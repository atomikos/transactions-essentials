package com.atomikos.recovery.imp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.imp.LogControlImp;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;

public class LogControlImpTestJUnit {

	private static final String TID = "TID";

	private LogControlImp sut;
	private AdminLog adminLog;

	private AdminTransaction adminTransaction;
	private String[] participantDetails = new String[2];

	@Before
	public void configure() {
		adminLog = Mockito.mock(AdminLog.class);
		sut = new LogControlImp(adminLog);
		participantDetails[0] = "one";
		participantDetails[1] = "two";
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

		Assert.assertEquals(participantDetails,
				adminTransaction.getParticipantDetails());

	}

	private void thenAdminTransactionWasCommitted(boolean value) {
		Assert.assertEquals(value, adminTransaction.wasCommitted());
	}

	private void givenPendingTransactionInLog(boolean wasCommitted) {
		CoordinatorLogEntry[] result = new CoordinatorLogEntry[1];

		result[0] = new CoordinatorLogEntry(TID, TxState.COMMITTING,
				wasCommitted, participantDetails);
		Mockito.when(adminLog.getCoordinatorLogEntries()).thenReturn(result);
	}

	private void thenAdminTransactionHasCorrectState() {
		Assert.assertEquals(AdminTransaction.STATE_COMMITTING,
				adminTransaction.getState());

	}

	private void whenGetFilteredAdminTransactions() {
		String[] tids = new String[1];
		tids[0] = TID;
		AdminTransaction[] adminTransactions = sut.getAdminTransactions(tids);
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
		result[0] = new CoordinatorLogEntry(TID, TxState.COMMITTING,
				participantDetails);
		Mockito.when(adminLog.getCoordinatorLogEntries()).thenReturn(result);
	}
}
