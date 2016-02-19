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

class Slf4jLogger implements Logger {

	private final org.slf4j.Logger slf4j;

	public Slf4jLogger(Class<?> clazz) {
		slf4j = org.slf4j.LoggerFactory.getLogger(clazz);
	}

	public void logWarning(String message) {
		slf4j.warn(message);
	}

	public void logInfo(String message) {
		slf4j.info(message);
	}

	public void logDebug(String message) {
		slf4j.debug(message);
	}

	public void logWarning(String message, Throwable error) {
		slf4j.warn(message,error);

	}

	public void logInfo(String message, Throwable error) {
		slf4j.info(message,error);
	}

	public void logDebug(String message, Throwable error) {
		slf4j.debug(message,error);

	}

	public boolean isDebugEnabled() {
		return slf4j.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return slf4j.isInfoEnabled();
	}
  
  public void logError(String message) {
    slf4j.error(message);
  }

  
  public void logError(String message, Throwable error) {
    slf4j.error(message, error);
  }

  
  public boolean isErrorEnabled() {
    return slf4j.isErrorEnabled();
  }

}
