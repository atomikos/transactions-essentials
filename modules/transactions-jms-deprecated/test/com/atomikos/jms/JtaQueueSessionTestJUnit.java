package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.TransactionInProgressException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;

import junit.framework.TestCase;

public class JtaQueueSessionTestJUnit extends TestCase {

	private JtaQueueSession session;

	private TestQueueSession testSession;
	private TestXAResource xaresource;
	private TestXATransactionalResource resource;
	
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		xaresource = new TestXAResource();
		testSession = new TestQueueSession ( xaresource );
		resource = new TestXATransactionalResource ( xaresource , "TestRes");
		session = new JtaQueueSession ( testSession , resource , xaresource );
	}
	
	public void testClose() throws Exception
	{
		assertFalse ( testSession.isCloseCalled() );
		session.close();
		assertTrue ( testSession.isCloseCalled() );
	}
	
	public void testCreateTemporaryQueue() throws Exception
	{
		
		assertFalse ( testSession.createTemporaryQueueCalled() );
		session.createTemporaryQueue();
		assertTrue ( testSession.createTemporaryQueueCalled() );
	}
	
	public void testCreateBrowserWithQueueAndSelector() throws Exception
	{
		assertFalse ( testSession.createBrowserWithQueueAndSelectorCalled() );
		session.createBrowser ( null , null );
		assertTrue ( testSession.createBrowserWithQueueAndSelectorCalled() );
	}

	public void testCreateBrowser() throws Exception
	{
		assertFalse ( testSession.createBrowserCalled() );
		session.createBrowser ( null );
		assertTrue ( testSession.createBrowserCalled() );
	}
	
	public void testCreateSender() throws Exception
	{
		assertFalse ( testSession.createSenderCalled() );
		session.createSender ( null );
		assertTrue ( testSession.createSenderCalled() );
	}
	
	public void testCreateReceiverWithSelector() throws Exception
	{
		assertFalse ( testSession.createReceiverWithSelectorCalled() );
		session.createReceiver ( null , null );
		assertTrue ( testSession.createReceiverWithSelectorCalled() );
	}
	
	public void testCreateReceiver() throws Exception
	{
		assertFalse ( testSession.createReceiverCalled() );
		session.createReceiver ( null  );
		assertTrue ( testSession.createReceiverCalled() );
	}
	
	public void testCreateQueue() throws Exception
	{
		assertFalse ( testSession.createQueueCalled() );
		session.createQueue ( null  );
		assertTrue ( testSession.createQueueCalled() );
	}
	
	public void testCommit() throws JMSException 
	{
		try {
			session.commit();
			fail ( "commit works");
		}
		catch ( TransactionInProgressException ok ) {}
	}
	
	public void testTransacted() throws JMSException
	{
		assertTrue ( session.getTransacted() );
	}
	
	public void testMessageListener() throws JMSException
	{
		assertNull ( session.getMessageListener() );
		TestMessageListener l = new TestMessageListener();
		session.setMessageListener ( l );
		assertEquals ( l , session.getMessageListener() );
	}
	
	public void testRecover()
	{
		try {
			session.recover();
			fail ( "recover works");
		}
		catch ( Exception ok ){}
	}
	
	public void testCreateTextMessage() throws JMSException
	{
		assertNotNull ( session.createTextMessage () );
	}
	
	public void testCreateTextMessageWithString() throws JMSException
	{
		String bla = "bla";
		TextMessage msg =  session.createTextMessage ( bla);
		assertNotNull (msg  );
		assertEquals ( bla , msg.getText() );
	}	
	
	public void testCreateStreamMessage() throws Exception
	{
		assertNotNull ( session.createStreamMessage ( ) );
	}
	
	public void testCreateObjectMessage() throws Exception
	{
		assertNotNull ( session.createObjectMessage() );
		assertNotNull ( session.createObjectMessage(null) );
	}
	
	public void testCreateMessage() throws Exception
	{
		assertNotNull ( session.createMessage() );
	}
	
	public void testCreateMapMessage() throws Exception
	{
		assertNotNull ( session.createMapMessage() );
	}
	
	public void testAcknowledgeMode()
	throws Exception
	{
		assertEquals ( testSession.getAcknowledgeMode() , session.getAcknowledgeMode() );
	}
	
	public void testCreateConsumer() throws JMSException 
	{
		Queue queue = new TestQueue();
		assertNotNull ( session.createConsumer( queue ) );
		assertNotNull ( session.createConsumer( queue , "" ) );
		assertNotNull ( session.createConsumer( queue  , ""  , false ) );

		assertNotNull ( session.createConsumer( queue  , ""  , true) );
	}
	
	public void testCreateProducer() throws Exception
	{

		Queue queue = new TestQueue();
		assertNotNull ( session.createProducer ( queue ) );
	}
	
	public void testCreateTopic() throws Exception
	{
		try {
			session.createTopic(null);
		}
		catch ( Exception ok ) {}
	}
	
	public void testCreateTemporaryTopic() throws Exception
	{
		try {
			session.createTemporaryTopic();
		}
		catch ( Exception ok ) {}
	}
	
	public void testCreateDurableSubscriber() throws Exception
	{
		try {
			session.createDurableSubscriber ( null , null );
		}
		catch ( Exception ok ) {}
		try {
			session.createDurableSubscriber ( null , null , null , false );
			
		}
		catch ( Exception ok ) {}
	}
}

