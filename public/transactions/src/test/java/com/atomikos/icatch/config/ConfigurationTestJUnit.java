package com.atomikos.icatch.config;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ConfigurationTestJUnit {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindAssemblerInClasspath() {
		Assembler assembler = Configuration.getAssembler();
		Assert.assertNotNull(assembler);
	}
	
	@Test
	@Ignore
	public void testAssemblerLoadsDefaultPropertiesFromClasspath() {
		Assert.assertNotNull(Configuration.getConfigProperties());
	}

}
