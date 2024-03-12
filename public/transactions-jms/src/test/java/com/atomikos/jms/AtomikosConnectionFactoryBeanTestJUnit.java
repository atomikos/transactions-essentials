/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import java.util.Properties;

import javax.naming.Reference;

import com.atomikos.icatch.OrderedLifecycleComponent;
import com.atomikos.util.IntraVmObjectFactory;

import junit.framework.TestCase;

public class AtomikosConnectionFactoryBeanTestJUnit extends TestCase 
{

	private AtomikosConnectionFactoryBean bean;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		bean = new AtomikosConnectionFactoryBean();
	}
	
	public void testMinPoolSize() 
	{
		assertEquals ( 1 , bean.getMinPoolSize() );
		bean.setMinPoolSize ( 2 );
		assertEquals ( 2 , bean.getMinPoolSize() );
	}
	
	public void testMaxPoolSize()
	{
		assertEquals ( 1 , bean.getMaxPoolSize() );
		bean.setMaxPoolSize ( 3 );
		assertEquals ( 3 , bean.getMaxPoolSize() );
	}
	
	public void testPoolSize() 
	{
		assertEquals ( 1 , bean.getMaxPoolSize() );
		assertEquals ( 1 , bean.getMinPoolSize() );
		bean.setPoolSize ( 4 );
		assertEquals ( 4 , bean.getMaxPoolSize() );
		assertEquals ( 4 , bean.getMinPoolSize() );

	}

	public void testXaConnectionFactoryClassName() 
	{
		assertEquals ( null , bean.getXaConnectionFactoryClassName() );
		String name = "blabla";
		bean.setXaConnectionFactoryClassName ( name );
		assertEquals ( name , bean.getXaConnectionFactoryClassName() );
	}
	
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
	
	public void testBorrowConnectionTimeout()
	{
		assertEquals ( 30 , bean.getBorrowConnectionTimeout() );
		int timeout = 45;
		bean.setBorrowConnectionTimeout ( timeout );
		assertEquals ( 45 , bean.getBorrowConnectionTimeout() );
	}
	
	public void testMaintenanceInterval()
	{
		assertEquals ( 60 , bean.getMaintenanceInterval() );
		int interval = 90;
		bean.setMaintenanceInterval ( interval );
		assertEquals ( interval , bean.getMaintenanceInterval() );
	}
	
	public void testMaxIdleTime() 
	{
		assertEquals ( 60 , bean.getMaxIdleTime() );
		int time = 99;
		bean.setMaxIdleTime ( time );
		assertEquals ( time , bean.getMaxIdleTime() );
	}
	
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
	
	public void testUniqueResourceName()
	{
		assertNull ( bean.getUniqueResourceName() );
		String name = "testname";
		bean.setUniqueResourceName ( name );
		assertEquals ( name , bean.getUniqueResourceName() );
	}
	
	public void testLocalTransactionMode() 
	{
		assertFalse ( bean.getLocalTransactionMode() );
		bean.setLocalTransactionMode ( true );
		assertTrue ( bean.getLocalTransactionMode() );
		bean.setLocalTransactionMode ( false );
		assertFalse ( bean.getLocalTransactionMode() );
	}
	
	public void testIgnoreSessionTransactedFlag() {
		assertTrue(bean.getIgnoreSessionTransactedFlag());
		bean.setIgnoreSessionTransactedFlag(false);
		assertFalse(bean.getIgnoreSessionTransactedFlag());
		bean.setIgnoreSessionTransactedFlag(true);
		assertTrue(bean.getIgnoreSessionTransactedFlag());

	}
	
	public void testImplementsOrderedLifecycleComponent() {
		assertTrue(bean instanceof OrderedLifecycleComponent);
	}
	
	public void testSessionCreationModeDefaultsToJMS_2_0() {
		assertEquals(SessionCreationMode.JMS_2_0, bean.getSessionCreationMode());
	}
	
	public void testSetSessionCreationMode() {
		bean.setSessionCreationMode(SessionCreationMode.PRE_6_0);
		assertEquals(SessionCreationMode.PRE_6_0, bean.getSessionCreationMode());
	}
	
	public void testIgnoreSessionFlagFalseMeansSessionCreationModePre3_9() {
		bean.setIgnoreSessionTransactedFlag(false);
		assertEquals(SessionCreationMode.PRE_3_9, bean.getSessionCreationMode());
	}
	
	public void testSetConnectionCreationModeAsInt() {
		bean.setSessionCreationMode(0);
	}
}
