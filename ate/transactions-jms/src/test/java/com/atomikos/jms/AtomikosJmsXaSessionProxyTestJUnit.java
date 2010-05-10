package com.atomikos.jms;


import java.lang.reflect.Proxy;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;


public class AtomikosJmsXaSessionProxyTestJUnit extends AbstractJmsSessionTest {

	
	public void testCloseSession ( Session s ) throws Exception {
		super.testCloseSession ( s );
		AtomikosJmsXaSessionProxy sp = ( AtomikosJmsXaSessionProxy ) Proxy.getInvocationHandler ( s );
		assertTrue ( sp.isAvailable() );
		assertFalse ( sp.isErroneous() );
		assertFalse ( sp.isInTransaction(null) );
	}

	public void testCloseTransactedSessionInJtaMode() throws Exception {
		Session s = conn.createSession ( true , 0 );
		testCloseSession ( s );
	}


	public void testUsingClosedSessionInJtaMode() throws Exception {
		Session s = conn.createSession ( true , 0 );
		testUsingClosedSession(s);
	}


	public void testSessionRollbackNotAllowedInJtaMode() throws Exception {
		Session s = conn.createSession ( true , 0 );
		try {
			s.rollback();
			fail ( "rollback works on transacted JTA session");
		} catch ( TransactionInProgressException ok ) {}
	}


	public void testSessionCommitNotAllowedInJtaMode() throws Exception {
		Session s = conn.createSession ( true , 0 );
		try {
			s.commit();
			fail ( "commit works on transacted JTA session");
		} catch ( TransactionInProgressException ok ) {}
	}

	public void testCreateMessageConsumer() throws Exception {
		Session s = conn.createSession ( true , 0 );
		MessageConsumer mc = s.createConsumer ( null );
		assertNotNull ( mc );
		assertTrue ( mc instanceof AtomikosJmsMessageConsumerProxy );
	}
	
	public void testCreateMessageProducer() throws Exception {
		Session s = conn.createSession ( true , 0 );
		MessageProducer mp = s.createProducer ( null );
		assertNotNull ( mp );
		assertTrue ( mp instanceof AtomikosJmsMessageProducerProxy );
	}
	
	public void testCreateDurableSubscriber() throws Exception {
		Session s = conn.createSession ( true , 0 );
		TopicSubscriber ts = s.createDurableSubscriber(null, null);
		assertNotNull ( ts );
		assertTrue ( ts instanceof AtomikosJmsTopicSubscriberProxy );
	}

}
