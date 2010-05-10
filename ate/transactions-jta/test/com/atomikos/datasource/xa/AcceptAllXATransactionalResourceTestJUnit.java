package com.atomikos.datasource.xa;

import junit.framework.TestCase;

public class AcceptAllXATransactionalResourceTestJUnit extends TestCase {

	private AcceptAllXATransactionalResource res;
	
	protected void setUp() throws Exception {
		super.setUp();
		res = new AcceptAllXATransactionalResource ( "TESTSERVER" , new DefaultXidFactory() );
	}
	
	public void testRefreshXAConnection()
	{
		//no refreshing can be done: could be any backend
		assertNull ( res.refreshXAConnection() );
	}
	
	public void testRecoverParticipant()
	{
		XAResourceTransaction p = 
			new XAResourceTransaction ();
		//this type of resource can't recover anything
		assertFalse ( res.recover ( p ) );
	}
	
	public void testUsesXAResource()
	{
		//always true by definition
		assertTrue ( res.usesXAResource ( null ));
		TestXAResource xares = new TestXAResource();
		assertTrue ( res.usesXAResource ( xares ) );
	}
	
	
	public void testRecovery()
	{
		res.recover();
		res.endRecovery();
	}

}
