package com.atomikos.jms;

import javax.jms.Connection;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import junit.framework.TestCase;

import com.atomikos.icatch.system.Configuration;

public class QueueConnectionFactoryBeanTestJUnit extends TestCase 
{
	
	private  QueueConnectionFactoryBean  bean;
	
	public QueueConnectionFactoryBeanTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
		
		bean = new QueueConnectionFactoryBean();
		
	}
	
	public void testInit()
	throws Exception
	{
		TestQueueConnectionFactory f = new TestQueueConnectionFactory();
		bean.setXaQueueConnectionFactory(f);
		bean.setResourceName ( "Name" );
		bean.init();
		assertNotNull ( Configuration.getResource ( bean.getResourceName() ) );
	}
	
	public void testCreateConnection()
	throws Exception
	{
		TestQueueConnectionFactory f = new TestQueueConnectionFactory();
		bean.setXaQueueConnectionFactory(f);
		bean.setResourceName("TestQueueConnectionFactoryBean");
		Connection c = bean.createQueueConnection();
        
		c.close();
		
		c = bean.createQueueConnection ( "" , "");
		c.close();
		
		assertNotNull ( Configuration.getResource ( bean.getResourceName() ) );
		
	}
	
	public void testResourcename()
	{
		bean.setResourceName ( "name" );
		assertEquals ( "name" , bean.getResourceName() );
	}
	
	public void testXaFactoryJndiName()
	{
		bean.setXaFactoryJndiName ( "name" );
		assertEquals ( bean.getXaFactoryJndiName() , "name" );
	}
	
	public void testXaQueueConnectionFactory()
	{
		TestQueueConnectionFactory f = new TestQueueConnectionFactory();
		bean.setXaQueueConnectionFactory(f);
		assertEquals ( f , bean.getXaQueueConnectionFactory() );
	}
	
	public void testReferencibility()
	throws Exception
	{
		bean.setResourceName ( "name" );
		Reference ref = bean.getReference();
		assertNotNull ( ref );
		Class clazz = Class.forName ( ref.getFactoryClassName() );
   		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
   		QueueConnectionFactoryBean instance = 
   			(QueueConnectionFactoryBean) fact.getObjectInstance ( ref , null , null , null );
   		assertNotNull ( instance );
   		assertEquals ( instance.getResourceName() , "name" );
	}

}
