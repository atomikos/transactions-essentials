package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import junit.framework.TestCase;

import com.atomikos.datasource.xa.TestXAResource;

public class QueueSenderSessionSimpleTestJUnit extends TestCase {

	private QueueSenderSession session;
	private QueueConnectionFactoryBean qcfb;
	
	private static final int DELIVERYMODE = 1;
	private static final String PW = "SECRET";
	private static final String USER = "USER";
	private static final int PRIORITY = 1;
	private static final long TTL = 100;

	public QueueSenderSessionSimpleTestJUnit (String name ) 
	{
		super ( name );
	}
	
	protected void setUp()
	{
		TestXAResource xares = new TestXAResource();
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory(xares);
		session = new QueueSenderSession();
		qcfb = new QueueConnectionFactoryBean();
		qcfb.setXaQueueConnectionFactory(fact);
		qcfb.setResourceName("TESTQUEUERESOURCE");
	}
	
	protected void tearDown()
	{
		session.stop();
		session = null;
	}
	
	public void testDeliveryMode() 
	{
		session.setDeliveryMode ( DELIVERYMODE );
		if ( session.getDeliveryMode() != DELIVERYMODE )
    			fail ( "get/setDeliveryMode fails" );
	}
	
	public void testPassword()
	{
		
		session.setPassword ( PW );
	}
	
	public void testUser()
	{
		
		session.setUser ( USER);
    		if ( !session.getUser().equals ( USER ) )
    			fail ( "get/setUser fails" );
	}
	
	public void testPriority()
	{
		session.setPriority ( PRIORITY );
		if ( session.getPriority() != PRIORITY )
			fail ( "get/setPriority fails" );
	}
	
	public void testQueue()
	{
		TestQueue queue = new TestQueue();
		session.setQueue ( queue );
		if ( session.getQueue() != queue )
    			fail ( "set/getQueue fails" );
    	
	}
	
	public void testQueueConnectionFactoryBean()
	{
		session.setQueueConnectionFactoryBean ( qcfb );
		if ( session.getQueueConnectionFactoryBean() != qcfb )
    			fail ( "get/setQueueConnectionFactory fails" );
	}
	
	public void testReplyToQueue()
	{
		TestQueue queue = new TestQueue();
		session.setReplyToQueue ( queue );
		if ( session.getReplyToQueue() != queue )
    			fail ( "set/getReplyToQueue fails" );
	}
	
	public void testTimeToLive() 
	{
		session.setTimeToLive ( TTL );
		if ( session.getTimeToLive() != TTL )
    			fail ( "set/getTimeToLive fails");
		
	}
	
	public void testCreateTextMessage() throws JMSException
	{
		TestQueue queue = new TestQueue();
		session.setQueue ( queue );
		session.setQueueConnectionFactoryBean ( qcfb );
		TextMessage msg = session.createTextMessage();
		assertNotNull ( msg );
	}

}
