package com.atomikos.icatch.admin.jmx;

import javax.management.ObjectName;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestAdminTransaction;

import junit.framework.TestCase;

public abstract class AbstractJUnitJmxTransactionTest 
extends TestCase {

	private JmxTransaction jmxTransaction;
	private TestAdminTransaction testAdminTransaction;
	private TestMBeanServer server;
	
	protected final void setUp() throws Exception 
	{
		super.setUp();
		server = new TestMBeanServer();
		testAdminTransaction = new TestAdminTransaction( "TEST" );
		jmxTransaction = onSetup ( testAdminTransaction , server );
	}
	
	protected abstract JmxTransaction onSetup (
			TestAdminTransaction testAdminTransaction ,
			TestMBeanServer server );
	
	
	public void testStateActive()
	{	
		testAdminTransaction.setState ( AdminTransaction.STATE_ACTIVE );
		assertEquals ( jmxTransaction.getState() , "ACTIVE" );
	}
	
	public void testStatePreparing()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_PREPARING );
		assertEquals ( jmxTransaction.getState() , "PREPARING" );
	}
	
	public void testStatePrepared()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_PREPARED );
		assertEquals ( jmxTransaction.getState() , "PREPARED" );
	}
	
	public void testStateHeuristicMixed()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_HEUR_MIXED );
		assertEquals ( jmxTransaction.getState() , "HEURISTIC MIXED" );
	}
	
	public void testStateHeuristicHazard()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_HEUR_HAZARD );
		assertEquals ( jmxTransaction.getState() , "HEURISTIC HAZARD" );
	}

	public void testStateHeuristicCommit()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_HEUR_COMMITTED );
		assertEquals ( jmxTransaction.getState() , "HEURISTIC COMMIT" );
	}
	
	public void testStateHeuristicRollback()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_HEUR_ABORTED );
		assertEquals ( jmxTransaction.getState() , "HEURISTIC ROLLBACK" );
	}
	
	public void testStateCommitting()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_COMMITTING );
		assertEquals ( jmxTransaction.getState() , "COMMITTING" );
	}
	
	public void testStateRollingBack()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_ABORTING );
		assertEquals ( jmxTransaction.getState() , "ROLLING BACK" );
	}
	
	public void testStateTerminated()
	{
		testAdminTransaction.setState ( AdminTransaction.STATE_TERMINATED );
		assertEquals ( jmxTransaction.getState() , "TERMINATED" );
	}
	
	public void testTid()
	{
		assertEquals ( testAdminTransaction.getTid() , jmxTransaction.getTid() );
	}
	
	public void testGetAdminTransaction()
	{
		assertEquals ( testAdminTransaction, jmxTransaction.getAdminTransaction());
	}
	
	public void testTags()
	{
		String tag = "tag";
		HeuristicMessage[] tags = 
			new HeuristicMessage[1];
		tags[0] = new StringHeuristicMessage ( tag );
		testAdminTransaction.setTags ( tags );
		String[] testTags = jmxTransaction.getTags();
		assertEquals ( testTags.length , 1 );
		assertEquals ( testTags[0] , tag );
	}
	
	public void testHeuristicMessages()
	{
		String msg = "msg";
		HeuristicMessage[] msgs =
			new StringHeuristicMessage[1];
		msgs[0] = new StringHeuristicMessage ( msg );
		testAdminTransaction.setHeuristicMessages ( msgs);
		String[] testMsgs = jmxTransaction.getHeuristicMessages();
		assertEquals ( testMsgs.length , 1 );
		assertEquals ( testMsgs[0] , msg );
	}
	
	public void testPostRegister()
	{
		jmxTransaction.postRegister ( null );
	}
	
	public void testPreDeregister() throws Exception
	{
		jmxTransaction.preDeregister();
	}
	
	public void testPostDeregister()
	{
		jmxTransaction.postDeregister();
	}
	
	public void testRegister() throws Exception
	{
		ObjectName name = jmxTransaction.preRegister ( server , null );
		//registration is NOT done -> remove assertion
		//assertTrue ( server.isRegistered ( name ) );
		jmxTransaction.unregister();
	}
	
}
