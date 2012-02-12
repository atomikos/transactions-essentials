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

import java.sql.SQLException;
import java.util.Stack;

import javax.sql.XAConnection;
import javax.sql.XADataSource;


import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

class AtomikosXAConnectionFactory implements ConnectionFactory
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosXAConnectionFactory.class);

	//TODO user and password to get new connections
	private JdbcTransactionalResource jdbcTransactionalResource;
	private XADataSource xaDataSource;
	private ConnectionPoolProperties props;

	public AtomikosXAConnectionFactory ( XADataSource xaDataSource, JdbcTransactionalResource jdbcTransactionalResource, ConnectionPoolProperties props )
	{
		this.xaDataSource = xaDataSource;
		this.jdbcTransactionalResource = jdbcTransactionalResource;
		this.props = props;
	}

	public XPooledConnection createPooledConnection() throws CreateConnectionException
	{
		try {
			XAConnection xaConnection = xaDataSource.getXAConnection();
			return new AtomikosXAPooledConnection ( xaConnection, jdbcTransactionalResource, props );
		} catch ( SQLException e ) {
			String msg = "XAConnectionFactory: failed to create pooled connection - DBMS down or unreachable?";
			LOGGER.logWarning ( msg , e );
			throw new CreateConnectionException ( msg , e );
		}
	}

}
