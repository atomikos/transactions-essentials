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

package com.atomikos.datasource.xa.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import junit.framework.TestCase;

public class XAResourceConfigTestJUnit extends TestCase {

	private XAResourceConfig cfg;
	
	protected void setUp() throws Exception {
		super.setUp();
		cfg = new XAResourceConfig();
		
	}
	
	public void testAcceptsAllXAResources() {

		assertFalse ( cfg.acceptsAllXAResources() );
		cfg.setAcceptAllXAResources ( false );
		assertFalse ( cfg.acceptsAllXAResources() );
		cfg.setAcceptAllXAResources ( true );
		assertTrue ( cfg.acceptsAllXAResources() );
	}
	
	public void testName() throws MalformedObjectNameException {
		assertNull ( cfg.getName() );
		ObjectName name = new ObjectName ( "" );
		cfg.setName ( name );
		assertEquals ( name , cfg.getName() );
	}
	
	public void testUsesWeakCompare() {
		assertFalse ( cfg.usesWeakCompare() );
		cfg.setUseWeakCompare ( true );
		assertTrue ( cfg.usesWeakCompare() );
		cfg.setUseWeakCompare ( false );
		assertFalse ( cfg.usesWeakCompare() );
	}

}
