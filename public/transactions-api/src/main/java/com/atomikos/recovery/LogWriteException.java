package com.atomikos.recovery;

public class LogWriteException extends LogException {

	private static final long serialVersionUID = 5648208124041649641L;

	public LogWriteException() {
		super();
	}
	public LogWriteException(Throwable cause) {
		super(cause);
	}
	
}