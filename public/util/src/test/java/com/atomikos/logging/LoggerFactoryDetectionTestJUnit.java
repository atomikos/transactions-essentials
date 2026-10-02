/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import junit.framework.TestCase;

// cf case 229746 — LoggerFactory recognises SLF4J via each generation's binding
// marker: 1.7.x = org.slf4j.impl.StaticLoggerBinder, 2.0.x = org.slf4j.spi.
// SLF4JServiceProvider (StaticLoggerBinder was removed in 2.x). The old code only
// probed StaticLoggerBinder, so SLF4J 2.x went undetected. These tests drive the
// pure detection method with a simulated classpath.
public class LoggerFactoryDetectionTestJUnit extends TestCase {

	private static final String SLF4J = "com.atomikos.logging.Slf4JLoggerFactoryDelegate";
	private static final String LOG4J2 = "com.atomikos.logging.Log4j2LoggerFactoryDelegate";
	private static final String LOG4J = "com.atomikos.logging.Log4JLoggerFactoryDelegate";

	private static final String SLF4J_1X_MARKER = "org.slf4j.impl.StaticLoggerBinder";
	private static final String SLF4J_2X_MARKER = "org.slf4j.spi.SLF4JServiceProvider";

	private static Predicate<String> classpath(String... present) {
		Set<String> onPath = new HashSet<String>();
		for (String c : present) {
			onPath.add(c);
		}
		return onPath::contains;
	}

	// SLF4J 2.x (the bug): StaticLoggerBinder is gone; detect via SLF4JServiceProvider.
	public void testSlf4j2xDetectedViaServiceProvider() {
		assertEquals(SLF4J, LoggerFactory.chooseDelegateClassName(classpath(SLF4J_2X_MARKER)));
	}

	// SLF4J 1.7.x: detected via StaticLoggerBinder (unchanged from before).
	public void testSlf4j1xDetectedViaStaticLoggerBinder() {
		assertEquals(SLF4J, LoggerFactory.chooseDelegateClassName(classpath(SLF4J_1X_MARKER)));
	}

	// Old behaviour preserved: slf4j-api present but NO binding (only the API entry
	// class org.slf4j.LoggerFactory, neither generation's binding marker) must NOT
	// select SLF4J — it falls through, exactly as before the fix.
	public void testSlf4jApiWithoutBindingFallsThrough() {
		assertNull(LoggerFactory.chooseDelegateClassName(classpath("org.slf4j.LoggerFactory")));
	}

	// No SLF4J, log4j2 present -> log4j2.
	public void testLog4j2WhenNoSlf4j() {
		assertEquals(LOG4J2, LoggerFactory.chooseDelegateClassName(classpath("org.apache.logging.log4j.Logger")));
	}

	// No SLF4J, no log4j2, log4j present -> log4j.
	public void testLog4jWhenNoSlf4jNorLog4j2() {
		assertEquals(LOG4J, LoggerFactory.chooseDelegateClassName(classpath("org.apache.log4j.Logger")));
	}

	// Nothing on the classpath -> null (caller falls back to JUL).
	public void testNullWhenNoKnownFrameworkPresent() {
		assertNull(LoggerFactory.chooseDelegateClassName(classpath()));
	}

	// SLF4J (2.x) wins over log4j2 when both are present (detection order preserved).
	public void testSlf4jPreferredOverLog4j2() {
		assertEquals(SLF4J, LoggerFactory.chooseDelegateClassName(
				classpath(SLF4J_2X_MARKER, "org.apache.logging.log4j.Logger")));
	}

	// --- cf bug 118228: a failed class probe must be swallowed silently ---
	// (These drive the actual Class.forName path in isClassPresent, unlike the
	// chooseDelegateClassName tests above which inject a simulated classpath.)

	private static final String ABSENT_CLASS = "com.atomikos.logging.NoSuchClass$$absent$$";

	// A class that is not on the classpath -> false, and NO exception escapes
	// (the catch (Throwable) swallows the ClassNotFoundException).
	public void testAbsentClassReturnsFalseWithoutThrowing() {
		assertFalse(LoggerFactory.isClassPresent(ABSENT_CLASS));
	}

	// A class that is certainly present -> true.
	public void testPresentClassReturnsTrue() {
		assertTrue(LoggerFactory.isClassPresent("java.lang.String"));
	}

	// The literal bug 118228 guarantee: probing an absent class must not print
	// the stack trace (or anything) to System.out / System.err.
	public void testAbsentClassProbePrintsNothing() {
		PrintStream origOut = System.out;
		PrintStream origErr = System.err;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(err));
			LoggerFactory.isClassPresent(ABSENT_CLASS);
		} finally {
			System.setOut(origOut);
			System.setErr(origErr);
		}
		assertEquals("nothing should be written to System.out on a failed probe", "", out.toString());
		assertEquals("nothing should be written to System.err on a failed probe", "", err.toString());
	}
}
