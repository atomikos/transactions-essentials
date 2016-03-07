/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
	
	public void logNewInfo(String message) {
		julLogger.log(Level.INFO,message);

	}

	public void logInfo(String message) {
		julLogger.log(Level.INFO,message);

	}

	public void logTrace(String message) {
		julLogger.log(Level.FINEST,message);
	}

	public void logWarning(String message, Throwable error) {
		julLogger.log(Level.WARNING, message, error);
		}

	public void logInfo(String message, Throwable error) {
		julLogger.log(Level.INFO, message, error);

	}

	public void logTrace(String message, Throwable error) {
		julLogger.log(Level.FINEST, message, error);
	}

	public boolean isTraceEnabled() {
		return julLogger.isLoggable(Level.FINEST);
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
