/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

public class FormatIso8601DateFormatTestJUnit {
	String dateFromJochen = "2002-05-30T09:30:10Z";
	String dateFromJochenNonUTC = "2002-05-30T10:30:10+01:00";
	long dateFromJochenInMillis = 1022751010000l;
	@Test
	public void testParseUTCDate() throws Exception {
		//from Str to Date/Calendar/timestamp 
		Calendar cal = DatatypeConverter.parseDateTime(dateFromJochen);
		
		assertNotNull(cal);
		assertEquals(2002, cal.get(Calendar.YEAR));
		assertEquals(4, cal.get(Calendar.MONTH));
		assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(9, cal.get(Calendar.HOUR));
		assertEquals(30, cal.get(Calendar.MINUTE));
		assertEquals(10, cal.get(Calendar.SECOND));
		assertEquals(TimeZone.getTimeZone("GMT+00:00"), cal.getTimeZone());
		assertEquals(dateFromJochenInMillis,cal.getTimeInMillis());
	}


	@Test
	public void testParseWithTimeZone() {

		Calendar cal = DatatypeConverter.parseDateTime(dateFromJochenNonUTC);
		
		assertNotNull(cal);
		assertEquals(2002, cal.get(Calendar.YEAR));
		assertEquals(4, cal.get(Calendar.MONTH));
		assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
		//!!! cf. GMT+01/00
		assertEquals(10, cal.get(Calendar.HOUR));
		assertEquals(30, cal.get(Calendar.MINUTE));
		assertEquals(10, cal.get(Calendar.SECOND));
		//!!! cf. GMT+01/00
		assertEquals(TimeZone.getTimeZone("GMT+01:00"), cal.getTimeZone());
		assertEquals(dateFromJochenInMillis,cal.getTimeInMillis());
		

	}
	
//	@Test
//	public void testFormatDateInUtc() throws Exception {
//		String formatted = DateUtil.toDate(dateFromJochenInMillis);
//		Assert.assertEquals(dateFromJochen, formatted);
//	}


}
