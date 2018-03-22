/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.config;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.provider.Assembler;
import com.atomikos.icatch.provider.ConfigProperties;

public class ConfigurationTestJUnit {

	private static final String CUSTOM_PROPERTY_NAME = "com.atomikos.icatch.bla";
	private static final String CUSTOM_PROPERTY_VALUE = "blabla";
	
	private ConfigProperties props;
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("com.atomikos.icatch.jta.to.override.by.system", "system.properties.override");
		System.setProperty("com.atomikos.icatch.custom.to.override.by.system", "system.properties.override");
		props = Configuration.getConfigProperties();
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
		Assert.assertNotNull(props.getProperty("com.atomikos.icatch.max_actives"));
	}
	
	@Test
	public void testSpecificPropertyFromPropertiesFileInClasspath() {
		Assert.assertNotNull(props.getProperty("com.atomikos.icatch.bla"));
	}
	
	@Test
	public void testJtaPropertiesFileInClasspathOverridesDefaults() {
		Assert.assertEquals("jta.properties.override", props.getProperty("com.atomikos.icatch.default.to.override.by.jta"));
	}
	
	@Test
	public void testTransactionsPropertiesFileInClasspathOverridesDefaults() {
		Assert.assertEquals("transactions.properties.override", props.getProperty("com.atomikos.icatch.default.to.override.by.transactions"));
	}
	
	@Test
	public void testJtaPropertiesFileOverridesTransactionsPropertiesFile() {
		Assert.assertEquals("jta.properties.override", props.getProperty("com.atomikos.icatch.transactions.to.override.by.jta"));
	}
	
	@Test
	public void testSystemPropertyOverridesJtaPropertiesFile() {
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.jta.to.override.by.system"));
	}
	
	@Test
	public void testCustomPropertiesFileOverridesJtaPropertiesFile() {
		Configuration.resetConfigProperties();
		String customFileNamePath ="custom.properties";
		System.setProperty("com.atomikos.icatch.file", customFileNamePath);
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals("custom.properties.override", props.getProperty("com.atomikos.icatch.jta.to.override.by.custom"));
	}

	@Test
	public void testSystemPropertyOverridesCustomPropertiesFile() {
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.custom.to.override.by.system"));
	}
	
	@Test
	public void testProgrammaticallySetPropertiesAreTakenIntoAccount() {
		setCustomProperty();
		ConfigProperties props = Configuration.getConfigProperties();
		Assert.assertEquals(CUSTOM_PROPERTY_VALUE, props.getProperty(CUSTOM_PROPERTY_NAME));
	}
	
	@Test
	public void testSystemPropertyOverridesProgrammaticallySetProperty() {
		setCustomProperty();
		final String systemOverride = overrideCustomPropertyAsSystemProperty();
		Assert.assertEquals(systemOverride, props.getProperty(CUSTOM_PROPERTY_NAME));
	}
	
	private String overrideCustomPropertyAsSystemProperty() {
		String ret = CUSTOM_PROPERTY_VALUE + "xxx";
		System.setProperty(CUSTOM_PROPERTY_NAME, ret);
		return ret;
	}

	private void setCustomProperty() {
		props.setProperty(CUSTOM_PROPERTY_NAME, CUSTOM_PROPERTY_VALUE);
	}
	@After
	public void cleanup(){
		System.clearProperty(CUSTOM_PROPERTY_NAME);
		System.clearProperty("com.atomikos.icatch.file");
	}

	@Test
	public void testPlaceHolderSubstitution() {
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.jta.to.substitute"));
	}
	

}
