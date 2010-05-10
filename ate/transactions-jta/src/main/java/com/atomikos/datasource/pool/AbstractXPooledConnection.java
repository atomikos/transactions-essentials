package com.atomikos.datasource.pool;

import java.util.ArrayList;
import java.util.List;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

 
 /**
  * 
  * 
  * Abstract superclass with generic support for XPooledConnection.
  * 
  * @author guy
  *
  */

public abstract class AbstractXPooledConnection implements XPooledConnection {

	private long lastTimeAcquired = System.currentTimeMillis();
	private long lastTimeReleased = System.currentTimeMillis();
	private List poolEventListeners = new ArrayList();
	private Reapable currentProxy = null;
	private ConnectionPoolProperties props;
	
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
	
	public synchronized Reapable createConnectionProxy ( HeuristicMessage hmsg ) throws CreateConnectionException
	{
		updateLastTimeAcquired();
		testUnderlyingConnection();
		currentProxy = doCreateConnectionProxy ( hmsg );
		Configuration.logDebug ( this + ": returning proxy " + currentProxy );
		return currentProxy;
	}

	public void reap() {
		
		if ( currentProxy != null ) {
			Configuration.logWarning ( this + ": reaping connection..." );
			currentProxy.reap();
		}
		updateLastTimeReleased();
	}

	public void registerXPooledConnectionEventListener(XPooledConnectionEventListener listener) {
		Configuration.logDebug ( this + ": registering listener " + listener );
		poolEventListeners.add(listener);
	}

	public void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener listener) {
		Configuration.logDebug ( this + ": unregistering listener " + listener );
		poolEventListeners.remove(listener);
	}

	protected void fireOnXPooledConnectionTerminated() {
		for (int i=0; i<poolEventListeners.size() ;i++) {
			XPooledConnectionEventListener listener = (XPooledConnectionEventListener) poolEventListeners.get(i);
			Configuration.logDebug ( this + ": notifying listener: " + listener );
			listener.onXPooledConnectionTerminated(this);
		}
		updateLastTimeReleased();
	}

	protected String getTestQuery() 
	{
		return props.getTestQuery();
	}
	
	protected void updateLastTimeReleased() {
		Configuration.logDebug ( this + ": updating last time released" );
		lastTimeReleased = System.currentTimeMillis();
	}
	
	protected void updateLastTimeAcquired() {
		Configuration.logDebug (  this + ": updating last time acquired" );
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
	
	protected abstract Reapable doCreateConnectionProxy ( HeuristicMessage hmsg ) throws CreateConnectionException;

	protected abstract void testUnderlyingConnection() throws CreateConnectionException;
}
