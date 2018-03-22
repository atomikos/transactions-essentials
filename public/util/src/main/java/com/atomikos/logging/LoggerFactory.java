/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

public final class LoggerFactory {

	private LoggerFactory() {

	}

	static LoggerFactoryDelegate loggerFactoryDelegate;

	public static final Logger createLogger(Class<?> clazz) {
		return loggerFactoryDelegate.createLogger(clazz);
	}

	static void setLoggerFactoryDelegate(LoggerFactoryDelegate loggerFactoryDelegate) {
		LoggerFactory.loggerFactoryDelegate = loggerFactoryDelegate;
	}

	static {
		String cname = null;
		//let's try with
		try {
			Class.forName("org.slf4j.impl.StaticLoggerBinder");
			cname = "com.atomikos.logging.Slf4JLoggerFactoryDelegate";
		} catch (Throwable ex) {
			System.out.println("No org.slf4j.impl.StaticLoggerBinder found in ClassPath, trying with log4j2...");
		}
		
		if(cname==null){
			try {
				Class.forName("org.apache.logging.log4j.Logger");
				cname = "com.atomikos.logging.Log4j2LoggerFactoryDelegate";
			} catch (Throwable ex) {
				//don't print stackTrace - cf bug 118228
				System.out.println("No org.apache.logging.log4j.Logger found found in ClassPath, trying with log4j...");
			}
		}

		if(cname==null){
			try {
				Class.forName("org.apache.log4j.Logger");
				cname = "com.atomikos.logging.Log4JLoggerFactoryDelegate";
			} catch (Throwable ex) {
				System.out.println("No org.apache.log4j.Logger found found in ClassPath, falling back default...");
			}
		}

		try {
			if (cname != null) {
				Class<?> loggerClass = (Class<?>) Class.forName(cname.trim(), true, Thread.currentThread().getContextClassLoader());
				loggerFactoryDelegate = (LoggerFactoryDelegate) loggerClass.newInstance();
			} else {
				fallbackToDefault();
			}
		} catch (Throwable ex) {
			// ignore - if we get here, some issue prevented the logger class
			// from being loaded.
			// maybe a ClassNotFound or NoClassDefFound or similar. Just use
			// j.u.l
			fallbackToDefault();
		}
		Logger logger = createLogger(LoggerFactory.class);
		logger.logDebug("Using " + loggerFactoryDelegate + " for logging.");
	}

	private static void fallbackToDefault() {
		setLoggerFactoryDelegate(new JULLoggerFactoryDelegate());
	}
}
