package com.atomikos.logging;

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

}
