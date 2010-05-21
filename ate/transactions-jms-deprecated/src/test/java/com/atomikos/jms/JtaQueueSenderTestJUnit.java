package com.atomikos.jms;

import javax.jms.JMSException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;

import junit.framework.TestCase;

public class JtaQueueSenderTestJUnit extends TestCase 
{
	private JtaQueueSender sender;
	private TestQueueSender testSender;
	private TestQueue queue;
	
	protected void setUp() throws Exception {
		super.setUp();
		queue = new TestQueue();
		testSender = new TestQueueSender( queue);
		TestXAResource xares = new TestXAResource();
		sender = new JtaQueueSender ( testSender , 
				new TestXATransactionalResource ( xares , "testresource") , 
				xares );
	}
	
	public void testQueue() throws JMSException
	{
		assertEquals ( queue , sender.getQueue() );
	}
	
	public void testDestination() throws JMSException
	{
		assertEquals ( queue , sender.getDestination() );
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
