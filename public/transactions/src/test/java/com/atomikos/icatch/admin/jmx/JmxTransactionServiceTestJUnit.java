package com.atomikos.icatch.admin.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.admin.TestAdminTransaction;

import junit.framework.TestCase;

public class JmxTransactionServiceTestJUnit extends TestCase {

	private JmxTransactionService jmxTransactionService;
	
	protected void setUp() throws Exception {
		super.setUp();
		TestLogControl tl = new TestLogControl();
		JmxLogAdministrator.getInstance().registerLogControl ( tl );
		jmxTransactionService = new JmxTransactionService();
		jmxTransactionService.preRegister( new TestMBeanServer() , null );
	}
	
	public void testHeuristicsOnly() 
	{
		assertFalse ( jmxTransactionService.getHeuristicsOnly() );
		jmxTransactionService.setHeuristicsOnly ( true );
		assertTrue ( jmxTransactionService.getHeuristicsOnly() );
		jmxTransactionService.setHeuristicsOnly ( false );
		assertFalse ( jmxTransactionService.getHeuristicsOnly() );
	}
	
	public void testGetTransactions() 
	{
		ObjectName[] names = jmxTransactionService.getTransactions();
		assertEquals ( "wrong number of admintxs returned" , 10 , names.length );
	}
	
	public void testGetTransactionsInHeuristicsOnlyMode()
	{
		jmxTransactionService.setHeuristicsOnly ( true );
		ObjectName[] names = jmxTransactionService.getTransactions();
		assertEquals ( "wrong number of heuristic admintxs returned" , 4 , names.length );
	}
	
	private static class TestLogControl 
	implements LogControl
	{
		private Map tidToTxMap = new HashMap();
		
		private void addTransaction ( String tid , int state ) {
			TestAdminTransaction admintx = new TestAdminTransaction ( tid );
			admintx.setState(state);
			tidToTxMap.put( tid , admintx );
		}
		
		TestLogControl() 
		{
			addTransaction ( "tx1" , AdminTransaction.STATE_ABORTING);
			addTransaction ( "tx2" , AdminTransaction.STATE_ACTIVE );
			addTransaction ( "tx3" , AdminTransaction.STATE_COMMITTING );
			addTransaction ( "tx4" , AdminTransaction.STATE_HEUR_ABORTED );
			addTransaction ( "tx5" , AdminTransaction.STATE_HEUR_COMMITTED );
			addTransaction ( "tx6" , AdminTransaction.STATE_HEUR_HAZARD );	
			addTransaction ( "tx7" , AdminTransaction.STATE_HEUR_MIXED );
			addTransaction ( "tx8" , AdminTransaction.STATE_PREPARED );
			addTransaction ( "tx9" , AdminTransaction.STATE_PREPARING );
			addTransaction ( "tx10" , AdminTransaction.STATE_TERMINATED );

		}

		public AdminTransaction[] getAdminTransactions() 
		{
			
			return ( AdminTransaction[] ) tidToTxMap.values().toArray ( new AdminTransaction[0] );
		}

		public AdminTransaction[] getAdminTransactions ( String[] tids ) 
		{
			List results = new ArrayList();
			for ( int i = 0 ; i < tids.length ; i++ ) {
				results.add ( tidToTxMap.get ( tids[i] ) );
			}
			return ( AdminTransaction[] ) results.toArray ( new AdminTransaction[0] );
		}
		
	}

}
