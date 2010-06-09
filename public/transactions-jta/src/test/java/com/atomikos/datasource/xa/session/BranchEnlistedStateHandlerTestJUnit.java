package com.atomikos.datasource.xa.session;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

public class BranchEnlistedStateHandlerTestJUnit extends
		TransactionServiceTestCase {

	private BranchEnlistedStateHandler state;
	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;
	
	public BranchEnlistedStateHandlerTestJUnit(String name) {
		super ( name );
	}
	
	protected void setUp()
	{
		super.setUp();
		xares = new TestXAResource();
		TestXATransactionalResource res = new TestXATransactionalResource ( xares , getName() );
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
	   	info.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
    	info.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath()
    	        );
    	uts.init ( info );
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		state = new BranchEnlistedStateHandler ( res , ct , xares , ( HeuristicMessage ) null );
		
		
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testClose()
	{
		assertNotNull ( xares.getLastStarted() );
		assertNull ( xares.getLastEnded() );
		Object nextState = state.sessionClosed();
		assertNotNull ( xares.getLastEnded() );
		assertEquals ( xares.getLastEnded() , xares.getLastStarted() );
		assertTrue ( nextState instanceof BranchEndedStateHandler );
	}
	
	public void testTerminated() throws Exception
	{
		ct.commit();
		Object nextState = state.transactionTerminated ( ct );
		assertTrue ( nextState instanceof NotInBranchStateHandler );
		assertNotNull ( xares.getLastEnded() );
		assertNotNull ( xares.getLastCommitted() );
		assertEquals ( xares.getLastCommitted() , xares.getLastEnded() );
		assertEquals ( xares.getLastEnded() , xares.getLastStarted() );
	}
	
	public void testIdempotencyOfEnlist() throws Exception
	{
		Object nextState = null;
		nextState = state.checkEnlistBeforeUse ( ct , null );
		assertNull ( nextState );
		nextState = state.checkEnlistBeforeUse ( ct , null );
		assertNull ( nextState );
	}
	
	public void testWithDifferentJtaTransaction() throws Exception
	{
		ct.commit();
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		Object xid = xares.getLastStarted();
		try {
			state.checkEnlistBeforeUse ( ct , null);
			fail ( "No error if different tx");
		} catch ( UnexpectedTransactionContextException ok ) {}
		assertEquals ( xid , xares.getLastStarted() );
	}
	
	public void testSuspended() throws Exception
	{
		Object nextState = state.transactionSuspended();
		assertTrue ( nextState instanceof BranchSuspendedStateHandler );
		assertEquals ( xares.getLastEnded() , xares.getLastStarted() );
	}
	
	public void testIsSuspendedInTransaction()
	{
		assertFalse ( state.isSuspendedInTransaction ( ct ) );
	}

	public void testTransactionResumed()
	{
		try {
			state.transactionResumed();
			fail ( "resume works when started in branch" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
	}
}
