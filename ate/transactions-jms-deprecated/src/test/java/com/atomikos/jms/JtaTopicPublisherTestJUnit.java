package com.atomikos.jms;

import javax.jms.JMSException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;

import junit.framework.TestCase;

public class JtaTopicPublisherTestJUnit extends TestCase {

	private JtaTopicPublisher sender;
	private TestTopicPublisher testSender;
	private TestTopic topic;
	
	protected void setUp() throws Exception {
		super.setUp();
		topic = new TestTopic();
		testSender = new TestTopicPublisher(topic);
		TestXAResource xares = new TestXAResource();
		sender = new JtaTopicPublisher ( testSender , 
				new TestXATransactionalResource ( xares , "testresource") , 
				xares );
	}
	
	public void testTopic() throws JMSException
	{
		assertEquals ( topic , sender.getTopic() );
	}
	
	public void testDestination() throws JMSException
	{
		assertEquals ( topic , sender.getDestination() );
	}
	
	public void testDisableMessageId() throws JMSException
	{
		assertFalse ( sender.getDisableMessageID() );
		sender.setDisableMessageID ( true );
		assertTrue ( sender.getDisableMessageID() );
	}
	
	public void testDisableMessageTimestamp() throws JMSException
	{
		assertFalse ( sender.getDisableMessageTimestamp() );
		sender.setDisableMessageTimestamp ( true );
		assertTrue ( sender.getDisableMessageTimestamp() );
	}

	public void testDeliveryMode() throws Exception
	{
		int mode = 1;
		sender.setDeliveryMode(mode );
		assertEquals ( mode , sender.getDeliveryMode() );
	}
	
	public void testPriority () throws Exception
	{
		int pty = 2;
		sender.setPriority(pty);
		assertEquals ( pty , sender.getPriority() );
	}
	
	public void testTtl() throws Exception
	{
		long ttl = 10;
		sender.setTimeToLive(ttl);
		assertEquals ( sender.getTimeToLive() , ttl );
	}
	
	public void testClose() throws Exception
	{
		assertFalse (testSender.closeCalled() );
		sender.close();
		assertTrue (testSender.closeCalled() );
	}

}
