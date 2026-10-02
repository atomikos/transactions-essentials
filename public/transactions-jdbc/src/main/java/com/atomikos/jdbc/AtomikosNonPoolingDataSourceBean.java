/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */


/**
 * Copyright (C) 2000-2025 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

import java.sql.Connection;

import java.sql.SQLException;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * 
 * A datasource implementation that still supports XA but does not pool connections.
 * 
 * Examples of where this can be useful:
 * 
 * <ul>
 *   <li><b>Testing and Development</b>: When you want to ensure that each test or
 *       transaction uses a completely fresh connection.</li>
 *   <li><b>Short-lived Applications</b>: For applications that run briefly and
 *       don't benefit from connection pooling overhead.</li>
 *   <li><b>Resource Constraints</b>: When you want to minimize the number of
 *       persistent connections to the database.</li>
 *   <li><b>Debugging</b>: When troubleshooting connection-related issues and
 *       want to eliminate pooling as a variable.</li>
 *   <li><b>Cloud Environments</b>: In serverless or ephemeral environments
 *       where maintaining a connection pool is counterproductive.</li>
 *   <li><b>Third-party Connection Pooling</b>: When delegating pooling to an
 *       external library such as Oracle UCP.</li>
 * </ul>
 */


public class AtomikosNonPoolingDataSourceBean extends AtomikosDataSourceBean {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonPoolingDataSourceBean.class);
	private transient ConnectionFactory<Connection> cf;

	public AtomikosNonPoolingDataSourceBean() {
		super.setPoolSize(0);
	}

	public Connection getConnection() throws SQLException {
		if (LOGGER.isDebugEnabled())
			LOGGER.logDebug(this + ": getConnection()...");
		Connection connection = null;
		init();
		try {
			XPooledConnection<Connection> xpc = cf.createPooledConnection();
			xpc.registerXPooledConnectionEventListener(con -> con.destroy());
			connection = xpc.createConnectionProxy();
		} catch (CreateConnectionException ex) {
			throwAtomikosSQLException("Failed to create a connection", ex);
		}
		return connection;
	}

	@Override
	protected ConnectionFactory<Connection> doInit() throws Exception {
		cf = super.doInit();
		return cf;
	}

	@Override
	public void setMaxPoolSize(int maxPoolSize) {
		thisPropertyIsNotRelevant();
	}

	@Override
	public void setMinPoolSize(int minPoolSize) {
		thisPropertyIsNotRelevant();
	}

	@Override
	public void setMaintenanceInterval(int maintenanceInterval) {
		thisPropertyIsNotRelevant();
	}

	@Override
	public void setPoolSize(int poolSize) {
		thisPropertyIsNotRelevant();
	}

	@Override
	public void setConcurrentConnectionValidation(boolean value) {
		thisPropertyIsNotRelevant();
	}

	private void thisPropertyIsNotRelevant() {
		throw new UnsupportedOperationException("This property applies to connection pooling and is not relevant for this instance.");
	}

	@Override
	public void setMaxIdleTime(int maxIdleTime) {
		thisPropertyIsNotRelevant();
	}

	@Override
	public void setMaxLifetime(int maxLifetime) {
		thisPropertyIsNotRelevant();
	}
	
	protected void assertPoolSizeSettings() throws AtomikosSQLException {
		// we don't care: no pool desired - poolSize is 0
	}
}
