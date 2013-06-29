package com.atomikos.util;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JmxRegistryTestJUnit {

	private MBeanServer mockedMBeanServer;
	private ObjectName objectName;
	private Object jmxBean;
	
	@Before
	public void setUp() throws Exception {
		mockedMBeanServer = Mockito.mock(MBeanServer.class);
		JmxRegistry.init(mockedMBeanServer);
		objectName = new ObjectName("test","test","test");
		jmxBean = new Object();
	}

	@Test
	public void testRegister() throws Exception {
		JmxRegistry.register(objectName,jmxBean);
		assertRegistered();
	}
	
	@Test
	public void testUnregister() throws Exception {
		JmxRegistry.unregister(objectName);
		assertUnregistered();
	}


	private void assertUnregistered() throws Exception {
		Mockito.verify(mockedMBeanServer).unregisterMBean(objectName);
	}

	private void assertRegistered() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		Mockito.verify(mockedMBeanServer).registerMBean(jmxBean, objectName);
	}

}
