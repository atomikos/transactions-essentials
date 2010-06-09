package com.atomikos.datasource.xa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

import junit.framework.TestCase;

public class XAResourceTransactionRecoveryTestJUnit extends TestCase {
	
	private static XAResourceTransaction prepareStreamOutAndRecover (
			XAResourceTransaction restx ) throws IOException, ClassNotFoundException, SysException, RollbackException, HeurHazardException, HeurMixedException 
	{		
		return prepareStreamOutAndRecover ( restx , new TestXAResource() );		
	}
	
	private static XAResourceTransaction prepareStreamOutAndRecover (
			XAResourceTransaction restx , TestXAResource xares ) throws IOException, ClassNotFoundException, SysException, RollbackException, HeurHazardException, HeurMixedException 
	{
		XAResourceTransaction ret = null;
		restx.setXAResource ( xares );
		restx.prepare();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream ( bout );
		out.writeObject ( restx );
		out.close();
		ByteArrayInputStream bin = new ByteArrayInputStream ( bout.toByteArray() );
		ObjectInputStream in = new ObjectInputStream ( bin );
		ret = (XAResourceTransaction) in.readObject();
		ret.recover();
		return ret;
	}
	
	

	private XAResourceTransaction restx;
	private TestXATransactionalResource res;
	private CompositeTransactionStub ct;
	private TestCompositeCoordinator cc;
	private TestXAResource xares;
	private final String resourceName = "testresource";
	
	protected void setUp() throws Exception {
		super.setUp();
		cc = new TestCompositeCoordinator ( getName() );
		ct = new CompositeTransactionStub ( getName() , true , cc );
		xares = new TestXAResource();
		res = new TestXATransactionalResource ( xares , resourceName );
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
	
	
	public void testCommitOfDeserializedInstanceWithNullResourcePointer() throws Exception
	{
		Configuration.removeResource ( resourceName );
		restx = prepareStreamOutAndRecover  ( restx );
		try {
			restx.commit ( false );
			fail ( "Expected heuristic exception" );
		} catch ( NullPointerException e ) {
			fail ( "Commit of partially recovered instance not handled gracefully" );
		} catch ( HeurHazardException ok ) {}
		
	}

	public void testRollbackOfDeserializedInstanceWithNullResourcePointer() throws Exception
	{
		Configuration.removeResource ( resourceName );
		restx = prepareStreamOutAndRecover  ( restx );
		try {
			restx.rollback();
			fail ( "Expected heuristic exception" );
		} catch ( NullPointerException e ) {
			fail ( "Rollback of partially recovered instance not handled gracefully" );
		} catch ( HeurHazardException ok ) {}
	}
	
	//test for case 59238
	public void testRollbackOfDeserializedInstanceWithSerializableXAResource() throws SysException, IOException, ClassNotFoundException, RollbackException, HeurHazardException, HeurMixedException, HeurCommitException 
	{	
		Configuration.removeResource ( resourceName );
		restx = prepareStreamOutAndRecover ( restx , new SerializableTestXAResource() );
		assertNotNull ( restx.getXAResource() );
		assertTrue ( restx.recover() );
		restx.rollback();
	}
	
	//test for case 59238
	public void testCommitOfDeserializedInstanceWithSerializableXAResource() throws SysException, IOException, ClassNotFoundException, RollbackException, HeurHazardException, HeurMixedException, HeurCommitException, HeurRollbackException 
	{	
		Configuration.removeResource ( resourceName );
		restx = prepareStreamOutAndRecover ( restx , new SerializableTestXAResource() );
		assertNotNull ( restx.getXAResource() );
		assertTrue ( restx.recover() );
		restx.commit ( false );
	}
}
