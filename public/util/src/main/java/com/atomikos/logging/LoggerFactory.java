package com.atomikos.logging;

public final class LoggerFactory {

	
	private static LoggerFactoryDelegate loggerFactoryDelegate;
	
	public static Logger createLogger (Class<?> clazz) {
		return loggerFactoryDelegate.createLogger(clazz);
	}

	static void setLoggerFactoryDelegate(
			LoggerFactoryDelegate loggerFactoryDelegate) {
		LoggerFactory.loggerFactoryDelegate = loggerFactoryDelegate;
	}
	
	
}
