package com.atomikos.logging;

public interface LoggerFactoryDelegate {

	Logger createLogger(Class<?> clazz);

}
