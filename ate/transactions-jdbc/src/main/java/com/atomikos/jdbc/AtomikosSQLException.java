package com.atomikos.jdbc;

import java.sql.SQLException;

import com.atomikos.icatch.system.Configuration;

public class AtomikosSQLException extends SQLException {
	
	/**
	 * Logs and throws an AtomikosSQLException.
	 * 
	 * @param message The message to use.
	 * @param cause The causing error.
	 * @throws AtomikosSQLException
	 */
	public static void throwAtomikosSQLException ( String message , Throwable cause ) throws AtomikosSQLException 
	{
		Configuration.logWarning ( message , cause );
		throw new AtomikosSQLException ( message , cause );
	}
	
	/**
	 * Logs and throws an AtomikosSQLException.
	 * 
	 * @param message The message to use.
	 * @throws AtomikosSQLException
	 */
	public static void throwAtomikosSQLException ( String message ) throws AtomikosSQLException 
	{
		throwAtomikosSQLException ( message , null );
	}
	
	private AtomikosSQLException(String message, Throwable cause) {
		super(message);
		
		if (cause instanceof SQLException) {
			setNextException((SQLException) cause);
		}
		initCause(cause);
	}

	private AtomikosSQLException(String message) {
		super(message);
	}

	private AtomikosSQLException(Throwable cause) {
		if (cause instanceof SQLException) {
			setNextException((SQLException) cause);
		}
		initCause(cause);
	}



}
