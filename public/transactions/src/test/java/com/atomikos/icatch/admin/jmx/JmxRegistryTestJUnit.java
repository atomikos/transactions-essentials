package com.atomikos.icatch.admin.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import junit.framework.TestCase;

public class JmxRegistryTestJUnit extends TestCase {

	private JmxRegistry registry;
	
	protected void setUp() throws Exception {
		super.setUp();
		registry = new JmxRegistry();
	}
	
	public void test() throws Exception
	{
		ObjectName name = new ObjectName("atomikos.transactions", "TID","Test");
		TestMBeanServer server = new TestMBeanServer();
		registry.preRegister ( server , name );
		registry.postRegister ( null );
		registry.preDeregister();
		registry.postDeregister();
	}

}
