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
		String xid = resourceTransaction.xidToHexString ( resourceTransaction.getXid() );
		assertEquals("XAResourceTransaction: " + xid , resourceTransaction.toString());
		
	}
	
	public void testHeuristicMessagesIncludeXidInHexFormat() throws Exception
	{
		resourceTransaction = new XAResourceTransaction(transactionalResource, compositeTx, "root");
		String xid = resourceTransaction.xidToHexString ( resourceTransaction.getXid() );
		assertEquals(1, resourceTransaction.getHeuristicMessages().length);
		assertEquals("XA resource 'test-resource1' accessed with Xid '" + xid + "'", resourceTransaction.getHeuristicMessages()[0].toString());
	}
	
	public void testHeuristicMessagesIncludeExceptionStackTrace() throws Exception
	{
		resourceTransaction = new XAResourceTransaction(transactionalResource, compositeTx, "root");
		resourceTransaction.setXAResource(xaResource);	
		resourceTransaction.resume();
		resourceTransaction.suspend();
		resourceTransaction.prepare();
		
		String xid = resourceTransaction.xidToHexString ( resourceTransaction.getXid() );
		final String reason = "XA resource 'test-resource1': commit for XID '" + xid + "' raised 5: the XA resource has heuristically committed some parts and rolled back other parts";
		
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
