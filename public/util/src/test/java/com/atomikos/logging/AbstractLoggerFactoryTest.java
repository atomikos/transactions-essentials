/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import junit.framework.TestCase;

public abstract class AbstractLoggerFactoryTest extends TestCase {

	protected static final String MESSAGE = "warning";

	protected static final Throwable ERROR = new Exception();

	protected Logger logger;


	public void testLogDebug() {
		configureLoggingFrameworkWithDebug();
		logger.logTrace(MESSAGE);
		assertLoggedAsDebug();
	}

	protected abstract void assertLoggedAsDebug();

	public void testLogDebugWithException() {
		configureLoggingFrameworkWithDebug();
		logger.logTrace(MESSAGE,ERROR);
		assertLoggedAsDebugWithException();
	}

	protected abstract void assertLoggedAsDebugWithException();

	public void testLoggerCreated() {
		assertNotNull(logger);
	}

	public void testLogInfo() {
		logger.logDebug(MESSAGE);
		assertLoggedAsInfo();
	}

	protected abstract void assertLoggedAsInfo();

	public void testLogInfoWithException() {
		logger.logDebug(MESSAGE,ERROR);
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
	
  public void testLogError() {
    logger.logError(MESSAGE);
    assertLoggedAsError();
  }
  
  protected abstract void assertLoggedAsError();
  
  public void testLogErrorWithException() {
    logger.logError(MESSAGE,ERROR);
    assertLoggedAsErrorWithException();
  }
  
  protected abstract void assertLoggedAsErrorWithException();

	public void testIsDebugEnabled() {
		assertFalse(logger.isTraceEnabled());
		configureLoggingFrameworkWithDebug();
		assertTrue(logger.isTraceEnabled());
	}

	public void testIsInfoEnabled() {
		assertFalse(logger.isDebugEnabled());
		configureLoggingFrameworkWithInfo();
		assertTrue(logger.isDebugEnabled());
	}
	
	public void testIsErrorEnabled() {
	  configureLoggingFrameworkWithNone();
    assertFalse(logger.isErrorEnabled());
    configureLoggingFrameworkWithError();
    assertTrue(logger.isErrorEnabled());
  }
	
	protected abstract void configureLoggingFrameworkWithNone();
	
	protected abstract void configureLoggingFrameworkWithError();
	
	protected abstract void configureLoggingFrameworkWithInfo();

	protected abstract void configureLoggingFrameworkWithDebug();
}