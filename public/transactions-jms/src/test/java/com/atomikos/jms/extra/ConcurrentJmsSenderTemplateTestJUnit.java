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

package com.atomikos.jms.extra;

import javax.jms.DeliveryMode;
import javax.jms.Destination;

import junit.framework.TestCase;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.TestQueue;

public class ConcurrentJmsSenderTemplateTestJUnit extends TestCase {

	private ConcurrentJmsSenderTemplate template;
	
	protected void setUp() throws Exception {
		super.setUp();
		template = new ConcurrentJmsSenderTemplate();
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
	
	public void testDestinationName()
	{
		assertNull ( template.getDestinationName() );
		String name = "name";
		template.setDestinationName ( name );
		assertEquals ( name , template.getDestinationName() );
	}
	
	public void testReplyToDestinationName()
	{
		assertNull ( template.getReplyToDestinationName() );
		String name = "name";
		template.setReplyToDestinationName ( name );
		assertEquals ( name , template.getReplyToDestinationName() );
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
