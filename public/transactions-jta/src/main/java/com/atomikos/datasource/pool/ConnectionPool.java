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

package com.atomikos.datasource.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.imp.thread.InterruptedExceptionHelper;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;


public class ConnectionPool implements XPooledConnectionEventListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPool.class);

	public final static int DEFAULT_MAINTENANCE_INTERVAL = 60;

	private List<XPooledConnection> connections = new ArrayList<XPooledConnection>();
	private ConnectionFactory connectionFactory;
	private ConnectionPoolProperties properties;
	private boolean destroyed;
	private PooledAlarmTimer maintenanceTimer;
	private String name;


	public ConnectionPool ( ConnectionFactory connectionFactory , ConnectionPoolProperties properties ) throws ConnectionPoolException
	{
		this.connectionFactory = connectionFactory;
		this.properties = properties;
		this.destroyed = false;
		this.name = properties.getUniqueResourceName();
		init();
	}

	private void assertNotDestroyed() throws ConnectionPoolException
	{
		if ( destroyed ) throw new ConnectionPoolException ( "Pool was already destroyed - you can no longer use it" );
	}

	private void init() throws ConnectionPoolException
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": initializing..." );
		for ( int i=0; i < properties.getMinPoolSize() ; i++ ) {
			try {
				XPooledConnection xpc = connectionFactory.createPooledConnection();
				connections.add ( xpc );
				xpc.registerXPooledConnectionEventListener ( this );
			} catch ( Exception dbDown ) {
				//see case 26380
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": could not establish initial connection" , dbDown );
			}
		}
		int maintenanceInterval = properties.getMaintenanceInterval();
		if ( maintenanceInterval <= 0 ) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": using default maintenance interval..." );
			maintenanceInterval = DEFAULT_MAINTENANCE_INTERVAL;
		}

		maintenanceTimer = new PooledAlarmTimer ( maintenanceInterval * 1000 );
		maintenanceTimer.addAlarmTimerListener(new AlarmTimerListener() {
			public void alarm(AlarmTimer timer) {
				shrinkPool();
				reapPool();
			}
		});
		TaskManager.getInstance().executeTask ( maintenanceTimer );
		
	}

	private Reapable recycleConnectionIfPossible ( HeuristicMessage hmsg ) throws Exception
	{
		Reapable ret = null;
		for (int i = 0; i < totalSize(); i++) {
			XPooledConnection xpc = (XPooledConnection) connections.get(i);

			if (xpc.canBeRecycledForCallingThread()) {
				ret = xpc.createConnectionProxy ( hmsg );
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": recycling connection from pool..." );
				return ret;
			}
		}
		return ret;
	}

	/**
	 * Borrows a connection from the pool.
	 * @param hmsg The heuristic message to get the connection with.
	 * @return The connection as Reapable.
	 * @throws CreateConnectionException If the pool attempted to grow but failed.
	 * @throws PoolExhaustedException If the pool could not grow because it is exhaused.
	 * @throws ConnectionPoolException Other errors.
	 */
	public synchronized Reapable borrowConnection ( HeuristicMessage hmsg ) throws CreateConnectionException , PoolExhaustedException, ConnectionPoolException
	{

		long remainingTime = properties.getBorrowConnectionTimeout() * 1000L;
		Reapable ret = null;

		while ( ret == null ) {
			assertNotDestroyed();
			if (remainingTime <= 0)
				throw new PoolExhaustedException ( "Cannot get a connection after waiting for " + properties.getBorrowConnectionTimeout() + " secs" );

			Reapable recycledConnection = null ;
			try {
				recycledConnection = recycleConnectionIfPossible ( hmsg );
			} catch (Exception e) {
				//ignore but log
				LOGGER.logWarning ( this + ": error while trying to recycle" , e );
				//don't throw: just try normal borrow logic instead...
			}
			if ( recycledConnection != null ) return recycledConnection;


			if (availableSize() == 0 && totalSize() < properties.getMaxPoolSize()) {
				growPool();
			} else {
				if (totalSize() == properties.getMaxPoolSize())
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this +  ": pool reached max size: " + properties.getMaxPoolSize());

				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this +  ": current size: " + availableSize() + "/" + totalSize());
				remainingTime = waitForConnectionInPoolIfNecessary(remainingTime);
			}

			XPooledConnection xpc = null;


			Iterator<XPooledConnection> it = connections.iterator();
			while ( it.hasNext() && ret == null ) {
				xpc =  it.next();

				if (xpc.isAvailable()) {
					try {
						ret = xpc.createConnectionProxy ( hmsg );
						if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": got connection from pool, new size: " + availableSize() + "/" + totalSize());
					} catch ( CreateConnectionException ex ) {
						String msg = this +  ": error creating proxy of connection " + xpc;
						LOGGER.logWarning( msg , ex);
						it.remove();
						xpc.destroy();
					}
				}
			}

			if ( ret == null ) {
				//no available connection found -> wait and try again until DB available or remaining time is over
				LOGGER.logWarning ( this + ": no connection found - waiting a bit..." );
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// cf bug 67457
					InterruptedExceptionHelper.handleInterruptedException ( e );
				}
				remainingTime -= 1000;
			}
		} 
		return ret;
	}

	private synchronized void growPool() throws CreateConnectionException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": growing pool size to: " + (totalSize() + 1));
		XPooledConnection xpc = connectionFactory.createPooledConnection();
		connections.add ( xpc );
		xpc.registerXPooledConnectionEventListener(this);
	}

	private synchronized void shrinkPool() {
		if (connections == null || properties.getMaxIdleTime() <= 0 )
			return;

		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": trying to shrink pool" );
		List<XPooledConnection> connectionsToRemove = new ArrayList<XPooledConnection>();
		int maxConnectionsToRemove = totalSize() - properties.getMinPoolSize();
		if ( maxConnectionsToRemove > 0 ) {
			for ( int i=0 ; i < connections.size() ; i++ ) {
				XPooledConnection xpc = ( XPooledConnection ) connections.get(i);
				long lastRelease = xpc.getLastTimeReleased();
				long maxIdle = properties.getMaxIdleTime();
				long now = System.currentTimeMillis();
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": connection idle for " + (now - lastRelease) + "ms");
				if ( xpc.isAvailable() &&  ( (now - lastRelease) >= (maxIdle * 1000L) ) && ( connectionsToRemove.size() < maxConnectionsToRemove ) ) {
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": connection idle for more than " + maxIdle + "s, closing it: " + xpc);

					xpc.destroy();
					connectionsToRemove.add(xpc);
				}
			}
		}
		connections.removeAll(connectionsToRemove);
	}

	private synchronized void reapPool()
	{
		long maxInUseTime = properties.getReapTimeout();
		if ( connections == null || maxInUseTime <= 0 ) return;

		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": reaping old connections" );

		Iterator<XPooledConnection> it = connections.iterator();
		while ( it.hasNext() ) {
			XPooledConnection xpc = it.next();
			long lastTimeReleased = xpc.getLastTimeAcquired();
			boolean inUse = !xpc.isAvailable();

			long now = System.currentTimeMillis();
			if ( inUse && ( ( now - maxInUseTime * 1000 ) > lastTimeReleased ) ) {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": connection in use for more than " + maxInUseTime + "s, reaping it: " + xpc );
				xpc.reap();
			}
		}
	}

	public synchronized void destroy()
	{

		if ( ! destroyed ) {
			LOGGER.logInfo ( this + ": destroying pool..." );
			for ( int i=0 ; i < connections.size() ; i++ ) {
				XPooledConnection xpc = ( XPooledConnection ) connections.get(i);
				if ( !xpc.isAvailable() ) {
					LOGGER.logWarning ( this + ": connection is still in use on pool destroy: " + xpc +
					" - please check your shutdown sequence to avoid heuristic termination " +
					"of ongoing transactions!" );
				}
				xpc.destroy();
			}
			connections = null;
			destroyed = true;
			maintenanceTimer.stop();
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": pool destroyed." );
		}
	}

	/**
	 * Wait until the connection pool contains an available connection or a timeout happens.
	 * Returns immediately if the pool already contains a connection in state available.
	 * @throws CreateConnectionException if a timeout happened while waiting for a connection
	 */
	private synchronized long waitForConnectionInPoolIfNecessary(long remainingTime) throws PoolExhaustedException
	{
        while (availableSize() == 0) {
        	if ( properties.getBorrowConnectionTimeout() <= 0 ) throw new PoolExhaustedException ( "ConnectionPool: pool is empty and borrowConnectionTimeout is not set" );
            long before = System.currentTimeMillis();
        	try {
        		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": about to wait for connection during " + remainingTime + "ms...");

        		this.wait (remainingTime);

			} catch (InterruptedException ex) {
				// cf bug 67457
				InterruptedExceptionHelper.handleInterruptedException ( ex );
				// ignore
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": interrupted during wait" , ex );
			}
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": done waiting." );
			long now = System.currentTimeMillis();
            remainingTime -= (now - before);
            if (remainingTime <= 0)
            	throw new PoolExhaustedException ("Connection pool is still empty after waiting for " + properties.getBorrowConnectionTimeout() + " secs" );
        }
        return remainingTime;
	}

	/**
	 * The amount of pooled connections in state available.
	 * @return the amount of pooled connections in state available.
	 */
	public synchronized int availableSize()
	{
		int ret = 0;

		if ( !destroyed ) {
			int count = 0;
			for ( int i=0 ; i < connections.size() ; i++ ) {
				XPooledConnection xpc = (XPooledConnection) connections.get(i);
				if (xpc.isAvailable()) count++;
			}
			ret = count;
		}
		return ret;
	}

	/**
	 * The total amount of pooled connections in any state.
	 * @return the total amount of pooled connections in any state
	 */
	public synchronized int totalSize()
	{
		if ( destroyed ) return 0;

		return connections.size();
	}

	public synchronized void onXPooledConnectionTerminated(XPooledConnection connection) {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this +  ": connection " + connection + " became available, notifying potentially waiting threads");

		this.notify();

	}
		
	public String toString() {
		return "atomikos connection pool '" + name + "'";
	}

}
