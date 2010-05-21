package com.atomikos.datasource.xa.jca;

import javax.resource.spi.ManagedConnection;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.TestXAResource;

import junit.framework.TestCase;

public class JcaTransactionalResourceTestJUnit extends TestCase {

	private static final String RES_NAME = "RESOURCE";
	private JcaTransactionalResource res;
	private TestManagedConnectionFactory mcf;
	private XAResource xares;

	
	protected void setUp() throws Exception {
		super.setUp();
		xares = new TestXAResource();
		mcf = new TestManagedConnectionFactory ( xares );
		res = new JcaTransactionalResource ( RES_NAME , mcf );
	}
	
	public void testName()
	{
		assertEquals ( RES_NAME , res.getName() );
	}
	
	public void testXAResource()
	{
		assertEquals ( xares , res.getXAResource() );
	}
	
	public void testRefreshXAConnection()
	{
		XAResource xares1 = res.refreshXAConnection();
		ManagedConnection mc = mcf.getLastCreatedManagedConnection();
		assertNotNull ( mc );
		assertEquals ( xares , xares1 );
	}
	
	public void testUsesXaResource()
	{
		assertFalse ( res.usesXAResource ( xares ) );
	}
	
	public void testClose()
	{
		XAResource xares1 = res.refreshXAConnection();
		TestManagedConnection mc = ( TestManagedConnection) mcf.getLastCreatedManagedConnection();
		res.close();
		assertTrue ( mc.wasDestroyed() );
	}
}
