package com.atomikos.datasource.xa.session;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

public class BranchSuspendedStateHandlerTestJUnit extends
		TransactionServiceTestCase 
		{

	private BranchSuspendedStateHandler state;
	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;
	
	public BranchSuspendedStateHandlerTestJUnit ( String name ) {
		super ( name );
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
		state = new BranchSuspendedStateHandler ( res , branch , ct , xares );	
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testTerminated()
	{
		Object nextState = state.transactionTerminated ( ct );
		assertTrue ( nextState instanceof NotInBranchStateHandler );
	}
	
	public void testResumed() throws InvalidSessionHandleStateException
	{
		Object nextState = state.transactionResumed();
		assertTrue ( nextState instanceof BranchEnlistedStateHandler );
	}
	
	public void testCheckEnlistBeforeUse() throws UnexpectedTransactionContextException
	{
		try {
			state.checkEnlistBeforeUse ( ct , null );
			fail ( "Enlist works if suspended" );
		}
		catch ( InvalidSessionHandleStateException ok ) {}
	}
	
	public void testSessionClosed()
	{
		Object nextState = state.sessionClosed();
		assertTrue ( nextState instanceof BranchEndedStateHandler );
	}
	
	public void testIsSuspendedInTransaction()
	{
		assertTrue ( state.isSuspendedInTransaction ( ct ) );
		CompositeTransaction otherCt = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		assertFalse ( state.isSuspendedInTransaction ( otherCt ) );
	}
	
	public void testTransactionSuspended()
	{
		try {
			state.transactionSuspended();
			fail ( "suspend works when suspended" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
		
	}
}
