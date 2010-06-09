package com.atomikos.jms;

import junit.framework.TestCase;

public class TopicPublisherSessionFactoryTestJUnit extends TestCase 
{
	private static final int DELIVERY_MODE = 1;
	private static final int PRIORITY = 2;
	private static final int TTL = 30;
	private static final String PW = "password";
	private static final String USER = "user";
	
	private TopicPublisherSessionFactory factory;

	public TopicPublisherSessionFactoryTestJUnit ( String name ) 
	{
		super ( name );
	}
	
	protected void setUp()
	{
		factory = new TopicPublisherSessionFactory();
	}
	
	public void testDeliveryMode()
	{
		factory.setDeliveryMode ( DELIVERY_MODE );
		assertEquals ( DELIVERY_MODE , factory.getDeliveryMode() );
	}
	
	public void testPassword()
	{
		factory.setPassword ( PW );
	}
	
	public void testPriority() 
	{
		factory.setPriority ( PRIORITY );
		assertEquals ( PRIORITY , factory.getPriority() );
	}
	
	public void testTopic()
	{
		TestTopic queue = new TestTopic();
		factory.setTopic  ( queue );
		assertEquals ( factory.getTopic() , queue );
	}
	
	public void testTopicConnectionFactoryBean()
	{
		TopicConnectionFactoryBean qcfb = new TopicConnectionFactoryBean();
		factory.setTopicConnectionFactoryBean ( qcfb );
		assertEquals ( qcfb , factory.getTopicConnectionFactoryBean() );
	}
	
	public void testReplyToTopic()
	{
		TestTopic queue = new TestTopic();
		factory.setReplyToTopic ( queue );
		assertEquals ( factory.getReplyToTopic() , queue );
	}
	
	public void testTimeToLive()
	{
		factory.setTimeToLive ( TTL );
		assertEquals ( TTL , factory.getTimeToLive() );
	}
	
	public void testUser()
	{
		factory.setUser  ( USER );
		assertEquals ( USER , factory.getUser() );
	}
	
	public void testPropagationOfPropertiesToSession()
	{
		TopicConnectionFactoryBean qcfb = new TopicConnectionFactoryBean();
		factory.setDeliveryMode(1);
		if (factory.getDeliveryMode() != 1)
			fail("get/setDeliveryMode fails");
		factory.setPriority(2);
		if (factory.getPriority() != 2)
			fail("get/setPriority fails");
		TestTopic queue = new TestTopic();
		factory.setTopic(queue);
		if (factory.getTopic() != queue)
			fail("get/setTopic fails");
		factory.setTopicConnectionFactoryBean(qcfb);
		if (factory.getTopicConnectionFactoryBean() != qcfb)
			fail("get/setTopicConnectionFactoryBean fails ");
		factory.setReplyToTopic(queue);
		if (factory.getReplyToTopic() != queue)
			fail("get/setReplyToTopic fails");
		factory.setTimeToLive(111);
		if (factory.getTimeToLive() != 111)
			fail("get/setTimeToLive fails");
		factory.setUser("user");
		if (!factory.getUser().equals("user"))
			fail("get/setUser fails");

		// assert that the sender session gets the same properties
		TopicPublisherSession session = factory.createTopicPublisherSession();
		if (session.getDeliveryMode() != 1)
			fail("deliveryMode not inherited");
		if (session.getPriority() != 2)
			fail("priority not inherited");
		if (session.getTopic() != queue)
			fail("queue not inherited");
		if (session.getTopicConnectionFactoryBean() != qcfb)
			fail("queueConnectionFactoryBean not inherited");
		if (session.getReplyToTopic() != queue)
			fail("queue not inherited");
		if (session.getTimeToLive() != 111)
			fail("TTL not inherited");
		if (!session.getUser().equals("user"))
			fail("user not inherited");
	}
	
}
