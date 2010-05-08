package com.atomikos.datasource.xa;

import java.util.Stack;

import javax.transaction.xa.XAException;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.imp.TestCompositeTransactionBase;

import junit.framework.TestCase;

public class XAResourceTransactionTestJUnit extends TestCase 
{
	
	private TestXAResource xaResource;
	private TestXATransactionalResource transactionalResource;
	private TestCompositeTransactionBase compositeTx;
	private XAResourceTransaction resourceTransaction;
	
	protected void setUp()
	{
		xaResource = new TestXAResource();
		transactionalResource = new TestXATransactionalResource(xaResource, "test-resource1");
		
		compositeTx = new TestCompositeTransactionBase("tid", new Stack());

	}
	
	public void testXidHexEncoding() throws Exception 
	{
		resourceTransaction = new XAResourceTransaction(transactionalResource, compositeTx, "root");
		//assert equality; NOTE: the pending '1' of the XID hex string depends on the order in which tests are launched!
		assertEquals("XAResourceTransaction: 746964:746573742D7265736F757263653131", resourceTransaction.toString());
		
	}
	
	public void testHeuristicMessagesIncludeXidInHexFormat() throws Exception
	{
		resourceTransaction = new XAResourceTransaction(transactionalResource, compositeTx, "root");
		assertEquals(1, resourceTransaction.getHeuristicMessages().length);
		//assert equality; NOTE: the pending '2' of the XID hex string depends on the order in which tests are launched!
		assertEquals("XA resource 'test-resource1' accessed with Xid '746964:746573742D7265736F757263653132'", resourceTransaction.getHeuristicMessages()[0].toString());
	}
	
	public void testHeuristicMessagesIncludeExceptionStackTrace() throws Exception
	{
		resourceTransaction = new XAResourceTransaction(transactionalResource, compositeTx, "root");
		resourceTransaction.setXAResource(xaResource);
		final String reason = "XA resource 'test-resource1': commit for XID '746964:746573742D7265736F757263653133' raised 5: the XA resource has heuristically committed some parts and rolled back other parts";
		resourceTransaction.resume();
		resourceTransaction.suspend();
		resourceTransaction.prepare();
		
		
		
		xaResource.setFailureMode ( TestXAResource.FAIL_COMMIT , new XAException ( XAException.XA_HEURMIX) );
		try {
			
			resourceTransaction.commit ( false );
			fail ( "No error on simulated exception?" );
		} catch ( Exception ok ) {
		}
		
		HeuristicMessage[] msgs = resourceTransaction.getHeuristicMessages();
		boolean found = false;
		for ( int i = 0 ; i < msgs.length ; i++ ) {
			if ( msgs[i].toString().indexOf ( reason ) >= 0 ) found = true;
			System.out.println ( msgs[i].toString() );
		}
		assertTrue ( "Heuristic stack trace not in heuristic messages" , found );
		
	}
}
