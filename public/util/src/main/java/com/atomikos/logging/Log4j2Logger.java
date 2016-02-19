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
