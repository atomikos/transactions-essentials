package com.atomikos.icatch.config;


import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTestJUnit {

	@Before
	public void setUp() throws Exception {
		System.setProperty("com.atomikos.icatch.jta.to.override.by.system", "system.properties.override");
	}
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty("com.atomikos.icatch.jta.to.override.by.system");
	}

	@Test
	public void testFindAssemblerInClasspath() {
		Assembler assembler = Configuration.getAssembler();
		Assert.assertNotNull(assembler);
	}
	
	@Test
	public void testDefaultValueForMaxActives() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertNotNull(props.getProperty("com.atomikos.icatch.max_actives"));
	}
	
	@Test
	public void testSpecificPropertyFromPropertiesFileInClasspath() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertNotNull(props.getProperty("com.atomikos.icatch.bla"));
	}
	
	@Test
	public void testJtaPropertiesFileInClasspathOverridesDefaults() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("jta.properties.override", props.getProperty("com.atomikos.icatch.default.to.override.by.jta"));
	}
	
	@Test
	public void testTransactionsPropertiesFileInClasspathOverridesDefaults() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("transactions.properties.override", props.getProperty("com.atomikos.icatch.default.to.override.by.transactions"));
	}
	
	@Test
	public void testJtaPropertiesFileOverridesTransactionsPropertiesFile() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("jta.properties.override", props.getProperty("com.atomikos.icatch.transactions.to.override.by.jta"));
	}
	
	@Test
	public void testSystemPropertyOverridesJtaPropertiesFile() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.jta.to.override.by.system"));
	}
	
	@Test
	public void testCustomPropertiesFileOverridesJtaPropertiesFile() {
		String customFileNamePath ="target/test-classes/custom.properties";
		System.setProperty("com.atomikos.icatch.file", customFileNamePath);
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("custom.properties.override", props.getProperty("com.atomikos.icatch.jta.to.override.by.custom"));
	}

	

}
