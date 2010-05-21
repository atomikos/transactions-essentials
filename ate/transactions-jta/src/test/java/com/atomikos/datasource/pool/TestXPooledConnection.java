package com.atomikos.datasource.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

public class TestXPooledConnection implements XPooledConnection {

	private long lastTimeAcquired;
	private long lastTimeReleased;
	private List listeners = new ArrayList();
	private boolean erroneous;
	private boolean destroyed;
	
	public Reapable createConnectionProxy ( HeuristicMessage hmsg  ) throws CreateConnectionException {
		if ( erroneous ) throw new CreateConnectionException ( "erroneous connection" );
		
		
		lastTimeAcquired = System.currentTimeMillis();
		Configuration.logDebug ( this + ": setting lastTimeAcquired to "+ lastTimeAcquired );
		return new TestReapable ( this );
	}
	
    synchronized void release() {
		lastTimeReleased = System.currentTimeMillis();
		Configuration.logDebug ( this + ": setting lastTimeReleased to "+ lastTimeReleased );
		Iterator it = listeners.iterator();
		while ( it.hasNext() ) {
			XPooledConnectionEventListener next = ( XPooledConnectionEventListener ) it.next();
			next.onXPooledConnectionTerminated(this);
		}
	}

	public void destroy() {
		destroyed = true;
	}

	public long getLastTimeAcquired() {
		return lastTimeAcquired;
	}

	public long getLastTimeReleased() {
		return lastTimeReleased;
	}

	public boolean isAvailable() {
		return lastTimeReleased >= lastTimeAcquired;
	}

	public boolean isErroneous() {
		return erroneous;
	}

	void setErroneous() {
		this.erroneous = true;
	}
	
	boolean wasDestroyed() {
		return destroyed;
	}
	
	public boolean isInTransaction(CompositeTransaction ct) {
		return false;
	}

	public void reap() {
		//force release for testing only - a real implementation would
		//merely for the closing/invalidation of the proxy and let pending
		//transactions terminate.
		release();

	}

	public synchronized void registerXPooledConnectionEventListener(
			XPooledConnectionEventListener listener) {
		listeners.add( listener );

	}

	public synchronized void unregisterXPooledConnectionEventListener(
			XPooledConnectionEventListener listener) {
		listeners.remove ( listener );
	}

	static class TestReapable implements Reapable {

		private TestXPooledConnection xpc;
		
		TestReapable ( TestXPooledConnection xpc ) {
			this.xpc = xpc;
		}
		
		public TestXPooledConnection getTestXPooledConnection() {
			return xpc;
		}
		
		public void setErroneous() {
			xpc.setErroneous();
		}
		
		public boolean isErroneous() {
			return xpc.isErroneous();
		}
		
		public void close() {
			xpc.release();
		}

		public void reap() {
			xpc.release(); 
		}
		
		public boolean wasUnderlyingConnectionDestroyed() {
			return xpc.wasDestroyed();
		}
		
	}

	public boolean canBeRecycledForCallingThread() {
		return false;
	}

}
