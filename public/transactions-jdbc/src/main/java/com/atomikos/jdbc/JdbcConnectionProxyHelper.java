/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.icatch.system.Configuration;

public class JdbcConnectionProxyHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(JdbcConnectionProxyHelper.class);

	/**
	 * Converts a driver error (generic exception) into an appropriate
	 * SQLException or RuntimeException.
	 *
	 * @param ex The driver exception.
	 * @param msg The message to use in the logs and conversion.
	 */

	public static void convertProxyError(Throwable ex, String msg)
			throws SQLException {
				if ( ex instanceof Error ) {
					Error err = ( Error ) ex;
					Configuration.logWarning ( msg , err );
					throw err;
				} else if ( ex instanceof RuntimeException ) {
					RuntimeException rte = ( RuntimeException ) ex;
					Configuration.logWarning ( msg , ex );
					throw rte;
				} else if ( ex instanceof SQLException ) {
					SQLException driverError = ( SQLException ) ex;
					Configuration.logWarning ( msg , ex );
					throw driverError;
				} else if ( ex instanceof InvocationTargetException ) {
					InvocationTargetException ite = ( InvocationTargetException ) ex;
					Throwable cause = ite.getCause();
					if ( cause != null ) {
						//log as debug and let the convert do the rest for the cause
						if ( LOGGER.isDebugEnabled() ) Configuration.logDebug ( msg , ite );
						convertProxyError ( cause , msg );
					}
					else {
						//cause is null -> throw AtomikosSQLException?
						AtomikosSQLException.throwAtomikosSQLException ( msg , ite );
					}
				}

				//default: throw AtomikosSQLException
				AtomikosSQLException.throwAtomikosSQLException ( msg , ex );

			}

	public static void setIsolationLevel ( Connection connection , int defaultIsolationLevel )
	throws CreateConnectionException
	{

		if (defaultIsolationLevel < 0)
			return;

		try {
			if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( "setting isolation level to " + defaultIsolationLevel);
			connection.setTransactionIsolation ( defaultIsolationLevel );
		}
		catch (SQLException ex) {
			Configuration.logWarning ( "cannot set isolation level to " + defaultIsolationLevel, ex);
			throw new CreateConnectionException ( "The configured default isolation level " + defaultIsolationLevel +
					" seems unsupported by the driver - please check your JDBC driver documentation?" , ex );
		}
	}

}
