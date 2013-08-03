package com.atomikos.util;

import static org.junit.Assert.*;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultMBeanRegistrationWithoutAutoRegistrationTestJUnit {

	private DefaultMBeanRegistration instance;
	private MBeanServer mockedMBeanServer;
	
	@Before
	public void setUp() throws Exception {
		com.atomikos.util.TestMBeanRegistration.instanceSpecificObjectName = new ObjectName("name:name=instance");
		instance = new TestMBeanRegistration();
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(false);
		mockedMBeanServer = Mockito.mock(MBeanServer.class);
		Mockito.when(mockedMBeanServer.isRegistered(Mockito.any(ObjectName.class))).thenReturn(true);
	}

	@Test
	public void testPreRegisterRemembersSuppliedMBeanServer() throws Exception {
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(false);
		instance.preRegister(mockedMBeanServer, null);
		assertEquals(mockedMBeanServer, instance.getMBeanServer());
	}

	@Test
	public void testPreRegisterWithNullObjectName() throws Exception {
		instance.preRegister(mockedMBeanServer, null);
		assertEquals(com.atomikos.util.TestMBeanRegistration.instanceSpecificObjectName, instance.getObjectName());
	}

	@Test
	public void testPreRegisterWithSuppliedObjectName() throws Exception {
		ObjectName suppliedObjectName = new ObjectName("name:name=supplied");
		instance.preRegister(mockedMBeanServer, suppliedObjectName);
		assertEquals(suppliedObjectName, instance.getObjectName());
	}

	@Test
	public void testUnregisterDoesNothingIfRegistrationFailedAfterPreRegister() throws Exception {
		instance.preRegister(mockedMBeanServer, TestMBeanRegistration.instanceSpecificObjectName);
		Mockito.when(mockedMBeanServer.isRegistered(TestMBeanRegistration.instanceSpecificObjectName)).thenReturn(false);
		instance.unregister();
		assertNotUnregisteredWithMBeanServer();
	}

	@Test
	public void testUnregisterDoesNothingWithoutPreRegister() throws Exception {
		instance.unregister();
		assertNotUnregisteredWithMBeanServer();
	}

	@Test
	public void testInitDoesNotAutoRegister() throws Exception {
		instance.init();
		assertFalse(instance.isRegistered());
	}
	
	private void assertNotUnregisteredWithMBeanServer() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer, Mockito.times(0)).unregisterMBean(Mockito.any(ObjectName.class));
	}

	private void assertUnregisteredWithMBeanServer() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer).unregisterMBean(TestMBeanRegistration.instanceSpecificObjectName);
	}

	@Test
	public void testUnregisterAfterPreRegister() throws Exception {
		instance.setAutoRegisterWithPlatformMBeanServerOnInit(false);
		instance.preRegister(mockedMBeanServer, TestMBeanRegistration.instanceSpecificObjectName);
		instance.unregister();
		assertUnregisteredWithMBeanServer();
	}

}
