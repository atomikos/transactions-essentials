package com.atomikos.recovery.imp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;

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
	public void getAdminTransactions() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenActiveCoordinatorsRetrievedFromAdminLog();
	}

	@Test
	public void getTid() throws Exception {
		givenPendingTransactionInLog();
		whenGetAdminTransactions();
		thenAdminTransactionHasCorrectTid();
	}
	
	@Test
	public void getFilteredAdminTransactions() throws Exception {
		
		givenPendingTransactionInLog();
		whenGetFilteredAdminTransactions();
		thenActiveCoordinatorsRetrievedFromAdminLog();
		
	}

	private void whenGetFilteredAdminTransactions() {
		String[] tids= new String[1];
		tids[0] = TID;
		AdminTransaction[] adminTransactions = sut.getAdminTransactions(tids);
		adminTransaction = adminTransactions[0];
	}

	private void thenAdminTransactionHasCorrectTid() {
		Assert.assertEquals(TID, adminTransaction.getTid());
	}

	private void thenActiveCoordinatorsRetrievedFromAdminLog() {

		Mockito.verify(adminLog, Mockito.times(1)).getPendingCoordinatorIds();
		Assert.assertNotNull(adminTransaction);

	}

	private void whenGetAdminTransactions() {
		AdminTransaction[] adminTransactions = sut.getAdminTransactions();
		adminTransaction = adminTransactions[0];
	}

	private void givenPendingTransactionInLog() {
		String[] result = new String[1];
		result[0] = TID;
		Mockito.when(adminLog.getPendingCoordinatorIds()).thenReturn(result);
	}
}
