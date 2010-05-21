package com.atomikos.jms;

import java.lang.reflect.Proxy;

import javax.jms.JMSException;
import javax.jms.XAConnection;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.DynamicProxy;

class AtomikosPooledJmsConnection extends AbstractXPooledConnection implements SessionHandleStateChangeListener {
	
	private XAConnection xaConnection;
	private XATransactionalResource jmsTransactionalResource;
	private Reapable currentProxy;
	private ConnectionPoolProperties props;
	private boolean erroneous;

	protected AtomikosPooledJmsConnection(XAConnection xac, XATransactionalResource jmsTransactionalResource, ConnectionPoolProperties props) {
		super(props);
		this.jmsTransactionalResource = jmsTransactionalResource;
		this.xaConnection = xac;
		this.props = props;
		this.erroneous = false;
	}

	protected Reapable doCreateConnectionProxy(HeuristicMessage msg) throws CreateConnectionException {
		currentProxy = AtomikosJmsConnectionProxy.newInstance ( xaConnection , jmsTransactionalResource , this , props );
		return currentProxy;
	}

	protected void testUnderlyingConnection() throws CreateConnectionException {
		if ( isErroneous() ) throw new CreateConnectionException ( this + ": connection is erroneous" );
	}

	public void destroy() {
		Configuration.logDebug ( this + ": destroying connection..." );
		if (xaConnection != null) {
			try {
				xaConnection.close();
			} catch (JMSException ex) {
				//ignore but log
				Configuration.logWarning ( this + ": error closing XAConnection: " , ex );
			}
		}
		xaConnection = null;
	}

	public synchronized boolean isAvailable() {
		boolean ret = true;
		if ( currentProxy != null ) {
			DynamicProxy dproxy = ( DynamicProxy ) currentProxy;
			AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) dproxy.getInvocationHandler();			
			ret = proxy.isAvailable();
		}
		return ret;
	}
	

	public synchronized boolean isErroneous() {
		boolean ret = erroneous;
		if ( currentProxy != null ) {
			AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler ( currentProxy );
			ret = ret || proxy.isErroneous();
		}
		return ret;
	}

	public synchronized boolean isInTransaction ( CompositeTransaction ct ) {
		boolean ret = false;
		if ( currentProxy != null ) {
			DynamicProxy dproxy = ( DynamicProxy ) currentProxy;
			AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) dproxy.getInvocationHandler();
			ret = proxy.isInTransaction ( ct );
		}
		return ret;
	}

	public void onTerminated() {
		boolean fireTerminatedEvent = false;
		
		synchronized ( this ) {
			//a session has terminated -> check reusability of all remaining
			Configuration.logDebug ( this + ": a session has terminated, is connection now available ? " + isAvailable() );
			if ( isAvailable() ) {
				if ( currentProxy != null ) {
					DynamicProxy dproxy = ( DynamicProxy ) currentProxy;
					AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) dproxy.getInvocationHandler();
					if ( proxy.isErroneous() ) erroneous = true;
					proxy.destroy();
				}
				
				fireTerminatedEvent = true;
			} else {
				//not yet available, but check if the connection is erroneous
				//which happens if the session being terminated is erroneous
				if ( currentProxy != null ) {
					DynamicProxy dproxy = ( DynamicProxy ) currentProxy;
					AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) dproxy.getInvocationHandler();
					if ( proxy.isErroneous() ) erroneous = true;
				}
			}
		}
		
        if ( fireTerminatedEvent ) {
            //callbacks done outside synch to avoid deadlock in case 27614
        	fireOnXPooledConnectionTerminated();
        }

		
	}
	
	public boolean canBeRecycledForCallingThread ()
	{
		boolean ret = false;
		if ( currentProxy != null ) {
			CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();
			
			CompositeTransaction current = tm.getCompositeTransaction();
			if ( ( current != null ) && ( current.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME) != null )) {
				DynamicProxy dproxy = ( DynamicProxy ) currentProxy;
				AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) dproxy.getInvocationHandler();
				//recycle if either inactive in this tx, OR if active (since a new session will be created anyway, and 
				//concurrent sessions are allowed on the same underlying connection!
				ret = proxy.isInactiveInTransaction(current) || proxy.isInTransaction( current );
			}
		}
		
		
		return ret;
	}
	
	public String toString() 
	{
		return "atomikos pooled connection for resource " + jmsTransactionalResource.getName();
	}

}
