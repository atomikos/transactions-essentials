package com.atomikos.recovery.imp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;

public class LogControlImpTestJUnit {

	private static final String TID = "TID";

	private LogControlImp sut;
	private AdminLog adminLog;

	private AdminTransaction adminTransaction;

	@Before
	public void configure() {
		adminLog = Mockito.mock(AdminLog.class);
		sut = new LogControlImp(adminLog);
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

	private void thenAdminTransactionHasCorrectState() {
		Assert.assertEquals(AdminTransaction.STATE_COMMITTING, adminTransaction.getState());
		
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
		result[0] = new CoordinatorLogEntry(TID, TxState.COMMITTING);
		Mockito.when(adminLog.getCoordinatorLogEntries()).thenReturn(result);
	}
}
