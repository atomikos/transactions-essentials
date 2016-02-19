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

import junit.framework.TestCase;

public abstract class AbstractLoggerFactoryTest extends TestCase {

	protected static final String MESSAGE = "warning";

	protected static final Throwable ERROR = new Exception();

	protected Logger logger;


	public void testLogDebug() {
		configureLoggingFrameworkWithDebug();
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug();
	}

	protected abstract void assertLoggedAsDebug();

	public void testLogDebugWithException() {
		configureLoggingFrameworkWithDebug();
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