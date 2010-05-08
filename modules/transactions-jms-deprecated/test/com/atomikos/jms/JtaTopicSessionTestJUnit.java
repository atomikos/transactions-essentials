package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;

import junit.framework.TestCase;

public class JtaTopicSessionTestJUnit extends TestCase {
	private JtaTopicSession session;

	private TestTopicSession testSession;
	private TestXAResource xaresource;
	private TestXATransactionalResource resource;
	
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		xaresource = new TestXAResource();
		testSession = new TestTopicSession ( xaresource );
		resource = new TestXATransactionalResource ( xaresource , "TestRes");
		session = new JtaTopicSession ( testSession , resource , xaresource );
	}
	
	public void testClose() throws Exception
	{
		assertFalse ( testSession.isCloseCalled() );
		session.close();
		assertTrue ( testSession.isCloseCalled());
	}
	
	public void testCommit() throws JMSException
	{
		try {
			session.commit();
			fail();
		}
		catch ( TransactionInProgressException ok ) {}
	}
	
	public void testCreateBrowserWithQueue() throws Exception
	{
		assertFalse ( testSession.createBrowserWithQueueCalled() );
		session.createBrowser ( null );
		assertTrue ( testSession.createBrowserWithQueueCalled() );
	}
	
	public void testCreateBrowserWithQueueAndSelector() throws Exception
	{
		assertFalse ( testSession.createBrowserWithQueueAndSelectorCalled() );
		session.createBrowser ( null , "selector" );
		assertTrue ( testSession.createBrowserWithQueueAndSelectorCalled() );
		
	}
	
	public void testCreateBrowser() throws Exception
	{
		assertFalse ( testSession.createBrowserWithQueueCalled() );
		session.createBrowser ( null );
		assertTrue ( testSession.createBrowserWithQueueCalled() );
	  
	}
	public void testCreateBrowserWithSelector() throws Exception
	{
		assertFalse ( testSession.createBrowserWithQueueAndSelectorCalled() );
		session.createBrowser ( null ,"" );
		assertTrue ( testSession.createBrowserWithQueueAndSelectorCalled() );
	  
	}
	
	public void testCreateConsumer() throws Exception
	{
		
		assertNotNull ( session.createConsumer(null));
		assertNotNull ( session.createConsumer(null,null));
		assertNotNull (session.createConsumer(null,null,false));
		assertNotNull ( session.createSubscriber(null));
		assertNotNull (session.createSubscriber(null,null,false));
		assertNotNull ( session.createDurableSubscriber(null,null));
		assertNotNull ( session.createDurableSubscriber(null,null,null,false));
	}
	
	public void testCreateProducer() throws Exception
	{
		assertNotNull ( session.createProducer(null));
		assertNotNull ( session.createPublisher(null));
		
	}
	
	public void testCreateMapMessage() throws Exception
	{
		assertNotNull ( session.createMapMessage() );
	}
	
	public void testCreateObjectMessage() throws Exception
	{
		assertNotNull ( session.createObjectMessage() );
		assertNotNull ( session.createObjectMessage(null) );
	}
	
	public void testCreateTextMessage() throws Exception
	{
		assertNotNull ( session.createTextMessage() );
		assertNotNull ( session.createTextMessage(null) );
	
	}
	
	public void testCreateMessage() throws Exception
	{
		assertNotNull ( session.createMessage());
	}
	
	public void testCreateStreamMessage() throws Exception
	{
		assertNotNull ( session.createStreamMessage());
		
	}
	
	public void testAcknowledgeMode()
	throws Exception
	{
		int ack = 123;
		testSession.setAcknowledgeMode ( ack );
		assertEquals ( ack , session.getAcknowledgeMode() );
		
	}
	
	public void testMessageListener() throws Exception
	{
		assertNull ( session.getMessageListener() );
		TestMessageListener l = new TestMessageListener();
		session.setMessageListener ( l );
		assertEquals ( l , session.getMessageListener() );
	}
	
	public void testTransacted() throws Exception
	{
		assertTrue ( session.getTransacted() );
	}
	
	public void testRecover() throws Exception
	{
		try {
			session.recover();
			fail();
		}	
		catch ( JMSException ok ){}
	}
	
	public void testRollback() throws Exception
	{
		try {
			session.rollback();
			fail();
		}	
		catch ( TransactionInProgressException ok ){}
	}
	
	public void testDurableSubscriber() throws Exception 
	{
		TopicSubscriber s = session.createDurableSubscriber( null , "testname" );
		assertNotNull ( s );
		assertTrue ( s instanceof JtaTopicSubscriber );
	}
	
}
