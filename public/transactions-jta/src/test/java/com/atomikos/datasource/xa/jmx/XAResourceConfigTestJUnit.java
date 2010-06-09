package com.atomikos.datasource.xa.jmx;

import junit.framework.TestCase;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

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
