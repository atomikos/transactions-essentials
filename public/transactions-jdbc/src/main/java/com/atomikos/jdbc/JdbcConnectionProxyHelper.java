/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class JdbcConnectionProxyHelper {
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
					LOGGER.logWarning ( msg , err );
					throw err;
				} else if ( ex instanceof RuntimeException ) {
					RuntimeException rte = ( RuntimeException ) ex;
					LOGGER.logWarning ( msg , ex );
					throw rte;
				} else if ( ex instanceof SQLException ) {
					SQLException driverError = ( SQLException ) ex;
					LOGGER.logWarning ( msg , ex );
					throw driverError;
				} else if ( ex instanceof InvocationTargetException ) {
					InvocationTargetException ite = ( InvocationTargetException ) ex;
					Throwable cause = ite.getCause();
					if ( cause != null ) {
						//log as debug and let the convert do the rest for the cause
						if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( msg , ite );
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
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "setting isolation level to " + defaultIsolationLevel);
			connection.setTransactionIsolation ( defaultIsolationLevel );
		}
		catch (SQLException ex) {
			LOGGER.logWarning ( "cannot set isolation level to " + defaultIsolationLevel, ex);
			throw new CreateConnectionException ( "The configured default isolation level " + defaultIsolationLevel +
					" seems unsupported by the driver - please check your JDBC driver documentation?" , ex );
		}
	}

}
