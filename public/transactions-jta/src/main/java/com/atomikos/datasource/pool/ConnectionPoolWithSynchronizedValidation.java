package com.atomikos.datasource.pool;


/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

public class ConnectionPoolWithSynchronizedValidation extends ConnectionPool {
	
	public ConnectionPoolWithSynchronizedValidation(
			ConnectionFactory connectionFactory,
			ConnectionPoolProperties properties) throws ConnectionPoolException {
		super(connectionFactory, properties);
	}
	
	@Override
	public synchronized Reapable borrowConnection() throws CreateConnectionException , PoolExhaustedException, ConnectionPoolException
	{
		return super.borrowConnection();
	}

}
