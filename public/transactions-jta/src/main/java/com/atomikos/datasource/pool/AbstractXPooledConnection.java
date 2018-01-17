/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 
 /**
  * 
  * 
  * Abstract superclass with generic support for XPooledConnection.
  * 
  *
  */

public abstract class AbstractXPooledConnection implements XPooledConnection {
	private static final Logger LOGGER = LoggerFactory.createLogger(AbstractXPooledConnection.class);

	private long lastTimeAcquired = System.currentTimeMillis();
	private long lastTimeReleased = System.currentTimeMillis();
	private List<XPooledConnectionEventListener> poolEventListeners = new ArrayList<XPooledConnectionEventListener>();
	private Reapable currentProxy = null;
	private ConnectionPoolProperties props;
	private long creationTime = System.currentTimeMillis();
	private final AtomicBoolean isConcurrentlyBeingAcquired = new AtomicBoolean(false);

	
	protected AbstractXPooledConnection ( ConnectionPoolProperties props ) 
	{
		this.props = props;
	}

	public long getLastTimeAcquired() {
		return lastTimeAcquired;
	}

	public long getLastTimeReleased() {
		return lastTimeReleased;
	}
	
	public synchronized Reapable createConnectionProxy() throws CreateConnectionException
	{
		updateLastTimeAcquired();
		testUnderlyingConnection();
		currentProxy = doCreateConnectionProxy();
		isConcurrentlyBeingAcquired.set(false);
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": returning proxy " + currentProxy );
		return currentProxy;
	}

	public void reap() {
		
		if ( currentProxy != null ) {
			LOGGER.logWarning ( this + ": reaping connection..." );
			currentProxy.reap();
		}
		updateLastTimeReleased();
	}

	public void registerXPooledConnectionEventListener(XPooledConnectionEventListener listener) {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": registering listener " + listener );
		poolEventListeners.add(listener);
	}

	public void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener listener) {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": unregistering listener " + listener );
		poolEventListeners.remove(listener);
	}

	protected void fireOnXPooledConnectionTerminated() {
		for (int i=0; i<poolEventListeners.size() ;i++) {
			XPooledConnectionEventListener listener = (XPooledConnectionEventListener) poolEventListeners.get(i);
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": notifying listener: " + listener );
			listener.onXPooledConnectionTerminated(this);
		}
		updateLastTimeReleased();
	}

	protected String getTestQuery() 
	{
		return props.getTestQuery();
	}
	
	protected void updateLastTimeReleased() {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": updating last time released" );
		lastTimeReleased = System.currentTimeMillis();
	}
	
	private void updateLastTimeAcquired() {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace (  this + ": updating last time acquired" );
		lastTimeAcquired = System.currentTimeMillis();
		
	}
	
	protected Reapable getCurrentConnectionProxy() {
		return currentProxy;
	}

	public boolean canBeRecycledForCallingThread ()
	{
		//default is false
		return false;
	}

	protected int getDefaultIsolationLevel() 
	{
		return props.getDefaultIsolationLevel();
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public boolean markAsBeingAcquiredIfAvailable() {
		synchronized (isConcurrentlyBeingAcquired) {
			if (isConcurrentlyBeingAcquired.get()) {
				return false;
			}
			isConcurrentlyBeingAcquired.set(isAvailable());
			return isConcurrentlyBeingAcquired.get();	
		}
	}
	
	protected abstract Reapable doCreateConnectionProxy() throws CreateConnectionException;

	protected abstract void testUnderlyingConnection() throws CreateConnectionException;
}
