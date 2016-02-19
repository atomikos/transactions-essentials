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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.ParticipantLinkFactory;

public class ParticipantLinkFactoryTestJUnit {
	
	private static final Date EXPIRES = new Date(1000l);
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
		assertEquals(EXPIRES, instance.getExpires().toGregorianCalendar().getTime());
	}
	

}