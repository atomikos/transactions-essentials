package com.atomikos.datasource.xa;

import com.atomikos.icatch.system.Configuration;

import junit.framework.TestCase;

public class XAResourceTransactionTestJnit extends TestCase {

	private XAResourceTransaction restx;
	private TestXATransactionalResource res;
	private CompositeTransactionStub ct;
	private TestCompositeCoordinator cc;
	private TestXAResource xares;
	
	protected void setUp() throws Exception {
		super.setUp();
		cc = new TestCompositeCoordinator ( getName() );
		ct = new CompositeTransactionStub ( getName() , true , cc );
		xares = new TestXAResource();
		res = new TestXATransactionalResource ( xares , "testresource" );
		restx = new XAResourceTransaction ( res , ct , getName() );
		Configuration.addResource ( res );
	}
	
	protected void tearDown() {
		Configuration.removeResource ( res.getName() );
	}

	//see case 23364
	public void testRecoveryDoesNotResetXAResourceBetweenEnlistAndDelist() throws Exception 
	{
		//make sure that xares is not reset by recovery for ACTIVE txs
		TestXAResource xares2 = new TestXAResource();
		restx.setXAResource ( xares2 );
		restx.resume();
		restx.recover();
		//xaresource should still be the same, or delist won't work!
		assertEquals ( xares2 , restx.getXAResource() );
		
	}
	
	public void testRecoveryDoesNotResetXAResourceBetweenDelistAndPrepare() throws Exception
	{
		//make sure that xares is not reset by recovery for ACTIVE txs
		TestXAResource xares2 = new TestXAResource();
		restx.setXAResource ( xares2 );
		restx.resume();
		restx.suspend();
		restx.recover();
		//xaresource should still be the same, or prepare won't work!
		assertEquals ( xares2 , restx.getXAResource() );
	}
	
	public void testRecoveryResetsXAResourceForIndoubts() throws Exception 
	{
		TestXAResource xares2 = new TestXAResource();
		restx.setXAResource ( xares2 );
		restx.resume();
		restx.suspend();
		restx.prepare();
		restx.recover();
		//xaresource should be reset by recovery!
		assertNotNull ( restx.getXAResource() );
		assertFalse ( xares2.equals ( restx.getXAResource() ) );
	}
}
