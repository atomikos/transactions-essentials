package com.atomikos.jms;

import javax.jms.Connection;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import junit.framework.TestCase;

import com.atomikos.icatch.system.Configuration;

public class TopicConnectionFactoryBeanTestJUnit extends TestCase 
{
	
	private  TopicConnectionFactoryBean  bean;
	
	public TopicConnectionFactoryBeanTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
		
		bean = new TopicConnectionFactoryBean();
		
	}
	
	public void testInit()
	throws Exception
	{
		TestTopicConnectionFactory f = new TestTopicConnectionFactory();
		bean.setXaTopicConnectionFactory(f);
		bean.setResourceName ( "Name" );
		bean.init();
		assertNotNull ( Configuration.getResource ( bean.getResourceName() ) );
	}
	
	public void testCreateConnection()
	throws Exception
	{
		TestTopicConnectionFactory f = new TestTopicConnectionFactory();
		bean.setXaTopicConnectionFactory(f);
		bean.setResourceName("TestTopicConnectionFactoryBean");
		Connection c = bean.createTopicConnection();
        
		c.close();
		
		c = bean.createTopicConnection ( "" , "");
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
	
	public void testXaTopicConnectionFactory()
	{
		TestTopicConnectionFactory f = new TestTopicConnectionFactory();
		bean.setXaTopicConnectionFactory(f);
		assertEquals ( f , bean.getXaTopicConnectionFactory() );
	}
	
	public void testReferencibility()
	throws Exception
	{
		bean.setResourceName ( "name" );
		Reference ref = bean.getReference();
		assertNotNull ( ref );
		Class clazz = Class.forName ( ref.getFactoryClassName() );
   		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
   		TopicConnectionFactoryBean instance = 
   			(TopicConnectionFactoryBean) fact.getObjectInstance ( ref , null , null , null );
   		assertNotNull ( instance );
   		assertEquals ( instance.getResourceName() , "name" );
	}

}
