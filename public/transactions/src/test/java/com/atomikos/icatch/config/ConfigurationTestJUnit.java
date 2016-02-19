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

	@Test
	public void testPlaceHolderSubstitution() {
		Assert.assertEquals("system.properties.override", props.getProperty("com.atomikos.icatch.jta.to.substitute"));
	}
	

}
