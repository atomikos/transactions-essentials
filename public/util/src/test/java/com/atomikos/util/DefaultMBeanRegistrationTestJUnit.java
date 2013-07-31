package com.atomikos.util;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultMBeanRegistrationTestJUnit {
	
	private static ObjectName instanceSpecificObjectName;
	
	private DefaultMBeanRegistration instance;
	private MBeanServer mockedMBeanServer;

	@Before
	public void setUp() throws Exception {
		instanceSpecificObjectName = new ObjectName("name:name=instance");
		instance = new TestMBeanRegistration();
		mockedMBeanServer = Mockito.mock(MBeanServer.class);
		Mockito.when(mockedMBeanServer.isRegistered(Mockito.any(ObjectName.class))).thenReturn(true);
	}

	@Test
	public void testPreRegisterWithSuppliedObjectName() throws Exception {
		ObjectName suppliedObjectName = new ObjectName("name:name=supplied");
		instance.preRegister(mockedMBeanServer, suppliedObjectName);
		assertEquals(suppliedObjectName, instance.getObjectName());
	}

	@Test
	public void testPreRegisterWithNullObjectName() throws Exception {
		instance.preRegister(mockedMBeanServer, null);
		assertEquals(instanceSpecificObjectName, instance.getObjectName());
	}
	
	@Test
	public void testPreRegisterRemembersSuppliedMBeanServer() throws Exception {
		instance.setUsePlatformMBeanServerOnInit(false);
		instance.preRegister(mockedMBeanServer, null);
		assertEquals(mockedMBeanServer, instance.getMBeanServer());
	}
	
	@Test
	public void testUnregisterAfterPreRegister() throws Exception {
		instance.setUsePlatformMBeanServerOnInit(false);
		instance.preRegister(mockedMBeanServer, instanceSpecificObjectName);
		instance.unregister();
		assertUnregistered();
	}
	
	@Test
	public void testUnregisterDoesNothingWithoutPreRegister() throws Exception {
		instance.unregister();
		assertNotUnregistered();
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
	public void testUnregister() throws Exception {
		instance.register();
		instance.unregister();
		assertFalse(instance.isRegistered());
	}
	
	@Test
	public void testUnregisterDoesNotThrowIfRegisterNotCalled() throws Exception {
		instance.unregister();
	}
	
	@Test
	public void testUnregisterDoesNothingIfRegistrationFailedAfterPreRegister() throws Exception {
		instance.preRegister(mockedMBeanServer, instanceSpecificObjectName);
		Mockito.when(mockedMBeanServer.isRegistered(instanceSpecificObjectName)).thenReturn(false);
		instance.unregister();
		assertNotUnregistered();
	}
	
	@Test
	public void testUsePlatformMBeanServerDefaultsToTrue() {
		assertTrue(instance.getUsePlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
	}
	
	@Test
	public void testSetUsePlatformMBeanServer() throws Exception {
		instance.setUsePlatformMBeanServerOnInit(true);
		assertTrue(instance.getUsePlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
		instance.setUsePlatformMBeanServerOnInit(false);
		assertFalse(instance.getUsePlatformMBeanServerOnInit());
		assertNull(instance.getMBeanServer());
		instance.setUsePlatformMBeanServerOnInit(true);
		assertTrue(instance.getUsePlatformMBeanServerOnInit());
		assertEquals(ManagementFactory.getPlatformMBeanServer(), instance.getMBeanServer());
	}
	
	
	@Test
	public void testInitWithUsePlatformMBeanServerTriggersRegistration() throws Exception {
		assertTrue(instance.getUsePlatformMBeanServerOnInit());
		instance.init();
		assertNotNull(instance.getObjectName());
		assertTrue(instance.isRegistered());
	}

	private void assertNotUnregistered() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer, Mockito.times(0)).unregisterMBean(Mockito.any(ObjectName.class));
	}

	private void assertUnregistered() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer).unregisterMBean(instanceSpecificObjectName);
	}
	
	private static interface TestMBeanRegistrationMBean {}
	private static class TestMBeanRegistration extends DefaultMBeanRegistration implements TestMBeanRegistrationMBean {
		@Override
		protected ObjectName createObjectName() {
			return instanceSpecificObjectName;
		}

		@Override
		protected void doInit() {
			
		}
	}
}
