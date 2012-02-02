package com.atomikos.logging;

public class Slf4JLoggerFactoryDelegate implements LoggerFactoryDelegate {

	public Logger createLogger(Class<?> clazz) {
		
		return new Slf4jLogger(clazz);
	}

}
