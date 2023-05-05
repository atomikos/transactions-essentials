/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class JdbcConnectionProxyHelper {
	private static final Logger LOGGER = LoggerFactory.createLogger(JdbcConnectionProxyHelper.class);

	
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
