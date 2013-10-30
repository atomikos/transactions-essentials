package com.atomikos.icatch.config;

import static org.junit.Assert.*;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
}
