/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
