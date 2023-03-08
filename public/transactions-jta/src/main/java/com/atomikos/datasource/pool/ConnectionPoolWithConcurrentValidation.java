/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.util.Iterator;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;


public class ConnectionPoolWithConcurrentValidation<ConnectionType> extends ConnectionPool<ConnectionType>
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPoolWithConcurrentValidation.class);

	public ConnectionPoolWithConcurrentValidation ( ConnectionFactory<ConnectionType> connectionFactory , ConnectionPoolProperties properties ) throws ConnectionPoolException
	{
		super(connectionFactory, properties);
	}
	
	@Override
	protected ConnectionType recycleConnectionIfPossible() throws Exception {
		ConnectionType ret = null;
		XPooledConnection<ConnectionType> xpc = findFirstRecyclablePooledConnectionForCallingThread();
		if (xpc != null) {
			ret = concurrentlyTryToRecycle(xpc);
		}
		return ret;
	}

	@Override
	protected ConnectionType retrieveFirstAvailableConnection() {
		ConnectionType ret = null;
		XPooledConnection<ConnectionType> xpc = claimFirstAvailablePooledConnection();
		if (xpc != null) {
			ret = concurrentlyTryToUse(xpc);
		}
		return ret;
	}
	
	private ConnectionType concurrentlyTryToRecycle(XPooledConnection<ConnectionType> xpc) throws Exception {
		ConnectionType ret = null;
		synchronized(xpc) { // just to be sure, although concurrent threads should not happen
			if (xpc.canBeRecycledForCallingThread()) { 
				ret = xpc.createConnectionProxy();
			}
		}
		return ret;
	}
	
	private ConnectionType concurrentlyTryToUse(XPooledConnection<ConnectionType> xpc) {
		ConnectionType ret = null;
			try {
				ret = xpc.createConnectionProxy();
				// here, connection is no longer available for other threads
			} catch ( CreateConnectionException ex ) {
				String msg = this +  ": error creating proxy of connection " + xpc;
				LOGGER.logDebug( msg , ex);
				removePooledConnection(xpc);
			} finally {
				logCurrentPoolSize();
			}
		return ret;
	}

	private synchronized XPooledConnection<ConnectionType> claimFirstAvailablePooledConnection() {
		XPooledConnection<ConnectionType> ret = null;
		Iterator<XPooledConnection<ConnectionType>> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection<ConnectionType> xpc =  it.next();
			if (xpc.markAsBeingAcquiredIfAvailable()) {
				ret = xpc;
			}
		}
		return ret;
	}
	
	private synchronized XPooledConnection<ConnectionType> findFirstRecyclablePooledConnectionForCallingThread() {
		XPooledConnection<ConnectionType> ret = null;
		Iterator<XPooledConnection<ConnectionType>> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection<ConnectionType> xpc =  it.next();
			if (xpc.canBeRecycledForCallingThread()) {
				ret = xpc;
			}
		}
		return ret;
	}
	
	private synchronized void removePooledConnection(XPooledConnection<ConnectionType> xpc) {
		connections.remove(xpc);
		destroyPooledConnection(xpc);
	}

}
