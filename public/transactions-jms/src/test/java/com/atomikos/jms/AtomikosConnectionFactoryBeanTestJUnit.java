/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jms;

import java.util.Properties;

import javax.naming.Reference;

import junit.framework.TestCase;

import com.atomikos.util.IntraVmObjectFactory;

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
	
	public void testReapTimeout()
	{
		assertEquals ( 0 , bean.getReapTimeout() );
		int timeout = 200;
		bean.setReapTimeout ( timeout );
		assertEquals ( timeout , bean.getReapTimeout() );

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
}
