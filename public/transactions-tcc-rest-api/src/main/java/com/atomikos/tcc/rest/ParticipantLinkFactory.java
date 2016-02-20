/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

 /**
  * Customized factory to make it easy to generate ParticipantLink instances.
  * 
  */

public class ParticipantLinkFactory {


	public static ParticipantLink createInstance(String uri, Date expires) {
		ParticipantLink pl = new ParticipantLink();
		pl.setUri(uri);
		XMLGregorianCalendar date = convertToXMLGregorianCalendar(expires);
		pl.setExpires(date);
		return pl;
	}

	private static XMLGregorianCalendar convertToXMLGregorianCalendar(
			Date expires) {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(expires);
		XMLGregorianCalendar date;
		try {
			date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return date;
	}

}
