/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ParticipantLink {

	protected String uri;
	protected XMLGregorianCalendar expires;

	public ParticipantLink() {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTimeInMillis(Long.MAX_VALUE);
		try {
			expires = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String value) {
		this.uri = value;
	}

	public XMLGregorianCalendar getExpires() {
		return expires;
	}

	public void setExpires(XMLGregorianCalendar value) {
		this.expires = value;
	}

}
