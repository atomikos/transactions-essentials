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
