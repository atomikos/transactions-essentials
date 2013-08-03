package com.atomikos.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.Before;
import org.junit.Test;

public class DefaultMBeanRegistrationWithAutoRegistrationTestJUnit {
	
	
	private DefaultMBeanRegistration instance;

	@Before
	public void setUp() throws Exception {
		instance = new TestMBeanRegistration();
	}

	@Test
	public void testRegister() throws Exception {
		instance.register();
		assertTrue(instance.isRegistered());
	}
	
	@Test
	public void testNotRegisteredByDefault() throws Exception {
		assertFalse(instance.isRegistered());
	}
	
	@Test
	public void testUnregisterAfterRegistration() throws Exception {
		instance.register();
		instance.unregister();
		assertFalse(instance.isRegistered());
	}
	
	@Test
	public void testUnregisterWithoutRegistration() throws Exception {
		instance.unregister();
		assertFalse(instance.isRegistered());
	}
	
	@Test
	public void testAutoRegisterWithPlatformMBeanServerOnInitDefaultsToTrue() {
		assertTrue(instance.getAutoRegisterWithPlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
	}
	
	@Test
	public void testSetAutoRegisterWithPlatformMBeanServerOnInit() throws Exception {
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(true);
		assertTrue(instance.getAutoRegisterWithPlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(false);
		assertFalse(instance.getAutoRegisterWithPlatformMBeanServerOnInit());
		assertNull(instance.getMBeanServer());
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(true);
		assertTrue(instance.getAutoRegisterWithPlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
	}
	
	
	@Test
	public void testInitWithAutoRegisterWithPlatformMBeanServerOnInitTriggersRegistration() throws Exception {
		assertTrue(instance.getAutoRegisterWithPlatformMBeanServerOnInit());
		instance.init();
		assertEquals(TestMBeanRegistration.instanceSpecificObjectName, instance.getObjectName());
		assertTrue(instance.isRegistered());
	}
	

}
