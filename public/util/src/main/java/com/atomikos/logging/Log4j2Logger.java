package com.atomikos.logging;

import org.apache.logging.log4j.LogManager;

public class Log4j2Logger implements Logger {

	private org.apache.logging.log4j.Logger logger;

	public Log4j2Logger(Class<?> clazz) {
		logger = LogManager.getLogger(clazz);
	}
	@Override
	public void logError(String message) {
		logger.error(message);
	}

	@Override
	public void logWarning(String message) {
		logger.warn(message);
	}

	@Override
	public void logInfo(String message) {
		logger.info(message);
	}

	@Override
	public void logDebug(String message) {
		logger.debug(message);
	}

	@Override
	public void logError(String message, Throwable error) {
		logger.error(message, error);
	}

	@Override
	public void logWarning(String message, Throwable error) {
		logger.warn(message, error);

	}

	@Override
	public void logInfo(String message, Throwable error) {
		logger.info(message, error);
	}

	@Override
	public void logDebug(String message, Throwable error) {
		logger.debug(message, error);

	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

}