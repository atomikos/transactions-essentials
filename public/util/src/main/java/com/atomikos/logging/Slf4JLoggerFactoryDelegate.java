/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

class Slf4JLoggerFactoryDelegate implements LoggerFactoryDelegate {

	public Logger createLogger(Class<?> clazz) {

		return new Slf4jLogger(clazz);
	}

	@Override
	public String toString() {

		return "Slf4J";
	}
}
