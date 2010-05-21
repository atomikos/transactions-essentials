package com.atomikos.datasource.xa.session;

import com.atomikos.icatch.jta.TransactionManagerImp;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;

public class NotInBranchStateHandlerTestJUnit extends
		TransactionServiceTestCase {

	private NotInBranchStateHandler state;
	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;
	
	public NotInBranchStateHandlerTestJUnit ( String name ) {
		super ( name );
	}
	
	protected void setUp()
	{
		super.setUp();
		xares = new TestXAResource();
		TestXATransactionalResource res = new TestXATransactionalResource ( xares , getName() );
		state = new NotInBranchStateHandler ( res , xares );
		uts = new UserTransactionServiceImp();
		uts.init(uts.createTSInitInfo());
		
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testNoEnlistWithoutTransaction() throws Exception
	{
		
		assertNull ( xares.getLastStarted() );
		Object nextState = state.checkEnlistBeforeUse ( null , null );
		assertNull ( xares.getLastStarted() );
		assertNull ( nextState );
	}
	
	public void testNoEnlistWithNonJtaTransaction() throws Exception
	{
		
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		assertNull ( xares.getLastStarted() );
		Object nextState = state.checkEnlistBeforeUse ( ct , null );
		assertNull ( xares.getLastStarted() );
		assertNull ( nextState );
	}
	
	public void testEnlistedWithJtaTransaction() throws Exception
	{
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		assertNull ( xares.getLastStarted() );
		Object nextState = state.checkEnlistBeforeUse ( ct , null );
		assertNotNull ( xares.getLastStarted() );
		assertTrue ( nextState instanceof BranchEnlistedStateHandler );
	}
	
	public void testClose() throws Exception
	{
		Object nextState = state.sessionClosed();
		assertTrue ( nextState instanceof TerminatedStateHandler );
		assertNull ( xares.getLastStarted() );
	}
	
	public void testTerminated() throws Exception
	{
		Object nextState = state.transactionTerminated ( ct );
		assertNull ( nextState );
	}
	
	public void testIsSuspendedInTransaction()
	{
		assertFalse ( state.isSuspendedInTransaction ( ct ) );
	}
	
	public void testTransactionSuspended()
	{
		try {
			state.transactionSuspended();
			fail ( "suspend works when not in  branch" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
		
	}
	
	public void testTransactionResumed()
	{
		try {
			state.transactionResumed();
			fail ( "resume works when not in branch" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
	}
}
