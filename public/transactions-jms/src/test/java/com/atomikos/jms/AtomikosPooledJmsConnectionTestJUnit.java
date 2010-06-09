package com.atomikos.jms;

import java.lang.reflect.Proxy;

import javax.jms.Session;

import junit.framework.TestCase;

import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;

public class AtomikosPooledJmsConnectionTestJUnit extends TestCase {

	private TestXAResource xares;
	private TestXAQueueConnection xaconn;
	private TestXATransactionalResource res;
	private AtomikosPooledJmsConnection conn;
	private TestConnectionPoolProperties props;
	private TestXPooledConnectionEventListener listener;

	protected void setUp() throws Exception {
		super.setUp();
		xares = new TestXAResource();
		xaconn = new TestXAQueueConnection();
		res = new TestXATransactionalResource ( xares , "testresource" );
		props = new TestConnectionPoolProperties();
		conn = new AtomikosPooledJmsConnection ( xaconn , res , props );
		listener = new TestXPooledConnectionEventListener();
		conn.registerXPooledConnectionEventListener ( listener );
	}
	
	public void testInitialState() throws Exception 
	{
		assertFalse ( conn.isErroneous() );
		assertFalse ( conn.isInTransaction(null) );
		assertTrue ( conn.isAvailable() );
	}
	
	public void testWithConnectionProxy() throws Exception {

		Reapable r = conn.createConnectionProxy ( null );
		assertFalse ( conn.isAvailable() );
		assertFalse ( conn.isErroneous() );
		assertFalse ( conn.isInTransaction(null) );
		assertNotNull ( r );
		assertFalse ( listener.isTerminated() );
		Session s = ( ( javax.jms.Connection) r).createSession ( true , 0 );
		Object proxy = Proxy.getInvocationHandler ( s );
		assertTrue ( proxy.getClass().getName() , proxy instanceof AtomikosJmsXaSessionProxy );
		s.close();
		assertFalse ( conn.isAvailable() );
		r.close();
		assertTrue ( conn.isAvailable() );
		assertFalse ( conn.isErroneous() );
		assertFalse ( conn.isInTransaction(null) );
		assertTrue ( listener.isTerminated() );
	}
	
	public void testWithConnectionProxyInLocalTransactionMode() throws Exception {
		props.setLocalTransactionMode(true);
		Reapable r = conn.createConnectionProxy ( null );
		assertFalse ( conn.isAvailable() );
		assertFalse ( conn.isErroneous() );
		assertFalse ( conn.isInTransaction(null) );
		assertNotNull ( r );
		assertFalse ( listener.isTerminated() );
		Session s = ( ( javax.jms.Connection) r).createSession ( true , 0 );
		Object proxy = Proxy.getInvocationHandler ( s );
		assertTrue ( proxy.getClass().getName() , proxy instanceof AtomikosJmsNonXaSessionProxy );
		s.close();
		assertFalse ( conn.isAvailable() );
		r.close();
		assertTrue ( conn.isAvailable() );
		assertFalse ( conn.isErroneous() );
		assertFalse ( conn.isInTransaction(null) );
		assertTrue ( listener.isTerminated() );
	}
	
	public void testCloseConnectionProxyWithOpenSessionMakesPooledConnectionAvailable() throws Exception {
		assertTrue ( conn.isAvailable() );
		Reapable r = conn.createConnectionProxy ( null );
		assertFalse ( conn.isAvailable() );
		//create session and leave it open
		Session s = ( ( javax.jms.Connection) r).createSession ( true , 0 );
		assertFalse ( conn.isAvailable() );
		//close proxy but DON'T close session -> should be closed by proxy!
		r.close();
		assertTrue ( conn.isAvailable() );
		assertTrue ( listener.isTerminated() );
	}
	
	public void testDestroy() throws Exception {
		assertFalse ( xaconn.closeCalled() );
		conn.destroy();
		assertTrue ( xaconn.closeCalled() );
	}
	
	
	public void testReapUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException 
	{
		long first = conn.getLastTimeReleased();
		Thread.sleep ( 10 );
		//pc.createConnectionProxy ( null );
		conn.reap();
		long second = conn.getLastTimeReleased();
		assertTrue ( "reaping does not update the lastTimeReleased value?" , second > first );
	}

	public void testCloseUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException {
		long first = conn.getLastTimeReleased();
		Thread.sleep ( 10 );
		Reapable proxy = conn.createConnectionProxy(null);
		proxy.close();
		long second = conn.getLastTimeReleased();
		assertTrue ( "closing does not update the lastTimeReleased value?" , second > first );
	}
	
	private static class TestXPooledConnectionEventListener implements XPooledConnectionEventListener {

		private boolean terminated; 
		
		public void onXPooledConnectionTerminated(XPooledConnection connection) {
			terminated = true;
			
		}
		
		public boolean isTerminated() {
			return terminated;
		}
		
	}
	
	
	

}
