/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.ParticipantLinkFactory;

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
	

}
