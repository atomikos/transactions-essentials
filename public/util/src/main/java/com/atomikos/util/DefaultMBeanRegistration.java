package com.atomikos.util;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Common default logic for registering and unregistering JMX beans.
 *
 */

public abstract class DefaultMBeanRegistration implements MBeanRegistration {

	private static Logger LOGGER = LoggerFactory.createLogger(DefaultMBeanRegistration.class);
	
	private MBeanServer mBeanServerFromPreRegister;
	private ObjectName name;

	private boolean usePlatformMBeanServer = true;

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws Exception {
		 this.mBeanServerFromPreRegister = server;
	     if ( name == null ) {
	            name = createObjectName();
	     }
	     this.name = name;
	     return name;
	}

	protected abstract ObjectName createObjectName() throws Exception;
	
	public void init() {
		doInit();
		if (usePlatformMBeanServer) {
			register();
		}
	}
	
	protected void register() {
		try {
			ObjectName objectName = createObjectName();
			registerWithExceptions(this, objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to register with MBeanServer" , e);
		}
	}

	protected abstract void doInit();
	
	protected MBeanServer getMBeanServer() {
		MBeanServer ret = null;
		if (usePlatformMBeanServer) {
			ret = ManagementFactory.getPlatformMBeanServer();
		} else {
			ret = mBeanServerFromPreRegister;
		}
		return ret;
	}
	
	protected void unregister() {
		try {
			unregisterWithExceptions(name);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to unregister" + name, e);
		}
	}

	private void unregisterWithExceptions(ObjectName objectName) throws MBeanRegistrationException,
			InstanceNotFoundException {
		if (isRegistered()) {
			MBeanServer server = getMBeanServer();
			server.unregisterMBean(objectName);
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
	
	/**
	 * Utility method for implementations that need to created and register additional MBeans at runtime.
	 * 
	 * @param objectNameAsString
	 * @param jmxBean
	 */
	protected void register(String objectNameAsString, Object jmxBean) {
		try {
			ObjectName objectName = convertToObjectName(objectNameAsString);
			registerWithExceptions(jmxBean, objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to register " + objectNameAsString , e);
		}
	}

	private void registerWithExceptions(Object jmxBean, ObjectName objectName)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException {
		MBeanServer server = getMBeanServer();
		if ( server != null ) {
			server.registerMBean(jmxBean, objectName);
			this.name = objectName;
		}
	}

	/**
	 * Utility method for implementations that register/unregister additional MBeans at runtime.
	 * @param objectNameAsString
	 */
	protected void unregister(String objectNameAsString) {	
		try {
			ObjectName objectName = convertToObjectName(objectNameAsString);
			unregisterWithExceptions(objectName);
		} catch (Exception e) {
			LOGGER.logWarning("Failed to unregister " + objectNameAsString , e);
		}
	}

	protected ObjectName convertToObjectName(String objectNameAsString) throws MalformedObjectNameException, NullPointerException {
		return new ObjectName(objectNameAsString);
	}

	/**
	 * Sets whether or not to use the JVM's built-in PlatformMBeanServer.
	 * Optional, defaults to true.
	 * 
	 * @param value If true, then init() will automatically register this bean
	 * with the JVM's built-in PlatformMBeanServer. If false, then you will have to 
	 * explicitly register this bean with some third-party MBeanServer.
	 */
	public void setUsePlatformMBeanServer(boolean value) {
		this.usePlatformMBeanServer = value;
	}

	public boolean getUsePlatformMBeanServer() {
		return usePlatformMBeanServer;
	}

	public boolean isRegistered() {
		boolean ret = false;
		MBeanServer server = getMBeanServer();
		ObjectName objectName = getObjectName();
		if (server != null && objectName != null) {
			ret = server.isRegistered(objectName);
		}
		return ret;
	}

}
