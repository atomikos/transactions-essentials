/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.util.Iterator;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;


/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

public class ConnectionPoolWithSynchronizedValidation<ConnectionType> extends ConnectionPool<ConnectionType> {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPoolWithSynchronizedValidation.class);
	
	public ConnectionPoolWithSynchronizedValidation(
			ConnectionFactory<ConnectionType> connectionFactory,
			ConnectionPoolProperties properties) throws ConnectionPoolException {
		super(connectionFactory, properties);
	}
	
	@Override
	public synchronized ConnectionType borrowConnection() throws CreateConnectionException , PoolExhaustedException, ConnectionPoolException
	{
		return super.borrowConnection();
	}
	
	protected ConnectionType recycleConnectionIfPossible() throws Exception
	{
		ConnectionType ret = null;
		for (int i = 0; i < totalSize(); i++) {
			XPooledConnection<ConnectionType> xpc = connections.get(i);

			if (xpc.canBeRecycledForCallingThread()) {
				ret = xpc.createConnectionProxy();
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": recycling connection from pool..." );
				return ret;
			}
		}
		return ret;
	}
	
	protected ConnectionType retrieveFirstAvailableConnection() {
		ConnectionType ret = null;
		Iterator<XPooledConnection<ConnectionType>> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection<ConnectionType> xpc =  it.next();
			if (xpc.isAvailable()) {
				try {
					ret = xpc.createConnectionProxy();
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": got connection from pool");
				} catch ( CreateConnectionException ex ) {
					String msg = this +  ": error creating proxy of connection " + xpc;
					LOGGER.logDebug( msg , ex);
					it.remove();
					destroyPooledConnection(xpc, false);
				} finally {
					logCurrentPoolSize();
				}
			}
		}
		return ret;
	}

}
