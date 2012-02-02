package com.atomikos.logging;

import junit.framework.TestCase;

public abstract class AbstractLoggerFactoryTest extends TestCase {

	protected static final String MESSAGE = "warning";
	
	protected static final Throwable ERROR = new Exception();
	
	protected Logger logger;
	

	public void testLogDebug() {
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug();
	}
	
	protected abstract void assertLoggedAsDebug();

	public void testLogDebugWithException() {
		logger.logDebug(MESSAGE,ERROR);
		assertLoggedAsDebugWithException();
	}
	
	protected abstract void assertLoggedAsDebugWithException();

	public void testLoggerCreated() {
		assertNotNull(logger);
	}

	public void testLogInfo() {
		logger.logInfo(MESSAGE);
		assertLoggedAsInfo();
	}

	protected abstract void assertLoggedAsInfo();

	public void testLogInfoWithException() {
		logger.logInfo(MESSAGE,ERROR);
		assertLoggedAsInfoWithException();
	}

	protected abstract void assertLoggedAsInfoWithException();

	public void testLogWarning() {
		logger.logWarning(MESSAGE);
		assertLoggedAsWarning();
	}

	protected abstract void assertLoggedAsWarning();

	public void testLogWarningWithException() {
		logger.logWarning(MESSAGE,ERROR);
		assertLoggedAsWarningWithException();
	}

	protected abstract void assertLoggedAsWarningWithException();
	
	public void testIsDebugEnabled() {
		assertFalse(logger.isDebugEnabled());
		configureLoggingFrameworkWithDebug();
		assertTrue(logger.isDebugEnabled());
	}
	
	public void testIsInfoEnabled() {
		assertFalse(logger.isInfoEnabled());
		configureLoggingFrameworkWithInfo();
		assertTrue(logger.isInfoEnabled());
	}

	protected abstract void configureLoggingFrameworkWithInfo();
	

	protected abstract void configureLoggingFrameworkWithDebug();
}