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

package com.atomikos.tcc.rest;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.ParticipantLinkBuilder;

public class ParticipantLinkBuilderTestJUnit {



	private static final String URI = "http://www.example.com/bla";
	private static final String FORMATTED_DATE = "02/01/2014";
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	
	private ParticipantLinkBuilder builder;
	private Date expiryAsDate;
	
	@Before
	public void setUp() throws ParseException {
		builder = ParticipantLinkBuilder.instance(URI);
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		expiryAsDate = formatter.parse(FORMATTED_DATE);
		builder.withExpires(expiryAsDate);
	}

	@Test(expected=IllegalStateException.class)
	public void testFailsWithoutDate() {
		builder.withExpires((XMLGregorianCalendar) null);
		builder.build();
	}
	
	@Test
	public void testWithExpiresAsXMLGregorianCalendar() {
		XMLGregorianCalendar cal = createXMLGregorianCalendar(expiryAsDate);
		builder.withExpires(cal);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(cal, pl.getExpires());
	}
	
	@Test
	public void testWithExpiresAsDate() {
		builder.withExpires(expiryAsDate);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(createXMLGregorianCalendar(expiryAsDate), pl.getExpires());
	}
	
	@Test
	public void testWithExpiresAsString() throws ParseException {
		builder.withExpires(DATE_FORMAT, FORMATTED_DATE);
		ParticipantLink pl = builder.build();
		assertNotNull(pl);
		assertEquals(expiryAsDate, pl.getExpires().toGregorianCalendar().getTime());
	}
	

	private XMLGregorianCalendar createXMLGregorianCalendar(Date date) {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(date);
		XMLGregorianCalendar cal;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return cal;
	}

}
