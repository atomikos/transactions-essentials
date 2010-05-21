package com.atomikos.datasource.xa.session;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

public class BranchEndedStateHandlerTestJUnit extends
		TransactionServiceTestCase {

	private BranchEndedStateHandler state;
	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;
	
	public BranchEndedStateHandlerTestJUnit(String name) {
		super(name);
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
		uts.init(info);		
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		XAResourceTransaction branch = (XAResourceTransaction) res.getResourceTransaction ( ct );
		branch.setXAResource ( xares );
		state = new BranchEndedStateHandler ( res , branch , ct );	
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testCheckEnlistBeforeUse()
	{
		try {
			state.checkEnlistBeforeUse ( ct , null );
			fail ( "Enlist works for ended branch" );
		}
		catch ( InvalidSessionHandleStateException ok ) {}
	}
	
	public void testTerminated()
	{
		Object nextState = state.transactionTerminated ( ct );
		assertTrue ( nextState instanceof TerminatedStateHandler );
		
		//assert that other tx is ignored
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		nextState = state.transactionTerminated ( ct );
		assertNull ( nextState );
	}
	
	public void testSessionClosed()
	{
		Object nextState = state.sessionClosed();
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
			fail ( "suspend works for ended branch" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
		
	}
	
	public void testTransactionResumed()
	{
		try {
			state.transactionResumed();
			fail ( "resume works for ended branch" );
		}
		catch ( InvalidSessionHandleStateException  ok ){}
	}
}
