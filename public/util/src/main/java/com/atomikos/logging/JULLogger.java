package com.atomikos.logging;

import java.util.logging.Level;


public class JULLogger implements Logger {

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

}
