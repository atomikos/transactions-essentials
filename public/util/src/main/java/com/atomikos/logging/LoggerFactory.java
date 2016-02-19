/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
			System.out.println("No org.slf4j.impl.StaticLoggerBinder found in ClassPath, trying with log4j...");
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
		logger.logInfo("Using " + loggerFactoryDelegate + " for logging.");
	}

	private static void fallbackToDefault() {
		setLoggerFactoryDelegate(new JULLoggerFactoryDelegate());
	}
}
