package com.atomikos.logging;

public class JULLoggerFactoryDelegate implements LoggerFactoryDelegate {

	public Logger createLogger(Class<?> clazz) {

		return new JULLogger(clazz);
	}

	@Override
	public String toString() {

		return "Java Util Logging";
	}
}
