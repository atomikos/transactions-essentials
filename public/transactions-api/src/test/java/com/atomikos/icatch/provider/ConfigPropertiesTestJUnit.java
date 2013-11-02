package com.atomikos.icatch.provider;

import static org.junit.Assert.*;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.provider.ConfigProperties;

public class ConfigPropertiesTestJUnit {
	
	private static final String CUSTOM_PROPERTY_NAME = "bla";
	private ConfigProperties props;

	@Before
	public void setUp() throws Exception {
		props = new ConfigProperties(new Properties());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetPropertyThrowsIfNotSet() {
		props.getProperty("bla");
	}
	
	@Test
	public void testGetAsBoolean() {
		props.setProperty(CUSTOM_PROPERTY_NAME,"true");
		Assert.assertTrue(props.getAsBoolean(CUSTOM_PROPERTY_NAME));
	}

	@Test
	public void testGetAsInt() {
		props.setProperty(CUSTOM_PROPERTY_NAME, "10");
		Assert.assertEquals(10, props.getAsInt(CUSTOM_PROPERTY_NAME));
	}

	@Test
	public void testGetAsLong() {
		props.setProperty(CUSTOM_PROPERTY_NAME, "10");
		Assert.assertEquals(10, props.getAsLong(CUSTOM_PROPERTY_NAME));
	}
	
	@Test
	public void testGetPropertyTrimsSpaces() {
		props.setProperty(CUSTOM_PROPERTY_NAME, " bla ");
		assertEquals("bla", props.getProperty(CUSTOM_PROPERTY_NAME));
	}
	
	@Test
	public void testGetLogBaseDir() {
		props.setProperty("com.atomikos.icatch.log_base_dir", "bla");
		assertEquals("bla", props.getLogBaseDir());
	}
	
	@Test
	public void testGetLogBaseName() {
		props.setProperty("com.atomikos.icatch.log_base_name", "bla");
		assertEquals("bla", props.getLogBaseName());
	}
	
	@Test
	public void testGetEnableLogging() {
		props.setProperty("com.atomikos.icatch.enable_logging", "true");
		assertTrue(props.getEnableLogging());
	}
	
	@Test
	public void testMaxTimeout() {
		props.setProperty("com.atomikos.icatch.max_timeout", "30000");
		assertEquals(30000, props.getMaxTimeout());
	}
	
	@Test 
	public void testMaxActives() {
		props.setProperty("com.atomikos.icatch.max_actives", "100");
		assertEquals(100, props.getMaxActives());
	}
	
	@Test
	public void testThreaded2pc() {
		props.setProperty("com.atomikos.icatch.threaded_2pc", "true");
		assertTrue(props.getThreaded2pc());
	}
}

