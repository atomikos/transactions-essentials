package com.atomikos.jms;

import java.lang.reflect.Proxy;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XASession;

import junit.framework.TestCase;

import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.session.TestSessionHandleState;

public class AtomikosJmsConnectionProxyTestJUnit extends TestCase {

	private TestSessionHandleState tshs;
	private Connection conn;
	
	private TestXAResource xares;
	private TestXATransactionalResource res;
	private TestXAQueueConnection xaconn;
	private TestConnectionPoolProperties props;
	
	protected void setUp() throws Exception {
		super.setUp();
		tshs = new TestSessionHandleState();
		xares = new TestXAResource();
		xaconn = new TestXAQueueConnection();
		res = new TestXATransactionalResource ( xares , "testresource" );
		props = new TestConnectionPoolProperties();
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( xaconn , res , tshs , props);
	}
	
	protected AtomikosJmsConnectionProxy getProxy ( Connection c ) {
		return ( AtomikosJmsConnectionProxy ) Proxy.getInvocationHandler ( c );
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testProxyClass() throws Exception {
		assertTrue ( Proxy.getInvocationHandler ( conn ) instanceof AtomikosJmsConnectionProxy );
		assertTrue ( conn instanceof Reapable );
	}
	
	public void testCloseWithoutSessions() throws Exception 
	{
		assertFalse ( getProxy ( conn ).isAvailable() );
		conn.close();
		assertTrue ( getProxy ( conn ).isAvailable() );
		getProxy ( conn ).destroy();
	}

	public void testUsingConnectionAfterClose() throws Exception
	{
		conn.close();
		//closing connection again should work - see javadoc for JMS
		conn.close();
		
		try {
			conn.createSession( true , 0 );
			fail ( "connection usable after close" );
		} catch ( javax.jms.IllegalStateException ok ) {}
	}
	
	public void testCloseWithXaSessions() throws Exception 
	{
		assertFalse ( getProxy ( conn ).isAvailable() );
		Session s1 = conn.createSession( true, 0 );
		assertFalse ( tshs.isTerminated() );
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		assertFalse ( getProxy ( conn ).isInTransaction(null));
		Session s2 = conn.createSession( true, 0 );
		s1.close();
		assertTrue ( tshs.isTerminated() );
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		s2.close();
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		conn.close();
		assertTrue ( getProxy ( conn ).isAvailable() );
		getProxy ( conn ).destroy();
	}
	
	public void testCloseWithNonXaSessions() throws Exception 
	{
		props.setLocalTransactionMode(true);
		assertFalse ( getProxy ( conn ).isAvailable() );
		Session s1 = conn.createSession( true, 0 );
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		assertFalse ( getProxy ( conn ).isInTransaction(null));
		Session s2 = conn.createSession( true, 0 );
		s1.close();
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		s2.close();
		assertFalse ( getProxy ( conn ).isAvailable() );
		assertFalse ( getProxy ( conn ).isErroneous() );
		conn.close();
		assertTrue ( getProxy ( conn ).isAvailable() );
		getProxy ( conn ).destroy();
	}
	
	
	public void testIsInTransaction() throws Exception 
	{
		assertFalse ( getProxy ( conn ).isInTransaction ( null ));
	}
	
	public void isErroneous() throws Exception
	{
		assertFalse ( getProxy ( conn ).isErroneous() );
	}
	
	
	public void testCreateTransactedSessionInLocalMode() throws Exception {
		props.setLocalTransactionMode(true);
		Session s = conn.createSession ( true , 0 );
		//flag true means transacted session, but NOT JTA due to local flag
		assertTrue ( Proxy.getInvocationHandler ( s )  instanceof AtomikosJmsNonXaSessionProxy );
		assertTrue ( s.getTransacted() );
		assertFalse ( s instanceof Reapable );
	}
	
	public void testCreateNonTransactedSession() throws Exception {
		Session s = conn.createSession ( false , Session.AUTO_ACKNOWLEDGE );
		//flag false means non-transacted session
		assertTrue( Proxy.getInvocationHandler ( s )  instanceof AtomikosJmsNonXaSessionProxy );
		assertFalse ( s.getTransacted() );
		assertFalse ( s instanceof Reapable );
	}

	public void testCreateTransactedSessionInJtaMode() throws Exception {
		Session s = conn.createSession ( true , 0 );
		//flag true means transacted session
		assertTrue ( Proxy.getInvocationHandler ( s )  instanceof AtomikosJmsXaSessionProxy );
		assertTrue ( s.getTransacted() );
		assertFalse ( s instanceof Reapable );

	}
	
	public void testIsErroneousWithException() throws Exception {
		
		TestXAQueueConnection tconn = new TestXAQueueConnection() {
			public XASession createXASession () throws JMSException {
				throw new JMSException ( "Simulated error" );
			}
		};
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( tconn , res , tshs , props);
		AtomikosJmsConnectionProxy proxy = getProxy ( conn );
		try {
			conn.createSession(true, 0);
			fail ( "no error with create of session");
		} catch ( JMSException ok ) {
		}
		assertTrue ( proxy.isErroneous());
	}
	
	public void testIsErroneousWithIllegalStateException() throws Exception {
		
		TestXAQueueConnection tconn = new TestXAQueueConnection() {
			public XASession createXASession () throws JMSException {
				throw new javax.jms.IllegalStateException ( "Simulated error" );
			}
		};
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( tconn , res , tshs , props);
		AtomikosJmsConnectionProxy proxy = getProxy ( conn );
		try {
			conn.createSession(true, 0);
			fail ( "no error with create of session");
		} catch ( javax.jms.IllegalStateException ok ) {
		}
		assertTrue ( proxy.isErroneous());
	}
	
	public void testClosingConnectionClosesAllPendingXaSessions() throws Exception {
		Session s = conn.createSession( true, 0 );
		conn.close();
		//session proxy must be closed, or no terminated callbacks will happen!
		AbstractJmsSessionProxy proxy = ( AbstractJmsSessionProxy ) Proxy.getInvocationHandler ( s );
		assertTrue ( proxy.isAvailable() );
	}
	
	public void testClosingConnectionClosesAllPendingNonXaSessions() throws Exception {
		props.setLocalTransactionMode(true);
		Session s = conn.createSession( true, 0 );
		conn.close();
		//session proxy must be closed, or no terminated callbacks will happen!
		AbstractJmsSessionProxy proxy = ( AbstractJmsSessionProxy ) Proxy.getInvocationHandler ( s );
		assertTrue ( proxy.isAvailable() );
	}
	
	public void testCloseConnectionWithoutSessionsNotifiesOwner() throws Exception 
	{
		conn.close();
		assertTrue ( tshs.isTerminated() );
	}
	
	public void testReap() 
	{
		assertTrue ( conn instanceof Reapable );
		Reapable r = ( Reapable ) conn;
		assertFalse ( getProxy ( conn ).isErroneous() );
		assertFalse ( getProxy ( conn ).isAvailable() );
		r.reap();
		assertTrue ( getProxy ( conn ).isErroneous() );
		assertTrue ( getProxy ( conn ).isAvailable() );
	}
	
	public void testWithErroneousNonXaSessionProxy() throws Exception 
	{
		//assert that errors in session proxy will invalidate conn proxy
		assertFalse ( getProxy ( conn ).isErroneous() );
		Session s = conn.createSession ( false , 0 );
		try {
			s.commit();
			fail ( "no exception on commit of nontx session" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		assertTrue ( getProxy ( conn ).isErroneous() );
	}
	
	public void testWithErroneousXaSessionProxy() throws Exception 
	{
		//assert that errors in session proxy will invalidate conn proxy
		assertFalse ( getProxy ( conn ).isErroneous() );
		Session s = conn.createSession ( true , 0 );
		try {
			s.commit();
			fail ( "no exception on commit of xa session" );
		} catch ( javax.jms.TransactionInProgressException ok ) {}
		assertFalse ( getProxy ( conn ).isErroneous() );
		
		
	}
	
	public void testIfProxyPreservesVendorSpecificJMSExceptionInLocalTransactionMode() throws Exception
	{
		final String ERROR = "Simulated JMS error";
		xaconn = new TestXAQueueConnection () {
			public Session createSession(boolean transacted, int ack) throws JMSException {
				throw new JMSException ( ERROR );
			}
		};
		props.setLocalTransactionMode ( true );
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( xaconn , res , tshs , props);
		try {
			conn.createSession ( true , 0 );
			fail ( "No exception on createSession?" );
		} catch ( JMSException ok ) {
			String expectedMsg = ERROR;
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testIfProxyPreservesVendorSpecificJMSExceptionInXaMode() throws Exception
	{
		final String ERROR = "Simulated JMS error";
		xaconn = new TestXAQueueConnection () {
			public XASession createXASession() throws JMSException {
				throw new JMSException ( ERROR );
			}
		};
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( xaconn , res , tshs , props);
		try {
			conn.createSession ( true , 0 );
			fail ( "No exception on createSession?" );
		} catch ( JMSException ok ) {
			String expectedMsg = ERROR;
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	
}
