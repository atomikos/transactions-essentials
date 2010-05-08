package com.atomikos.datasource.xa.session;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

public class TerminatedStateHanderTestJUnit extends TransactionServiceTestCase {


	private TerminatedStateHandler state;
	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;	
	
	public TerminatedStateHanderTestJUnit(String name) {
		super(name);
	}

	protected void setUp()
	{
		super.setUp();
		xares = new TestXAResource();
		TestXATransactionalResource res = new TestXATransactionalResource ( xares , getName() );
		uts = new UserTransactionServiceImp();
		uts.init(uts.createTSInitInfo());
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		XAResourceTransaction branch = (XAResourceTransaction) res.getResourceTransaction ( ct );
		branch.setXAResource ( xares );
		state = new TerminatedStateHandler();	
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testSessionClosed()
	{
		assertNull ( state.sessionClosed() );
	}
	
	public void testTerminated() 
	{
		assertNull ( state.transactionTerminated ( ct ) );
	}
	
	public void testIsSuspendedInTransaction()
	{
		assertFalse ( state.isSuspendedInTransaction ( ct ) );
	}
	
	public void testCheckEnlistBeforeUse()
	{
		try {
			state.checkEnlistBeforeUse ( ct , null );
			fail ( "enlist works if terminated" );
		}
		catch ( InvalidSessionHandleStateException ok ) {}
	}
	
	public void testTransactionSuspended()
	{
		try {
			state.transactionSuspended();
			fail ( "suspend works when terminated" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
		
	}
	
	public void testTransactionResumed()
	{
		try {
			state.transactionResumed();
			fail ( "resume works when terminated" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
	}
}
