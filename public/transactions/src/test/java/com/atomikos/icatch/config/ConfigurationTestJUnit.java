package com.atomikos.icatch.config;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTestJUnit {

	private static final String CUSTOM_PROPERTY_NAME = "bla";
	private static final String CUSTOM_PROPERTY_VALUE = "blabla";
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("com.atomikos.icatch.jta.to.override.by.system", "system.properties.override");
		System.setProperty("com.atomikos.icatch.custom.to.override.by.system", "system.properties.override");
	}
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty("com.atomikos.icatch.jta.to.override.by.system");
		System.clearProperty("com.atomikos.icatch.file");
		System.clearProperty("com.atomikos.icatch.custom.to.override.by.system");
		Configuration.resetConfigProperties();
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

	@Test
	public void testSystemPropertyOverridesCustomPropertiesFile() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.custom.to.override.by.system"));
	}
	
	@Test
	public void testProgramaticallySetPropertiesAreTakenIntoAccount() {
		setCustomProperty();
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals(CUSTOM_PROPERTY_VALUE, props.getProperty(CUSTOM_PROPERTY_NAME));
	}
	
	private void setCustomProperty() {
		ConfigProperties props = Configuration.getConfigProperties();
		props.setProperty(CUSTOM_PROPERTY_NAME, CUSTOM_PROPERTY_VALUE);
	}

	@Test
	public void testPlaceHolderSubstitution() {
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.jta.to.substitute"));
	}

}
