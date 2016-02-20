/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
