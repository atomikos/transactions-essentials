package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.standalone.UserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

public class SessionHandleStateTestJUnit extends TransactionServiceTestCase {

	private TestXAResource xares;
	private UserTransactionServiceImp uts;
	private CompositeTransaction ct;	
	private SessionHandleState handle;
	
	public SessionHandleStateTestJUnit ( String name ) {
		super ( name );
	}
	
	protected void setUp()
	{
		super.setUp();
		xares = new TestXAResource();
		String name = getName();
		if ( name.length() > 32 ) name = name.substring ( 32 );
		TestXATransactionalResource res = new TestXATransactionalResource ( xares , name );
		handle = new SessionHandleState ( res , xares );
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();

		info.setProperty ( UserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , "true");
		info.setProperty ( UserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "INFO" );
	   	info.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
    	info.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath()
    	        );
		uts.init(info);
		Configuration.getCompositeTransactionManager().suspend();
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );	
		ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		
	}
	
	protected void tearDown()
	{
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testDefaultValues()
	{
		assertTrue ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		assertNull ( xares.getLastStarted() );
		assertFalse ( handle.isActiveInTransaction ( null ));
		assertFalse ( handle.isActiveInTransaction ( ct ));
		assertFalse ( handle.isInactiveInTransaction ( ct ) );
	}
	
	public void testSessionError()
	{
		assertFalse ( handle.isErroneous() );
		handle.notifySessionErrorOccurred();
		assertTrue ( handle.isErroneous() );
	}
	
	public void testErrorIfUsedWithoutBorrow() 
	{
		try {
			handle.notifyBeforeUse ( null , null );
			fail ( "no error if no borrow" );
		}
		catch ( InvalidSessionHandleStateException ok ) {}
	}
	
	public void testErrorIfUsedAfterClose()
	{
		handle.notifySessionBorrowed();
		handle.notifySessionClosed();
		try {
			handle.notifyBeforeUse ( null , null );
			fail ( "no error after close" );
		}
		catch ( InvalidSessionHandleStateException ok ) {}
	}
	
	public void testWithOneTransactionAndCloseBeforeCommit() throws Exception
	{
		testDefaultValues();
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertFalse ( handle.isInactiveInTransaction ( ct ) );
		handle.notifySessionBorrowed();
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertFalse ( handle.isInactiveInTransaction ( ct ) );
		handle.notifyBeforeUse ( ct , null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		assertFalse ( handle.isInactiveInTransaction ( ct ) );
		Object xid = xares.getLastStarted();
		assertNotNull ( xid );
		handle.notifySessionClosed();
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertTrue ( handle.isInactiveInTransaction ( ct ) );
		assertEquals ( xid , xares.getLastEnded() );
		assertFalse ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		ct.commit();
		assertFalse ( handle.isActiveInTransaction( ct ));
		handle.notifyTransactionTerminated ( ct );
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertFalse ( handle.isInactiveInTransaction ( ct ) );
		assertEquals ( xid , xares.getLastCommitted() );
		assertTrue ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithOneTransactionAndCommitBeforeClose() throws InvalidSessionHandleStateException, SysException, SecurityException, HeurRollbackException, HeurMixedException, HeurHazardException, RollbackException
	{
		testDefaultValues();
		handle.notifySessionBorrowed();
		assertFalse ( handle.isActiveInTransaction( ct ));
		handle.notifyBeforeUse ( ct , null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		Object xid = xares.getLastStarted();
		assertNotNull ( xid );
		ct.commit();
		handle.notifyTransactionTerminated ( ct );
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertFalse ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		handle.notifySessionClosed();
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertEquals ( xid , xares.getLastEnded() );
		assertTrue ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithTwoTransactionsAndCloseBeforeCommits() throws Exception 
	{
		//NOTE: probably not a very realistic test since one would expect the second tx to commit first, before the first is resumed
		testDefaultValues();
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct , null );
		Object xid = xares.getLastStarted();
		//System.out.println ( "Started " + xares.getLastStarted() );
		Configuration.getCompositeTransactionManager().suspend();
		CompositeTransaction ct2 = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );
		ct2.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		handle.notifyBeforeUse ( ct2 , null);
		//xid must have been suspended
		assertEquals ( xid , xares.getLastEnded() );
		//another xid must have been started
		//System.out.println ( "Started " + xares.getLastStarted() );
		assertFalse ( xid.equals ( xares.getLastStarted() ));
		assertFalse ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		//simulate resume of first ct
		handle.notifyBeforeUse ( ct , null);
		//first xid must have been started again
		assertEquals ( xid , xares.getLastStarted() );
		handle.notifySessionClosed();
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		ct2.commit();	
		handle.notifyTransactionTerminated ( ct2 );
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		ct.commit();
		handle.notifyTransactionTerminated ( ct );
		assertTrue ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithTwoTransactionsAndCloseAfterCommits() throws InvalidSessionHandleStateException 
	{
		testDefaultValues();
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct , null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		Object xid = xares.getLastStarted();
		CompositeTransaction ct2 = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );
		ct2.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		handle.notifyBeforeUse ( ct2 , null );	
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertTrue ( handle.isActiveInTransaction( ct2 ));
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		//xid must have been suspended
		assertEquals ( xid , xares.getLastEnded() );
		//another xid must have been started
		assertFalse ( xid.equals ( xares.getLastStarted() ));
		assertFalse ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		//second tx is committed first
		handle.notifyTransactionTerminated ( ct2 );
		assertFalse ( handle.isActiveInTransaction( ct2 ));
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		//simulate resume of first ct
		handle.notifyBeforeUse ( ct , null );
		assertTrue( handle.isActiveInTransaction( ct ));
		//first xid must have been started again (resumed)
		assertEquals ( xid , xares.getLastStarted() );
		handle.notifyTransactionTerminated ( ct );
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		handle.notifySessionClosed();
		assertTrue ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithTwoTransactionsAndCloseBetweenCommits() throws Exception 
	{
		testDefaultValues();
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct ,null);
		Object xid1 = xares.getLastStarted();
		Configuration.getCompositeTransactionManager().suspend();
		CompositeTransaction ct2 = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );
		ct2.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		//simulate passing tx boundary with REQUIRES_NEW
		handle.notifyBeforeUse ( ct2 ,null);	
		//first tx must be suspended
		assertEquals ( xid1 , xares.getLastEnded() );
		Object xid2 = xares.getLastStarted();
		assertFalse ( xid1.equals ( xid2 ) );
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		//simulate commit of new tx
		ct2.commit();
		assertEquals ( xid2 , xares.getLastCommitted() );
		handle.notifyTransactionTerminated ( ct2 );
		//simulate resume of first tx
		handle.notifyBeforeUse ( ct ,null);
		assertEquals ( xid1 , xares.getLastStarted() );
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		handle.notifySessionClosed();
		assertFalse ( handle.isTerminated());
		assertFalse ( handle.isErroneous() );
		ct.commit();
		handle.notifyTransactionTerminated ( ct );
		assertEquals ( xid1 , xares.getLastEnded() );
		assertEquals ( xid1 , xares.getLastCommitted() );
		assertTrue ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithTransactionAndSwitchContextToNotTransactional() throws Exception
	{
		testDefaultValues();
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct , null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		Object xid = xares.getLastStarted();
		assertNotNull ( xid );
		handle.notifyBeforeUse ( null , null );
		assertFalse ( handle.isActiveInTransaction( ct ));
		//xid seen by resource should still be the first one!
		assertEquals ( xid , xares.getLastStarted() );
		//first xid must have been suspended
		assertEquals ( xid , xares.getLastEnded() );
		//resume first tx
		handle.notifyBeforeUse ( ct , null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		assertEquals ( xid , xares.getLastStarted() );
		handle.notifySessionClosed();
		assertFalse ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
		ct.commit();
		handle.notifyTransactionTerminated ( ct );
		assertFalse ( handle.isActiveInTransaction( ct ));
		assertEquals ( xid , xares.getLastEnded() );
		assertEquals ( xid , xares.getLastCommitted() );
		assertTrue ( handle.isTerminated() );
		assertFalse ( handle.isErroneous() );
	}
	
	public void testWithSubTransactionAndNotClosed() throws Exception
	{
		testDefaultValues();
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct ,null);
		Object xid1 = xares.getLastStarted();
		CompositeTransaction ct2 = Configuration.getCompositeTransactionManager().createCompositeTransaction ( 1000 );
		ct2.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		handle.notifyBeforeUse ( ct2 , null );
		//handle not closed -> xid should be different since parent not yet done!
		assertNotSame ( xid1 , xares.getLastStarted() );
	}
	

	
	public void testChangeListener() {
		CounterSessionHandleStateChangeListener sessionHandleStateChangeListener = new CounterSessionHandleStateChangeListener();
		
		handle.registerSessionHandleStateChangeListener(sessionHandleStateChangeListener);
		handle.notifySessionBorrowed();
		handle.notifySessionClosed();
		
		assertEquals(1, sessionHandleStateChangeListener.eventCount);
	}
	
	public void testNoEnlistForNonJtaTransaction() throws Exception
	{
		Configuration.getCompositeTransactionManager().suspend();
		ct = Configuration.getCompositeTransactionManager().createCompositeTransaction(1000);
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse(ct, null);
		assertNull ( xares.getLastStarted() );
		assertFalse ( handle.isActiveInTransaction(ct) );
		
	}
	
	public void testEnlistedWithTransactionTimeout() throws Exception
	{
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct ,null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		//suspend tx to simulate transaction timeout 
		//handle.notifyTransactionTerminated ( ct );
		Configuration.getCompositeTransactionManager().suspend();
		xares.setFailureMode ( TestXAResource.FAIL_END , new XAException ( "Simulated suspend error" ) );
		//assuming the transaction has timed out, further (non-JTA) use of the connection should be forbidden
		try { 
			handle.notifyBeforeUse ( null , null );
			//after timeout/rollback, this is an error since the user will think 
			//he is using the same tx context on the connection
			//whereas the transaction does not exist any more;
			//later commits on the connection will save statements of a rolled back transaction!
			//see case 23659
			fail ( "Using a session handle after its transaction expires is allowed?" );
		} catch ( InvalidSessionHandleStateException ok ) {
			ok.printStackTrace();
		}
	}
	
	public void testNoDuplicateTerminationCallbacks() throws Exception
	{
		//assert that termination is never reported twice, or we get pooling problems!
		CounterSessionHandleStateChangeListener sessionHandleStateChangeListener = new CounterSessionHandleStateChangeListener();
		handle.registerSessionHandleStateChangeListener(sessionHandleStateChangeListener);
		handle.notifySessionBorrowed();
		handle.notifyBeforeUse ( ct ,null );
		assertTrue ( handle.isActiveInTransaction( ct ));
		handle.notifySessionClosed();
		ct.commit();
		//cause second termination event
		handle.notifyTransactionTerminated(ct);
		assertEquals(1, sessionHandleStateChangeListener.eventCount);
	}
	
	private class CounterSessionHandleStateChangeListener implements SessionHandleStateChangeListener {
		int eventCount = 0;
		public void onTerminated() {
			eventCount ++;
		}
	}
}
