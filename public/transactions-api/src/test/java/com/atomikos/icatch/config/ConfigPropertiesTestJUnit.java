package com.atomikos.icatch.config;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ConfigPropertiesTestJUnit {
	
	private ConfigProperties props;

	@Before
	public void setUp() throws Exception {
		props = new ConfigProperties(new Properties());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetPropertyThrowsIfNotSet() {
		props.getProperty("bla");
	}

}
