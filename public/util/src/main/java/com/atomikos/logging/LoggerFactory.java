/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import java.util.function.Predicate;

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

	// cf case 229746: detection extracted from the static initializer into a pure,
	// testable method. SLF4J is recognised via each generation's own binding marker:
	// 1.7.x binds via `org.slf4j.impl.StaticLoggerBinder`; 2.0.x removed that (it
	// moved to the ServiceLoader SPI) and introduced `org.slf4j.spi.SLF4JServiceProvider`.
	// The old code only probed StaticLoggerBinder, so SLF4J 2.x went undetected and
	// logging silently fell through to log4j2/log4j/JUL. Probing both markers fixes
	// 2.x while leaving 1.x behaviour unchanged — SLF4JServiceProvider does not exist
	// in 1.x, so the 1.x path (including "api present without a binding" → fall
	// through) is exactly as before.
	static String chooseDelegateClassName(Predicate<String> classPresent) {
		if (classPresent.test("org.slf4j.impl.StaticLoggerBinder")          // SLF4J 1.7.x
				|| classPresent.test("org.slf4j.spi.SLF4JServiceProvider")) {  // SLF4J 2.0.x
			return "com.atomikos.logging.Slf4JLoggerFactoryDelegate";
		}
		if (classPresent.test("org.apache.logging.log4j.Logger")) {
			return "com.atomikos.logging.Log4j2LoggerFactoryDelegate";
		}
		if (classPresent.test("org.apache.log4j.Logger")) {
			return "com.atomikos.logging.Log4JLoggerFactoryDelegate";
		}
		return null;
	}

	// package-private (not private) so LoggerFactoryDetectionTestJUnit can
	// exercise the real Class.forName probe directly.
	static boolean isClassPresent(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (Throwable ex) {
			// absent, or present but failed to load — swallow silently and do
			// NOT print the stack trace. cf bug 118228 (printStackTrace on a
			// failed class probe spammed the console, e.g. under OSGi).
			return false;
		}
	}

	static {
		String cname = chooseDelegateClassName(LoggerFactory::isClassPresent);
		try {
			if (cname != null) {
				Class<?> loggerClass = (Class<?>) Class.forName(cname.trim(), true, LoggerFactory.class.getClassLoader());
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
