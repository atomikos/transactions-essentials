/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.DeliveryMode;
import javax.jms.Destination;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.TestQueue;

import junit.framework.TestCase;

public class SingleThreadedJmsSenderTemplateTestJUnit extends TestCase {

	private SingleThreadedJmsSenderTemplate template;
	
	protected void setUp() throws Exception {
		super.setUp();
		template = new SingleThreadedJmsSenderTemplate();
	}
	
	public void testUser()
	{
		assertNull ( template.getUser() );
		String user = "user";
		template.setUser ( user );
		assertEquals ( user , template.getUser() );
	}
	
	public void testDestination()
	{
		assertNull ( template.getDestination() );
		Destination destination = new TestQueue();
		template.setDestination(destination);
		assertEquals ( destination, template.getDestination() );	
	}
	
	public void testReplyToDestination()
	{
		assertNull ( template.getReplyToDestination() );
		Destination destination = new TestQueue();
		template.setReplyToDestination(destination);
		assertEquals ( destination, template.getReplyToDestination() );
	}
	
	public void testDeliveryMode()
	{
		assertEquals ( DeliveryMode.PERSISTENT , template.getDeliveryMode() );
		template.setDeliveryMode ( DeliveryMode.NON_PERSISTENT );
		assertEquals ( DeliveryMode.NON_PERSISTENT , template.getDeliveryMode() );
	}
	
	public void testPriority()
	{
		assertEquals ( 4 , template.getPriority() );
		template.setPriority(5);
		assertEquals ( 5 , template.getPriority() );
	}
	
	public void testTimeToLive()
	{
		assertEquals ( 0 , template.getTimeToLive() );
		template.setTimeToLive(3);
		assertEquals ( 3 , template.getTimeToLive() );
	}
	
	public void testInitWithoutDestinationThrowsMeaningfulException() throws Exception 
	{
		template.setAtomikosConnectionFactoryBean(new AtomikosConnectionFactoryBean() );
		try {
			template.init();
		} catch ( IllegalStateException ok ) {
			assertEquals ( "Property 'destination' or 'destinationName' must be set first!" , ok.getMessage() );
		}
	}
	
	public void testInitWithoutConnectionFactoryThrowsMeaningfulException() throws Exception 
	{
		template.setDestination ( new TestQueue()  );
		try {
			template.init();
		} catch ( IllegalStateException ok ) {
			assertEquals ( "Property 'atomikosConnectionFactoryBean' must be set first!" , ok.getMessage() );
		}
	}

}
