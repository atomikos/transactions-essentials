package com.atomikos.logging;

import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

import junit.framework.TestCase;

public class LoggerFactoryTestJUnit extends TestCase {
	
	private static final String MESSAGE = "warning";
	
	private static final Throwable ERROR = new Exception();
	
	private Logger logger;
	
	
	public void setUp() {
		logger = LoggerFactory.createLogger(getClass());	
	}
	
	public void testLoggerCreated() {
		assertNotNull(logger);
	}
	
	public void testAssertSlf4jLoggerCreated() {
		assertTrue(logger instanceof Slf4jLogger);
	}

	public void testLogWarning() {
		logger.logWarning(MESSAGE);
		assertLoggedAsWarning(MESSAGE);
	}

	private void assertLoggedAsWarning(String warning) {
		Mockito.verify(StaticLoggerBinder.mockito).warn(MESSAGE);
	}
	
	public void testLogInfo() {
		logger.logInfo(MESSAGE);
		assertLoggedAsInfo(MESSAGE);
	}

	public void testLogDebug() {
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug(MESSAGE);
	}
	
	public void testLogWarningWithException() {
		logger.logWarning(MESSAGE,ERROR);
		assertLoggedAsWarningWithException();
	}
	public void testLogInfoWithException() {
		logger.logInfo(MESSAGE,ERROR);
		assertLoggedAsInfoWithException();
	}
	
	public void testLogDebugWithException() {
		logger.logDebug(MESSAGE,ERROR);
		assertLoggedAsDebugWithException();
	}
	
	private void assertLoggedAsDebugWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).debug(MESSAGE,ERROR);
		
		
	}

	private void assertLoggedAsInfoWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).info(MESSAGE,ERROR);
	
		
	}

	private void assertLoggedAsWarningWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).warn(MESSAGE,ERROR);
	}

	private void assertLoggedAsDebug(String message) {
		Mockito.verify(StaticLoggerBinder.mockito).debug(MESSAGE);
	}

	private void assertLoggedAsInfo(String message) {
		Mockito.verify(StaticLoggerBinder.mockito).info(MESSAGE);
	}

}
