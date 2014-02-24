package com.atomikos.logging;

class Slf4jLogger implements Logger {

	private final org.slf4j.Logger slf4j;

	public Slf4jLogger(Class<?> clazz) {
		slf4j = org.slf4j.LoggerFactory.getLogger(clazz);
	}

	public void logWarning(String message) {
		slf4j.warn(message);
	}

	public void logInfo(String message) {
		slf4j.info(message);
	}

	public void logDebug(String message) {
		slf4j.debug(message);
	}

	public void logWarning(String message, Throwable error) {
		slf4j.warn(message,error);

	}

	public void logInfo(String message, Throwable error) {
		slf4j.info(message,error);
	}

	public void logDebug(String message, Throwable error) {
		slf4j.debug(message,error);

	}

	public boolean isDebugEnabled() {
		return slf4j.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return slf4j.isInfoEnabled();
	}
  
  public void logError(String message) {
    slf4j.error(message);
  }

  
  public void logError(String message, Throwable error) {
    slf4j.error(message, error);
  }

  
  public boolean isErrorEnabled() {
    return slf4j.isErrorEnabled();
  }

}
