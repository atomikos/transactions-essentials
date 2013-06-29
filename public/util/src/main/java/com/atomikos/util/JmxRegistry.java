package com.atomikos.util;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;


public class JmxRegistry {
	
	static Logger LOGGER = LoggerFactory.createLogger(JmxRegistry.class);

	private static MBeanServer mBeanServerInstance;
	
	static void init(MBeanServer mBeanServer) {
		mBeanServerInstance = mBeanServer;
	}

	private static MBeanServer getMBeanServer() {
		if (mBeanServerInstance == null) {
			init(ManagementFactory.getPlatformMBeanServer());
		}
		return mBeanServerInstance;
	}

	public static void register(ObjectName objectName, Object jmxBean) {
		MBeanServer server = getMBeanServer();
		try {
			server.registerMBean(jmxBean, objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to register " + objectName , e);
		}
	}

	public static void unregister(ObjectName objectName) {
		MBeanServer server = getMBeanServer();
		try {
			server.unregisterMBean(objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to unregister " + objectName , e);
		}
	}
	
}
