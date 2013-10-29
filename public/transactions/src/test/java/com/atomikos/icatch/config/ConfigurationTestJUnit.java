package com.atomikos.icatch.config;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationTestJUnit {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindAssemblerInClasspath() {
		Assembler assembler = Configuration.instance().getAssembler();
		Assert.assertNotNull(assembler);
	}

}
