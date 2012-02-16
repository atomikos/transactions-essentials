package com.atomikos.logging;

public interface Logger {

	void logWarning(String message);

	void logInfo(String message);

	void logDebug(String message);

	void logWarning(String message, Throwable error);

	void logInfo(String message, Throwable error);

	void logDebug(String message, Throwable error);

	boolean isDebugEnabled();

	boolean isInfoEnabled();


}
