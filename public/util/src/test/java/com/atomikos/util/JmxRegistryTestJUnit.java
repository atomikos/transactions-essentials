package com.atomikos.util;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JmxRegistryTestJUnit {

	private MBeanServer mockedMBeanServer;
	private String jmxName;
	private Object jmxBean;
	
	@Before
	public void setUp() throws Exception {
		mockedMBeanServer = Mockito.mock(MBeanServer.class);
		JmxRegistry.init(mockedMBeanServer);
		jmxName = "atomikos:name=something";
		jmxBean = new Object();
	}

	@Test
	public void testRegister() throws Exception {
		JmxRegistry.register(jmxName, jmxBean);
		assertRegistered();
	}
	
	@Test
	public void testUnregister() throws Exception {
		JmxRegistry.unregister(jmxName);
		assertUnregistered();
	}


	private void assertUnregistered() throws Exception {
		Mockito.verify(mockedMBeanServer).unregisterMBean(new ObjectName(jmxName));
	}

	private void assertRegistered() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException {
		Mockito.verify(mockedMBeanServer).registerMBean(jmxBean, new ObjectName(jmxName));
	}

}
