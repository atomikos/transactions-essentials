package com.atomikos.jms;

import java.lang.reflect.Proxy;

import javax.jms.Session;


public class AtomikosJmsNonXaSessionProxyTestJUnit extends AbstractJmsSessionTest {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testCloseSession ( Session s ) throws Exception {
		super.testCloseSession ( s );
		AtomikosJmsNonXaSessionProxy sp = ( AtomikosJmsNonXaSessionProxy ) Proxy.getInvocationHandler ( s );
		assertTrue ( sp.isAvailable() );
		assertFalse ( sp.isErroneous() );
		assertFalse ( sp.isInTransaction(null) );
	}

	public void testSessionCommitAllowedForSessionInLocalTransactionMode() throws Exception 
	{
		props.setLocalTransactionMode(true);
		Session s = conn.createSession ( true , 0 );
		s.commit();
	}

	public void testSessionCommitNotAllowedForNonTransactedSession() throws Exception {
	
		Session s = conn.createSession ( false , Session.CLIENT_ACKNOWLEDGE );
		try {
			s.commit();
			fail ( "commit allowed on non-tx session" );
		} catch ( javax.jms.IllegalStateException ok ) {}
	}

	public void testSessionRollbackAllowedForSessionInLocalTransactionMode() throws Exception 
	{
		props.setLocalTransactionMode(true);		
		Session s = conn.createSession ( true , 0 );
		s.rollback();
	}

	public void testSessionRollbackNotAllowedForNonTransactedSession() throws Exception {
	
		Session s = conn.createSession ( false , Session.CLIENT_ACKNOWLEDGE );
		try {
			s.rollback();
			fail ( "rollback allowed on non-tx session" );
		} catch ( javax.jms.IllegalStateException ok ) {}
	}

	public void testUsingClosedSessionInLocalTransactionMode() throws Exception {
		props.setLocalTransactionMode(true);		
		Session s = conn.createSession ( true , 0 );
		testUsingClosedSession(s);
	}

	public void testUsingClosedSessionInNonTransactionalMode() throws Exception {
		Session s = conn.createSession ( false , Session.CLIENT_ACKNOWLEDGE );
		testUsingClosedSession(s);
	}

	public void testCloseTransactedSessionInNonTransactedMode() throws Exception {
		Session s = conn.createSession ( false , Session.CLIENT_ACKNOWLEDGE );
		testCloseSession(s);
	}

	public void testCloseTransactedSessionInLocalTransactionMode() throws Exception {

		props.setLocalTransactionMode(true);
		Session s = conn.createSession ( true , 0 );
		testCloseSession(s);
	}
	
	

}
