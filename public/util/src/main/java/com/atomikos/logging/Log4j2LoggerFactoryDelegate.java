package com.atomikos.logging;

public class Log4j2LoggerFactoryDelegate implements LoggerFactoryDelegate {

	@Override
	public Logger createLogger(Class<?> clazz) {

		return new Log4j2Logger(clazz);
	}

	@Override
	public String toString() {

		return "Log4j2";
	}
}
