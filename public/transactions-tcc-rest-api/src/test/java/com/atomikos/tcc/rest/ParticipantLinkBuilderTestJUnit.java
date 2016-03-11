/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class ParticipantLinkBuilderTestJUnit {



	private static final String URI = "http://www.example.com/bla";
	private static final String FORMATTED_DATE = "2002-05-30T09:30:10Z";
	
	private ParticipantLinkBuilder builder;
	
	@Before
	public void setUp() throws ParseException {
		builder = ParticipantLinkBuilder.instance(URI, FORMATTED_DATE);
		
	}

	@Test(expected=IllegalStateException.class)
	public void testFailsWithoutDate() {
		builder = ParticipantLinkBuilder.instance(URI, null);
		builder.build();
	}
	
	@Test
	public void testWithExpiresAsXMLGregorianCalendar() {
		builder = ParticipantLinkBuilder.instance(URI, FORMATTED_DATE);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(FORMATTED_DATE, pl.getExpires());
	}
	
	@Test
	public void testWithExpiresAsDate() {
		builder = ParticipantLinkBuilder.instance(URI, FORMATTED_DATE);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(FORMATTED_DATE, pl.getExpires());
	}
	
	@Test
	public void testWithExpiresAsString() throws ParseException {
		builder = ParticipantLinkBuilder.instance(URI, FORMATTED_DATE);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(FORMATTED_DATE, pl.getExpires());
	}
	


}
