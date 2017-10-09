/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.util.Iterator;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;


public class ConnectionPoolWithConcurrentValidation extends ConnectionPool
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPoolWithConcurrentValidation.class);

	public ConnectionPoolWithConcurrentValidation ( ConnectionFactory connectionFactory , ConnectionPoolProperties properties ) throws ConnectionPoolException
	{
		super(connectionFactory, properties);
	}
	
	@Override
	protected Reapable recycleConnectionIfPossible() throws Exception {
		Reapable ret = null;
		XPooledConnection xpc = findFirstRecyclablePooledConnectionForCallingThread();
		if (xpc != null) {
			ret = concurrentlyTryToRecycle(xpc);
		}
		return ret;
	}

	@Override
	protected Reapable retrieveFirstAvailableConnection() {
		Reapable ret = null;
		XPooledConnection xpc = findFirstAvailablePooledConnection();
		if (xpc != null) {
			ret = concurrentlyTryToUse(xpc);
		}
		return ret;
	}
	
	private Reapable concurrentlyTryToRecycle(XPooledConnection xpc) throws Exception {
		Reapable ret = null;
		synchronized(xpc) { // just to be sure, although concurrent threads should not happen
			if (xpc.canBeRecycledForCallingThread()) { 
				ret = xpc.createConnectionProxy();
			}
		}
		return ret;
	}
	
	private Reapable concurrentlyTryToUse(XPooledConnection xpc) {
		Reapable ret = null;
		synchronized(xpc) {
			if (xpc.isAvailable()) { 
				// re-check availability to avoid race conditions
				// for X concurrent threads: only the first to enter sync block will win
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
			}
		}
		return ret;
	}

	private synchronized XPooledConnection findFirstAvailablePooledConnection() {
		XPooledConnection ret = null;
		Iterator<XPooledConnection> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection xpc =  it.next();
			if (xpc.isAvailable()) {
				ret = xpc;
			}
		}
		return ret;
	}
	
	private synchronized XPooledConnection findFirstRecyclablePooledConnectionForCallingThread() {
		XPooledConnection ret = null;
		Iterator<XPooledConnection> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection xpc =  it.next();
			if (xpc.canBeRecycledForCallingThread()) {
				ret = xpc;
			}
		}
		return ret;
	}
	
	private synchronized void removePooledConnection(XPooledConnection xpc) {
		connections.remove(xpc);
		destroyPooledConnection(xpc);
	}

}
