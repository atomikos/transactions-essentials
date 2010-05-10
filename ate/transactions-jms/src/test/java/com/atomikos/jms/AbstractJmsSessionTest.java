package com.atomikos.jms;

import javax.jms.Connection;
import javax.jms.Session;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.session.TestSessionHandleState;

import junit.framework.TestCase;

public class AbstractJmsSessionTest extends TestCase {


	protected TestSessionHandleState tshs;
	protected Connection conn;
	protected TestXAResource xares;
	protected TestXATransactionalResource res;
	protected TestXAQueueConnection xaconn;
	protected TestConnectionPoolProperties props;
	

	
	
	protected void setUp() throws Exception {
		super.setUp();
		tshs = new TestSessionHandleState();
		xares = new TestXAResource();
		xaconn = new TestXAQueueConnection();
		res = new TestXATransactionalResource ( xares , "testresource" );
		props = new TestConnectionPoolProperties();
		conn = ( Connection ) AtomikosJmsConnectionProxy.newInstance ( xaconn , res , tshs , props );
	}
	
	public void testCloseSession ( Session s ) throws Exception {
		assertFalse ( tshs.isTerminated() );
		s.close();
		assertTrue ( tshs.isTerminated() );
	}
	
	public void testUsingClosedSession ( Session s ) throws Exception {

		s.close();
		//closing again SHOULD be fine: see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/jms/Session.html#close()
		s.close();
		
		//any other method should throw an exception
		try {
			s.createBrowser(null);
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.createBytesMessage();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.createConsumer(null);
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.createTemporaryQueue();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.getTransacted();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.getMessageListener();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		try {
			s.getAcknowledgeMode();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
		
		
		try {
			s.commit();
			fail ( "illegal use of closed session is allowed" );
		} catch ( javax.jms.IllegalStateException ok ) {}
	}

}
