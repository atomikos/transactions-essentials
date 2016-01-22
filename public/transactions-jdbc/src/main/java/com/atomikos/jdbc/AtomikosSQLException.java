/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jdbc;

import java.sql.SQLException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosSQLException extends SQLException {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosSQLException.class);
	
	/**
	 * Logs and throws an AtomikosSQLException.
	 * 
	 * @param message The message to use.
	 * @param cause The causing error.
	 * @throws AtomikosSQLException
	 */
	public static void throwAtomikosSQLException ( String message , Throwable cause ) throws AtomikosSQLException 
	{
		LOGGER.logWarning ( message , cause );
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
