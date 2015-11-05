package com.atomikos.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.naming.Reference;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.util.IntraVmObjectFactory;

public class AtomikosConnectionFactoryBeanTestJUnit 
{

	private AtomikosConnectionFactoryBean bean;
	
	@Before
	public void setUp() throws Exception 
	{
		bean = new AtomikosConnectionFactoryBean();
	}
	
	@Test
	public void testMinPoolSize() 
	{
		assertEquals ( 1 , bean.getMinPoolSize() );
		bean.setMinPoolSize ( 2 );
		assertEquals ( 2 , bean.getMinPoolSize() );
	}
	
	@Test
	public void testMaxPoolSize()
	{
		assertEquals ( 1 , bean.getMaxPoolSize() );
		bean.setMaxPoolSize ( 3 );
		assertEquals ( 3 , bean.getMaxPoolSize() );
	}
	
	@Test
	public void testPoolSize() 
	{
		assertEquals ( 1 , bean.getMaxPoolSize() );
		assertEquals ( 1 , bean.getMinPoolSize() );
		bean.setPoolSize ( 4 );
		assertEquals ( 4 , bean.getMaxPoolSize() );
		assertEquals ( 4 , bean.getMinPoolSize() );

	}

	@Test
	public void testXaConnectionFactoryClassName() 
	{
		assertEquals ( null , bean.getXaConnectionFactoryClassName() );
		String name = "blabla";
		bean.setXaConnectionFactoryClassName ( name );
		assertEquals ( name , bean.getXaConnectionFactoryClassName() );
	}
	
	@Test
	public void testXaProperties()
	{
		assertTrue ( bean.getXaProperties().isEmpty() );
		Properties p = new Properties();
		String pname = "property";
		String pvalue = "value";
		p.setProperty ( pname , pvalue );
		bean.setXaProperties ( p );
		assertEquals ( p , bean.getXaProperties() );
		assertEquals ( pvalue , bean.getXaProperties().getProperty(pname));
		assertEquals ( p.size() , bean.getXaProperties().size() );
	}
	
	@Test
	public void testBorrowConnectionTimeout()
	{
		assertEquals ( 30 , bean.getBorrowConnectionTimeout() );
		int timeout = 45;
		bean.setBorrowConnectionTimeout ( timeout );
		assertEquals ( timeout , bean.getBorrowConnectionTimeout() );
	}
	
	@Test
	public void testMaintenanceInterval()
	{
		assertEquals ( 60 , bean.getMaintenanceInterval() );
		int interval = 90;
		bean.setMaintenanceInterval ( interval );
		assertEquals ( interval , bean.getMaintenanceInterval() );
	}
	
	@Test
	public void testMaxIdleTime() 
	{
		assertEquals ( 60 , bean.getMaxIdleTime() );
		int time = 99;
		bean.setMaxIdleTime ( time );
		assertEquals ( time , bean.getMaxIdleTime() );
	}
	
	@Test
	public void testReapTimeout()
	{
		assertEquals ( 0 , bean.getReapTimeout() );
		int timeout = 200;
		bean.setReapTimeout ( timeout );
		assertEquals ( timeout , bean.getReapTimeout() );
	}
	
	@Test
	public void testReferenceable() throws Exception 
	{
		bean.setUniqueResourceName ( "testReferenceable" );
		Reference ref = bean.getReference();
		assertNotNull ( ref );
		IntraVmObjectFactory f = new IntraVmObjectFactory();
		Object res = null;
		res = f.getObjectInstance ( ref , null , null , null );
		assertNotNull ( res );
		assertSame ( bean , res );
	}
	
	@Test
	public void testUniqueResourceName()
	{
		assertNull ( bean.getUniqueResourceName() );
		String name = "testname";
		bean.setUniqueResourceName ( name );
		assertEquals ( name , bean.getUniqueResourceName() );
	}
	
	@Test
	public void testLocalTransactionMode() 
	{
		assertFalse ( bean.getLocalTransactionMode() );
		bean.setLocalTransactionMode ( true );
		assertTrue ( bean.getLocalTransactionMode() );
		bean.setLocalTransactionMode ( false );
		assertFalse ( bean.getLocalTransactionMode() );
	}
	
	@Test
	public void testIgnoreSessionTransactedFlag()
	{
		assertTrue ( bean.getIgnoreSessionTransactedFlag() );
		bean.setIgnoreSessionTransactedFlag ( false );
		assertFalse ( bean.getIgnoreSessionTransactedFlag() );
		bean.setIgnoreSessionTransactedFlag( true );
		assertTrue ( bean.getIgnoreSessionTransactedFlag() );
	}

	@Test
	public void testMaxLifetime() 
	{
		assertEquals ( 0 , bean.getMaxLifetime() );
		int time = 99;
		bean.setMaxLifetime ( time );
		assertEquals ( time , bean.getMaxLifetime() );
	}
	
	@Test
	public void testPrintPropertiesSkipsPassword() throws Exception
	{
		Properties xaProperties = new Properties();
		xaProperties.setProperty ( "username", "johndoe" );
		xaProperties.setProperty ( "password", "secret" );
		bean.setXaProperties ( xaProperties );
		String s = bean.printProperties();
		assertTrue ( s.contains( "username=johndoe" ) );
		assertFalse ( s.contains( "password=secret" ) );
	}

}
