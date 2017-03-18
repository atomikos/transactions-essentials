/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import junit.framework.TestCase;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractLoggerFactoryTest extends TestCase {

	protected static final String MESSAGE = "warning";

	protected static final Throwable ERROR = new Exception();

	protected Logger logger;

//  @Ignore
//	public void testLogTrace() {
//		configureLoggingFrameworkWithTrace();
//		logger.logTrace(MESSAGE);
//		assertLoggedAsTrace();
//	}

	protected abstract void configureLoggingFrameworkWithTrace() ;

	protected abstract void assertLoggedAsTrace();
	
	public void testLogDebug() {
		configureLoggingFrameworkWithDebug();
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug();
	}

	protected abstract void assertLoggedAsDebug();

//  @Ignore
//	public void testLogTraceWithException() {
//		configureLoggingFrameworkWithTrace();
//		logger.logTrace(MESSAGE,ERROR);
//		assertLoggedAsTraceWithException();
//	}
	
	protected abstract void assertLoggedAsTraceWithException() ;

//	public void testLogDebugWithException() {
//		configureLoggingFrameworkWithDebug();
//		logger.logDebug(MESSAGE,ERROR);
//		assertLoggedAsDebugWithException();
//	}

	protected abstract void assertLoggedAsDebugWithException();

	public void testLoggerCreated() {
		assertNotNull(logger);
	}

//	public void testLogInfo() {
//		logger.logInfo(MESSAGE);
//		assertLoggedAsInfo();
//	}

	protected abstract void assertLoggedAsInfo();

//	public void testLogInfoWithException() {
//		logger.logInfo(MESSAGE,ERROR);
//		assertLoggedAsInfoWithException();
//	}

	protected abstract void assertLoggedAsInfoWithException();

//  @Ignore
//	public void testLogWarning() {
//		logger.logWarning(MESSAGE);
//		assertLoggedAsWarning();
//	}

	protected abstract void assertLoggedAsWarning();

//	public void testLogWarningWithException() {
//		logger.logWarning(MESSAGE,ERROR);
//		assertLoggedAsWarningWithException();
//	}

	protected abstract void assertLoggedAsWarningWithException();

//  @Ignore
//  public void testLogError() {
//    logger.logError(MESSAGE);
//    assertLoggedAsError();
//  }
  
  protected abstract void assertLoggedAsError();
  
//  public void testLogErrorWithException() {
//    logger.logError(MESSAGE,ERROR);
//    assertLoggedAsErrorWithException();
//  }
  
  protected abstract void assertLoggedAsErrorWithException();

  public void testIsTraceEnabled() {
		assertFalse(logger.isTraceEnabled());
		configureLoggingFrameworkWithTrace();
		assertTrue(logger.isTraceEnabled());
	}
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