package com.atomikos.logging;

class Log4JLoggerFactoryDelegate implements LoggerFactoryDelegate {

	public Logger createLogger(Class<?> clazz) {

		return new Log4JLogger(clazz);
	}

	@Override
	public String toString() {

		return "Log4j";
	}
}
