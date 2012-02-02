package com.atomikos.logging;

interface LoggerFactoryDelegate {

	Logger createLogger(Class<?> clazz);

}
