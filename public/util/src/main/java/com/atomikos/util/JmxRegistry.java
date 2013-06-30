package com.atomikos.util;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
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
		return mBeanServerInstance;
	}

	public static void register(String objectNameAsString, Object jmxBean) {
		MBeanServer server = getMBeanServer();
		if (server != null) {
			try {
				ObjectName objectName = convertToObjectName(objectNameAsString);
				server.registerMBean(jmxBean, objectName);
			} catch (Exception e) {
				LOGGER.logWarning("Failed to register " + objectNameAsString , e);
			}
		}
	}

	public static void unregister(String objectNameAsString) {
		MBeanServer server = getMBeanServer();
		if (server != null) {
			try {
				ObjectName objectName = convertToObjectName(objectNameAsString);
				server.unregisterMBean(objectName);
			} catch (Exception e) {
				LOGGER.logWarning("Failed to unregister " + objectNameAsString , e);
			}
		}
	}

	private static ObjectName convertToObjectName(String objectNameAsString) throws MalformedObjectNameException, NullPointerException {
		return new ObjectName(objectNameAsString);
	}
	
}
