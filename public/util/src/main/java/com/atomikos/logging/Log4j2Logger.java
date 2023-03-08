/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

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
	public void logTrace(String message) {
		logger.trace(message);
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
	public void logDebug(String message, Throwable error) {
		logger.debug(message, error);
	}

	@Override
	public void logTrace(String message, Throwable error) {
		logger.trace(message, error);

	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}
	@Override
	public void logInfo(String message, Throwable error) {
		logger.info(message, error);
	}
	
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	@Override
	public void logFatal(String message) {
		logger.fatal(message);
	}
	@Override
	public void logFatal(String message, Throwable error) {
		logger.fatal(message, error);
	}

}
