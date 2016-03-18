/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ParticipantLinkFactoryTestJUnit {
	
	private static final String EXPIRES = "2002-05-30T09:30:10Z";
	private static final String URI = "http://www.example.com/tcc";
	
	private ParticipantLink instance;
	
	@Before
	public void setUp() {
		instance = ParticipantLinkFactory.createInstance(URI, EXPIRES);
	}

	@Test
	public void testUri() {
		assertEquals(URI, instance.getUri());
	}	
	
	@Test
	public void testExpires() {
		assertEquals(EXPIRES, instance.getExpires());
	}
	


	@Test(expected=IllegalStateException.class)
	public void testFailsWithoutDate() {
		instance = ParticipantLinkFactory.createInstance(URI, null);
		
	}
	
	@Test
	public void testWithExpiresAsXMLGregorianCalendar() {
		instance = ParticipantLinkFactory.createInstance(URI, EXPIRES);
		assertEquals(EXPIRES, instance.getExpires());
	}
	
	
}
