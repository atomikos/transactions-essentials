package com.atomikos.logging;

public class Log4JLoggerFactoryDelegate implements LoggerFactoryDelegate {

	public Logger createLogger(Class<?> clazz) {

		return new Log4JLogger(clazz);
	}

}
