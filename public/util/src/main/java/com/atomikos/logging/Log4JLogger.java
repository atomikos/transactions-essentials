/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import org.apache.log4j.Level;

class Log4JLogger implements Logger {

	private final org.apache.log4j.Logger log4jLogger;

	public Log4JLogger(Class<?> clazz) {
		log4jLogger = org.apache.log4j.Logger.getLogger(clazz);
	}

	public void logWarning(String message) {
		log4jLogger.warn(message);
	}

	public void logInfo(String message) {
		log4jLogger.info(message);
	}

	public void logDebug(String message) {
		log4jLogger.debug(message);
	}

	public void logWarning(String message, Throwable error) {
		log4jLogger.warn(message, error);

	}

	public void logInfo(String message, Throwable error) {
		log4jLogger.info(message, error);
	}

	public void logDebug(String message, Throwable error) {
		log4jLogger.debug(message, error);
	}

	public boolean isDebugEnabled() {

		return log4jLogger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {

		return log4jLogger.isInfoEnabled();
	}
  
  public void logError(String message) {
    log4jLogger.error(message);
  }  
  
  public void logError(String message, Throwable error) {
    log4jLogger.error(message, error);
  }  
  
  public boolean isErrorEnabled() {
    return log4jLogger.isEnabledFor(Level.ERROR);
  }

}
