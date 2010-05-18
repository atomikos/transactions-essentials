package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import junit.framework.TestCase;

import com.atomikos.datasource.xa.TestXAResource;

public class TopicPublisherSessionSimpleTestJUnit extends TestCase 
{

	private TopicPublisherSession session;
	private TopicConnectionFactoryBean qcfb;
	
	private static final int DELIVERYMODE = 1;
	private static final String PW = "SECRET";
	private static final String USER = "USER";
	private static final int PRIORITY = 1;
	private static final long TTL = 100;

	public TopicPublisherSessionSimpleTestJUnit (String name ) 
	{
		super ( name );
	}
	
	protected void setUp()
	{
		TestXAResource xares = new TestXAResource();
		TestTopicConnectionFactory fact = new TestTopicConnectionFactory(xares);
		session = new TopicPublisherSession();
		qcfb = new TopicConnectionFactoryBean();
		qcfb.setXaTopicConnectionFactory(fact);
		qcfb.setResourceName(getClass().getSimpleName());
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
	
	public void testTopic()
	{
		TestTopic queue = new TestTopic();
		session.setTopic ( queue );
		if ( session.getTopic() != queue )
    			fail ( "set/getTopic fails" );
    	
	}
	
	public void testTopicConnectionFactoryBean()
	{
		session.setTopicConnectionFactoryBean ( qcfb );
		if ( session.getTopicConnectionFactoryBean() != qcfb )
    			fail ( "get/setTopicConnectionFactory fails" );
	}
	
	public void testReplyToTopic()
	{
		TestTopic queue = new TestTopic();
		session.setReplyToTopic ( queue );
		if ( session.getReplyToTopic() != queue )
    			fail ( "set/getReplyToTopic fails" );
	}
	
	public void testTimeToLive() 
	{
		session.setTimeToLive ( TTL );
		if ( session.getTimeToLive() != TTL )
    			fail ( "set/getTimeToLive fails");
		
	}
	
	public void testCreateTextMessage() throws JMSException
	{
		TestTopic queue = new TestTopic();
		session.setTopic ( queue );
		session.setTopicConnectionFactoryBean ( qcfb );
		TextMessage msg = session.createTextMessage();
		assertNotNull ( msg );
	}

}
