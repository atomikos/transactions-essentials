package com.atomikos.util;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Common default logic for registering and unregistering JMX beans.
 *
 */

public abstract class DefaultMBeanRegistration implements MBeanRegistration {

	private static Logger LOGGER = LoggerFactory.createLogger(DefaultMBeanRegistration.class);
	
	private MBeanServer server;
	private ObjectName name;

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws Exception {
		 this.server = server;
	     if ( name == null ) {
	            name = createObjectName();
	     }
	     this.name = name;
	     return name;
	}

	protected abstract ObjectName createObjectName();
	
	protected MBeanServer getMBeanServer() {
		return server;
	}
	
	protected void unregister() {
		try {
			if ( server != null && server.isRegistered ( name ) ) {
				server.unregisterMBean ( name );
			}
		} catch (Exception e) {
			LOGGER.logWarning("Failed to unregister" + name, e);
		}
	}
	
	@Override
	public void postRegister(Boolean registrationDone) {}

	@Override
	public void preDeregister() throws Exception {}

	@Override
	public void postDeregister() {}

	protected ObjectName getObjectName() {
		return name;
	}
	
	protected void register(String objectNameAsString, Object jmxBean) {
		MBeanServer server = getMBeanServer();
		if ( server != null ) {
			try {
				ObjectName objectName = convertToObjectName(objectNameAsString);
				server.registerMBean(jmxBean, objectName);
			} catch (Exception e) {
				LOGGER.logWarning("Failed to register " + objectNameAsString , e);
			}
		}
	}

	protected void unregister(String objectNameAsString) {
		MBeanServer server = getMBeanServer();
		if (server != null) {
			try {
				ObjectName objectName = convertToObjectName(objectNameAsString);
				if (server.isRegistered(objectName)) {
					server.unregisterMBean(objectName);
				}
			} catch (Exception e) {
				LOGGER.logWarning("Failed to unregister " + objectNameAsString , e);
			}
		}
	}

	protected ObjectName convertToObjectName(String objectNameAsString) throws MalformedObjectNameException, NullPointerException {
		return new ObjectName(objectNameAsString);
	}

}
