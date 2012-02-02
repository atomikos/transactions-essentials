package com.atomikos.logging;

public final class LoggerFactory {

	public static Logger createLogger (Class<?> clazz) {
		return new Slf4jLogger(clazz);
	}

	
	
}
