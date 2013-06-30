package com.atomikos.util;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;


public class JmxRegistry {
	
	static Logger LOGGER = LoggerFactory.createLogger(JmxRegistry.class);

	private static MBeanServer mBeanServerInstance;
	
	/**
	 * Custom init for testing purposes mostly.
	 * 
	 * @param mBeanServer
	 */
	static void init(MBeanServer mBeanServer) {
		LOGGER.logWarning("Using custom MBeanServer: " + mBeanServer);
		mBeanServerInstance = mBeanServer;
	}

	private static MBeanServer getMBeanServer() {
		if (mBeanServerInstance == null) {
			mBeanServerInstance = ManagementFactory.getPlatformMBeanServer();
		}
		return mBeanServerInstance;
	}

	public static void register(String objectNameAsString, Object jmxBean) {
		MBeanServer server = getMBeanServer();
		try {
			ObjectName objectName = convertToObjectName(objectNameAsString);
			server.registerMBean(jmxBean, objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to register " + objectNameAsString , e);
		}
	}

	public static void unregister(String objectNameAsString) {
		MBeanServer server = getMBeanServer();
		try {
			ObjectName objectName = convertToObjectName(objectNameAsString);
			if (server.isRegistered(objectName)) {
				server.unregisterMBean(objectName);
			}
		} catch (Exception e) {
			LOGGER.logWarning("Failed to unregister " + objectNameAsString , e);
		}
	}

	private static ObjectName convertToObjectName(String objectNameAsString) throws MalformedObjectNameException, NullPointerException {
		return new ObjectName(objectNameAsString);
	}
	
}
