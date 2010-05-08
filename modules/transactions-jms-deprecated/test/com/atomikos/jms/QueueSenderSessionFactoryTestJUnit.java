package com.atomikos.jms;

import junit.framework.TestCase;

public class QueueSenderSessionFactoryTestJUnit extends TestCase 
{
	private static final int DELIVERY_MODE = 1;
	private static final int PRIORITY = 2;
	private static final int TTL = 30;
	private static final String PW = "password";
	private static final String USER = "user";
	
	private QueueSenderSessionFactory factory;

	public QueueSenderSessionFactoryTestJUnit ( String name ) 
	{
		super ( name );
	}
	
	protected void setUp()
	{
		factory = new QueueSenderSessionFactory();
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
	
	public void testQueue()
	{
		TestQueue queue = new TestQueue();
		factory.setQueue  ( queue );
		assertEquals ( factory.getQueue() , queue );
	}
	
	public void testQueueConnectionFactoryBean()
	{
		QueueConnectionFactoryBean qcfb = new QueueConnectionFactoryBean();
		factory.setQueueConnectionFactoryBean ( qcfb );
		assertEquals ( qcfb , factory.getQueueConnectionFactoryBean() );
	}
	
	public void testReplyToQueue()
	{
		TestQueue queue = new TestQueue();
		factory.setReplyToQueue ( queue );
		assertEquals ( factory.getReplyToQueue() , queue );
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
		QueueConnectionFactoryBean qcfb = new QueueConnectionFactoryBean();
		factory.setDeliveryMode(1);
		if (factory.getDeliveryMode() != 1)
			fail("get/setDeliveryMode fails");
		factory.setPriority(2);
		if (factory.getPriority() != 2)
			fail("get/setPriority fails");
		TestQueue queue = new TestQueue();
		factory.setQueue(queue);
		if (factory.getQueue() != queue)
			fail("get/setQueue fails");
		factory.setQueueConnectionFactoryBean(qcfb);
		if (factory.getQueueConnectionFactoryBean() != qcfb)
			fail("get/setQueueConnectionFactoryBean fails ");
		factory.setReplyToQueue(queue);
		if (factory.getReplyToQueue() != queue)
			fail("get/setReplyToQueue fails");
		factory.setTimeToLive(111);
		if (factory.getTimeToLive() != 111)
			fail("get/setTimeToLive fails");
		factory.setUser("user");
		if (!factory.getUser().equals("user"))
			fail("get/setUser fails");

		// assert that the sender session gets the same properties
		QueueSenderSession session = factory.createQueueSenderSession();
		if (session.getDeliveryMode() != 1)
			fail("deliveryMode not inherited");
		if (session.getPriority() != 2)
			fail("priority not inherited");
		if (session.getQueue() != queue)
			fail("queue not inherited");
		if (session.getQueueConnectionFactoryBean() != qcfb)
			fail("queueConnectionFactoryBean not inherited");
		if (session.getReplyToQueue() != queue)
			fail("queue not inherited");
		if (session.getTimeToLive() != 111)
			fail("TTL not inherited");
		if (!session.getUser().equals("user"))
			fail("user not inherited");
	}
	
}
