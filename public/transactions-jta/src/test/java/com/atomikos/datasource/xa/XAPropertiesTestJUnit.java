package com.atomikos.datasource.xa;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class XAPropertiesTestJUnit {

	@Test
	public void testPrintPropertiesHandlesNullProperties() {
		XAProperties xaprops = new XAProperties( null );
		assertEquals( "", xaprops.printProperties() );
	}

	@Test
	public void testPrintPropertiesHandlesEmptyProperties() {
		XAProperties xaprops = new XAProperties();
		assertEquals( "[]", xaprops.printProperties() );
	}

	@Test
	public void testPrintPropertiesSkipsPassword() {
		Properties props = new Properties();
		props.setProperty( "user", "johndoe" );
		props.setProperty( "password", "secret" );
		props.setProperty( "Password", "secret" );
		props.setProperty( "PASSWORD", "secret" );
		props.setProperty( "custom", "value" );
		XAProperties xaprops = new XAProperties( props );
		assertEquals( "[user=johndoe,custom=value]", xaprops.printProperties() );
	}

}
