/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

	private static final Logger LOGGER = LoggerFactory.createLogger(MethodHandles.lookup().lookupClass());

	private long lastTimeAcquired = System.currentTimeMillis();
	private long lastTimeReleased = System.currentTimeMillis();
	private List<XPooledConnectionEventListener> poolEventListeners = new ArrayList<XPooledConnectionEventListener>();
	private Reapable currentProxy = null;
	private ConnectionPoolProperties props;
	private long creationTime = System.currentTimeMillis();

	protected Connection connection;
	
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
	
	protected abstract Reapable doCreateConnectionProxy() throws CreateConnectionException;

	protected void testUnderlyingConnection() throws CreateConnectionException {
    if ( isErroneous() ) throw new CreateConnectionException ( this + ": connection is erroneous" );

    String testQuery = getTestQuery();

    if (testQuery != null) {
      if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": testing connection with query [" + testQuery + "]" );

      try (Statement stmt = connection.createStatement()){

        //use execute instead of executeQuery - cf case 58830
        stmt.execute(testQuery);
        stmt.close();
      } catch ( Exception e) {
        //catch any Exception - cf case 22198
        throw new CreateConnectionException ( "Error executing testQuery" ,  e );
      }
      if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection tested OK" );
    }
    else {
      if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": no test query, skipping test" );
    }
  }
}
