/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.datasource.pool.event.ConnectionPoolExhaustedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionCreatedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionDestroyedEvent;
import com.atomikos.datasource.pool.event.PooledConnectionReapedEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.thread.InterruptedExceptionHelper;
import com.atomikos.thread.TaskManager;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;


public class ConnectionPool implements XPooledConnectionEventListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPool.class);

	private final static int DEFAULT_MAINTENANCE_INTERVAL = 60;

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
		if (destroyed) throw new ConnectionPoolException ( "Pool was already destroyed - you can no longer use it" );
	}

	private void init() throws ConnectionPoolException
	{
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": initializing..." );
		addConnectionsIfMinPoolSizeNotReached();
		launchMaintenanceTimer();
	}

	private void launchMaintenanceTimer() {
		int maintenanceInterval = properties.getMaintenanceInterval();
		if ( maintenanceInterval <= 0 ) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": using default maintenance interval..." );
			maintenanceInterval = DEFAULT_MAINTENANCE_INTERVAL;
		}
		maintenanceTimer = new PooledAlarmTimer ( maintenanceInterval * 1000 );
		maintenanceTimer.addAlarmTimerListener(new AlarmTimerListener() {
			public void alarm(AlarmTimer timer) {
				reapPool();
				removeConnectionsThatExceededMaxLifetime();
				addConnectionsIfMinPoolSizeNotReached();
				removeIdleConnectionsIfMinPoolSizeExceeded();
			}
		});
		TaskManager.SINGLETON.executeTask ( maintenanceTimer );
	}

	private synchronized void addConnectionsIfMinPoolSizeNotReached() {
		int connectionsToAdd = properties.getMinPoolSize() - totalSize();
		for ( int i = 0 ; i < connectionsToAdd ; i++ ) {
			try {
				XPooledConnection xpc = createPooledConnection();
				connections.add ( xpc );
				xpc.registerXPooledConnectionEventListener ( this );
			} catch ( Exception dbDown ) {
				//see case 26380
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": could not establish initial connection" , dbDown );
			}
		}
	}

	private XPooledConnection createPooledConnection()
			throws CreateConnectionException {
		XPooledConnection xpc = connectionFactory.createPooledConnection();
		EventPublisher.publish(new PooledConnectionCreatedEvent(properties.getUniqueResourceName(),xpc));
		return xpc;
	}

	private Reapable recycleConnectionIfPossible() throws Exception
	{
		Reapable ret = null;
		for (int i = 0; i < totalSize(); i++) {
			XPooledConnection xpc = (XPooledConnection) connections.get(i);

			if (xpc.canBeRecycledForCallingThread()) {
				ret = xpc.createConnectionProxy();
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": recycling connection from pool..." );
				return ret;
			}
		}
		return ret;
	}

	/**
	 * Borrows a connection from the pool.
	 * @return The connection as Reapable.
	 * @throws CreateConnectionException If the pool attempted to grow but failed.
	 * @throws PoolExhaustedException If the pool could not grow because it is exhausted.
	 * @throws ConnectionPoolException Other errors.
	 */
	public synchronized Reapable borrowConnection() throws CreateConnectionException , PoolExhaustedException, ConnectionPoolException
	{
		assertNotDestroyed();

		Reapable ret = null;	
		ret = findExistingOpenConnectionForCallingThread();	
		if (ret == null) {
			ret = findOrWaitForAnAvailableConnection();		
		}
		return ret;
	}

	private Reapable findOrWaitForAnAvailableConnection() throws ConnectionPoolException {
		Reapable ret = null;
		long remainingTime = properties.getBorrowConnectionTimeout() * 1000L;		
		do {
			ret = retrieveFirstAvailableConnectionAndGrowPoolIfNecessary();
			if ( ret == null ) {
				EventPublisher.publish(new ConnectionPoolExhaustedEvent(properties.getUniqueResourceName()));
				remainingTime = waitForAtLeastOneAvailableConnection(remainingTime);
				assertNotDestroyed();
			}
		} while ( ret == null );
		return ret;
	}

	private Reapable retrieveFirstAvailableConnectionAndGrowPoolIfNecessary() throws CreateConnectionException {
		
		Reapable ret = retrieveFirstAvailableConnection();
		if ( ret == null && canGrow() ) {
			growPool();
			ret = retrieveFirstAvailableConnection();
		}		
		return ret;
	}

	private Reapable findExistingOpenConnectionForCallingThread() {
		Reapable recycledConnection = null ;
		try {
			recycledConnection = recycleConnectionIfPossible();
		} catch (Exception e) {
			//ignore but log
			LOGGER.logWarning ( this + ": error while trying to recycle" , e );
		}
		return recycledConnection;
	}

	private void logCurrentPoolSize() {
		if ( LOGGER.isTraceEnabled() )  {
			LOGGER.logTrace( this +  ": current size: " + availableSize() + "/" + totalSize());
		}
	}

	private boolean canGrow() {
		return totalSize() < properties.getMaxPoolSize();
	}

	private Reapable retrieveFirstAvailableConnection() {
		Reapable ret = null;
		Iterator<XPooledConnection> it = connections.iterator();			
		while ( it.hasNext() && ret == null ) {
			XPooledConnection xpc =  it.next();
			if (xpc.isAvailable()) {
				try {
					ret = xpc.createConnectionProxy();
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": got connection from pool");
				} catch ( CreateConnectionException ex ) {
					String msg = this +  ": error creating proxy of connection " + xpc;
					LOGGER.logWarning( msg , ex);
					it.remove();
					destroyPooledConnection(xpc);
				} finally {
					logCurrentPoolSize();
				}
			}
		}
		return ret;
	}

	private synchronized void growPool() throws CreateConnectionException {
		XPooledConnection xpc = createPooledConnection();
		connections.add ( xpc );
		xpc.registerXPooledConnectionEventListener(this);
		logCurrentPoolSize();
	}

	private synchronized void removeIdleConnectionsIfMinPoolSizeExceeded() {
		if (connections == null || properties.getMaxIdleTime() <= 0 )
			return;

		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": trying to shrink pool" );
		List<XPooledConnection> connectionsToRemove = new ArrayList<XPooledConnection>();
		int maxConnectionsToRemove = totalSize() - properties.getMinPoolSize();
		if ( maxConnectionsToRemove > 0 ) {
			for ( int i=0 ; i < connections.size() ; i++ ) {
				XPooledConnection xpc = ( XPooledConnection ) connections.get(i);
				long lastRelease = xpc.getLastTimeReleased();
				long maxIdle = properties.getMaxIdleTime();
				long now = System.currentTimeMillis();
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection idle for " + (now - lastRelease) + "ms");
				if ( xpc.isAvailable() &&  ( (now - lastRelease) >= (maxIdle * 1000L) ) && ( connectionsToRemove.size() < maxConnectionsToRemove ) ) {
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection idle for more than " + maxIdle + "s, closing it: " + xpc);
					destroyPooledConnection(xpc);
					connectionsToRemove.add(xpc);
				}
			}
		}
		connections.removeAll(connectionsToRemove);
		logCurrentPoolSize();
	}

	private void destroyPooledConnection(XPooledConnection xpc) {
		xpc.destroy();
		EventPublisher.publish(new PooledConnectionDestroyedEvent(properties.getUniqueResourceName(),xpc));
	}

	public synchronized void reapPool()
	{
		long maxInUseTime = properties.getReapTimeout();
		if ( connections == null || maxInUseTime <= 0 ) return;

		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": reaping old connections" );

		Iterator<XPooledConnection> it = connections.iterator();
		while ( it.hasNext() ) {
			XPooledConnection xpc = it.next();
			long lastTimeReleased = xpc.getLastTimeAcquired();
			boolean inUse = !xpc.isAvailable();

			long now = System.currentTimeMillis();
			if ( inUse && ( ( now - maxInUseTime * 1000 ) > lastTimeReleased ) ) {
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection in use for more than " + maxInUseTime + "s, reaping it: " + xpc );
				xpc.reap();
				EventPublisher.publish(new PooledConnectionReapedEvent(properties.getUniqueResourceName(),xpc));
			}
		}
		logCurrentPoolSize();
	}
	
	private synchronized void removeConnectionsThatExceededMaxLifetime()
	{
		long maxLifetime = properties.getMaxLifetime();
		if ( connections == null || maxLifetime <= 0 ) return;

		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": closing connections that exceeded maxLifetime" );

		Iterator<XPooledConnection> it = connections.iterator();
		while ( it.hasNext() ) {
			XPooledConnection xpc = it.next();
			long creationTime = xpc.getCreationTime();
			long now = System.currentTimeMillis();
			if ( xpc.isAvailable() &&  ( (now - creationTime) >= (maxLifetime * 1000L) ) ) {
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection in use for more than " + maxLifetime + "s, destroying it: " + xpc );
				destroyPooledConnection(xpc);
				it.remove();
			}
		}
		logCurrentPoolSize();
	}

	public synchronized void destroy()
	{

		if ( ! destroyed ) {
			LOGGER.logNewInfo ( this + ": destroying pool..." );
			for ( int i=0 ; i < connections.size() ; i++ ) {
				XPooledConnection xpc = ( XPooledConnection ) connections.get(i);
				if ( !xpc.isAvailable() ) {
					LOGGER.logWarning ( this + ": connection is still in use on pool destroy: " + xpc +
					" - please check your shutdown sequence to avoid heuristic termination " +
					"of ongoing transactions!" );
				}
				destroyPooledConnection(xpc);
			}
			connections = null;
			destroyed = true;
			maintenanceTimer.stop();
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": pool destroyed." );
		}
	}
	
	public synchronized void refresh() {
		List<XPooledConnection> connectionsToRemove = new ArrayList<XPooledConnection>();
		for (XPooledConnection conn : connections) {
			if (conn.isAvailable()) {
				connectionsToRemove.add(conn);
				destroyPooledConnection(conn);
			}
		}
		connections.removeAll(connectionsToRemove);
		addConnectionsIfMinPoolSizeNotReached();
	}

	/**
	 * Wait until the connection pool contains an available connection or a timeout happens.
	 * Returns immediately if the pool already contains a connection in state available.
	 * @throws CreateConnectionException if a timeout happened while waiting for a connection
	 */
	private synchronized long waitForAtLeastOneAvailableConnection(long waitTime) throws PoolExhaustedException
	{
        while (availableSize() == 0) {
        	if ( waitTime <= 0 ) throw new PoolExhaustedException ( "ConnectionPool: pool is empty - increase either maxPoolSize or borrowConnectionTimeout" );
            long before = System.currentTimeMillis();
        	try {
        		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": about to wait for connection during " + waitTime + "ms...");
        		this.wait (waitTime);

			} catch (InterruptedException ex) {
				// cf bug 67457
				InterruptedExceptionHelper.handleInterruptedException ( ex );
				// ignore
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": interrupted during wait" , ex );
			}
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": done waiting." );
			long now = System.currentTimeMillis();
            waitTime -= (now - before);
        }
        return waitTime;
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
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this +  ": connection " + connection + " became available, notifying potentially waiting threads");
		this.notify();

	}
		
	public String toString() {
		return "atomikos connection pool '" + name + "'";
	}

}
