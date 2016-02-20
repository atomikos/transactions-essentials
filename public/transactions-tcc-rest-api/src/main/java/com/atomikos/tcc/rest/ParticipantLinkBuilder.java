/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ParticipantLinkBuilder {

	public static ParticipantLinkBuilder instance(String uri) {
		return new ParticipantLinkBuilder(uri);
	}

	private ParticipantLink pl;
	
	private ParticipantLinkBuilder(String uri) {
		this.pl = new ParticipantLink();
		this.pl.setUri(uri);
	}
	
	public ParticipantLinkBuilder withExpires(XMLGregorianCalendar date) {
		this.pl.setExpires(date);
		return this;
	}
	
	public ParticipantLink build() {
		assertNotNull(pl.getExpires(), "Required: expiry");
		return this.pl;
	}

	private void assertNotNull(Object o, String msg) {
		if (o == null) throw new IllegalStateException(msg);
	}

	public ParticipantLinkBuilder withExpires(Date date) {
		XMLGregorianCalendar cal = createXMLGregorianCalendar(date);
		return withExpires(cal);
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

	public ParticipantLinkBuilder withExpires(String dateFormat, String formattedDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Date date = formatter.parse(formattedDate);
		return withExpires(date);
	}


}
