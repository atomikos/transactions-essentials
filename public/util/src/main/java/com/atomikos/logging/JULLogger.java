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

import java.util.logging.Level;


class JULLogger implements Logger {

	private final java.util.logging.Logger julLogger;

	public JULLogger(Class<?> clazz) {
		julLogger=java.util.logging.Logger.getLogger(clazz.getName());

	}

	public void logWarning(String message) {
		julLogger.log(Level.WARNING,message);
	}

	public void logInfo(String message) {
		julLogger.log(Level.INFO,message);

	}

	public void logDebug(String message) {
		julLogger.log(Level.FINE,message);
	}

	public void logWarning(String message, Throwable error) {
		julLogger.log(Level.WARNING, message, error);
		}

	public void logInfo(String message, Throwable error) {
		julLogger.log(Level.INFO, message, error);

	}

	public void logDebug(String message, Throwable error) {
		julLogger.log(Level.FINE, message, error);
	}

	public boolean isDebugEnabled() {
		return julLogger.isLoggable(Level.FINE);
	}

	public boolean isInfoEnabled() {
		return julLogger.isLoggable(Level.INFO);
	}
  
  public void logError(String message) {
    julLogger.log(Level.SEVERE, message);
  }
  
  public void logError(String message, Throwable error) {
    julLogger.log(Level.SEVERE, message, error);
  }
  
  public boolean isErrorEnabled() {
    return julLogger.isLoggable(Level.SEVERE);
  }

}
