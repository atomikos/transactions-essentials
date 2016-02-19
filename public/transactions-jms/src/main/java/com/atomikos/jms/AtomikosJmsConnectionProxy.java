/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.jms;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

class AtomikosJmsConnectionProxy extends AbstractJmsProxy 
implements SessionHandleStateChangeListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsConnectionProxy.class);
	
	private static Class<?>[] MINIMUM_SET_OF_INTERFACES = {Reapable.class, DynamicProxy.class,javax.jms.Connection.class };
	private static final String CREATE_SESSION_METHOD = "createSession";
	
	private static final String CLOSE_METHOD = "close";
	
	private static final String REAP_METHOD = "reap";
	
	private static void forceConnectionIntoXaMode ( Connection c )
	{
	   //ORACLE AQ WORKAROUND: 
 	   //force connection into global tx mode
 	   //cf ISSUE 10095
	   Session s = null;
 	   try {
 		   s = c.createSession ( true , Session.AUTO_ACKNOWLEDGE );
 		   s.rollback();
 	   }
 	   catch ( Exception e ) {
 		   //ignore: workaround code
 		   if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "JMS: driver complains while enforcing XA mode - ignore if no later errors:" , e );
 	   } 
 	   finally {
 		   if ( s != null ) {
 			   try {
 				   s.close();
 			   } catch ( JMSException e ) {
 				   //ignore: workaround code
 				   if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "JMS: driver complains while enforcing XA mode - ignore if no later errors:" , e );
 			   }
 		   }
 	   }
	}
	
	private XAConnection delegate;
	private XATransactionalResource jmsTransactionalResource;
	private List<Session> sessions;
	private boolean closed;
	private boolean reaped;
	private SessionHandleStateChangeListener owner;
	private ConnectionPoolProperties props;
	private boolean erroneous;

	private boolean ignoreSessionTransactedFlag;
	
	private AtomikosJmsConnectionProxy ( boolean ignoreSessionTransactedFlag, XAConnection c , XATransactionalResource jmsTransactionalResource , SessionHandleStateChangeListener owner, ConnectionPoolProperties props ) 
	{
		this.delegate = c;
		this.sessions = new ArrayList<Session>();
		this.jmsTransactionalResource = jmsTransactionalResource;
		this.closed = false;
		this.reaped = false;
		this.owner = owner;
		this.props = props;
		this.ignoreSessionTransactedFlag = ignoreSessionTransactedFlag;
	}

	private void reap() {
		LOGGER.logWarning ( this + ": reaping - check if the application closes connections correctly, or increase the reapTimeout value");
		close();
		//added for 22101
		erroneous = true;
		reaped = true;
	}
	
	private void addSession ( Session session ) 
	{
		// fix for case 62041: synchronized!
		synchronized ( sessions ) {
			sessions.add ( session );
		}
	}


	//DON'T synchronize - see case 26976
	public Object invoke ( Object proxy, Method method, Object[] args ) throws JMSException 
	{
		String methodName = method.getName();
		
		//see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;
		
		//close on destroy is dealt with in AtomikosPooledJmsConnection?!
		
		try {
			if (methodName.equals( REAP_METHOD )) {
				reap();
				return null;
			}
			
			if ( CLOSE_METHOD.equals ( methodName ) ) {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": intercepting call to close" );
				close();
				return null;
			} else if ( reaped ) {
				String msg = "Connection was reaped - calling method " + methodName + " no longer allowed. Increase the reapTimeout to avoid this.";
				LOGGER.logWarning ( this + ": " + msg );
				throw new javax.jms.IllegalStateException ( msg );
			} else if ( closed && !methodAllowedAfterClose(method) ) {
				String msg = "Connection is closed already - calling method " + methodName + " no longer allowed.";
				LOGGER.logWarning ( this + ": " + msg );
				throw new javax.jms.IllegalStateException ( msg );
			}
			else if ( CREATE_SESSION_METHOD.equals ( methodName ) ) {
				Boolean transactedFlag = ( Boolean ) args[0];
				Session session = null;
				if ( createXaSession(transactedFlag.booleanValue()) ) {
					session = recycleSession();
					if (session == null) {
						if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": creating XA-capable session..." );
						forceConnectionIntoXaMode ( delegate );
						XASession wrapped = null;
						try {
							wrapped = delegate.createXASession();
						} catch ( JMSException vendorError ) {
							String msg = "Could not create an XASession on the javax.jms.XAConnectionFactory's XAConnection - check if your JMS backend is configured for XA?";
							convertProxyError( vendorError , msg );
						}
						session = (Session) AtomikosJmsXaSessionProxy.newInstance( wrapped , jmsTransactionalResource , owner , this );
						addSession ( session );
					}
				} else {
					CompositeTransaction ct = null;
					CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
					if ( ctm != null ) ct = ctm.getCompositeTransaction();
					if ( ct != null && ct.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {
						if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": creating NON-XA session - the resulting JMS work will NOT be part of the JTA transaction!" );
					}
					Integer ackMode = ( Integer ) args[1];
					Session wrapped = null;
					try {
						wrapped = delegate.createSession( transactedFlag.booleanValue() , ackMode.intValue() );
					} catch ( JMSException vendorError ) {
						String msg = "Could not create a non-XA session on the javax.jms.XAConnectionFactory's XAConnection - check your JMS vendor's documentation to see if non-XA use of its XAConnection is supported?";
						convertProxyError( vendorError , msg );
					}
					session = ( Session ) AtomikosJmsNonXaSessionProxy.newInstance( wrapped , owner , this );
					addSession ( session );
				}
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": returning " + session );
				return session;
				
			} else {		
			
					if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": calling " + methodName + " on JMS driver...");
					Object ret = method.invoke(delegate, args);
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": " + methodName + " returning " + ret );
					return ret;
			
			}
		} catch ( AtomikosJMSException e ) {
			//no need to log here: this exception is logged at throwing time
			erroneous = true;
			throw e;
		} catch (Exception e) {
			String msg = "Error delegating '" + methodName + "' call to JMS driver";
			erroneous = true;
			convertProxyError ( e , msg );
		}
		
		//dummy return to make compiler happy
		return null;
	}

	private boolean methodAllowedAfterClose(Method method) {
		return method.getName().equals("close") || ClassLoadingHelper.existsInJavaObjectClass(method);
	}

	private boolean createXaSession(boolean sessionTransactedFlag) {
		if (ignoreSessionTransactedFlag) {
			return !props.getLocalTransactionMode();
		}
		else {
			return sessionTransactedFlag && !props.getLocalTransactionMode();
		}
	}
	
	private synchronized Session recycleSession() {
		CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();
		if (tm == null)
			return null;
		
		CompositeTransaction current = tm.getCompositeTransaction();
		if ( ( current != null ) && ( current.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME) != null )) {
			synchronized (sessions) {
				for (int i=0; i<sessions.size() ;i++) {
					Session session = (Session) sessions.get(i);
					DynamicProxy dproxy = ( DynamicProxy ) session;
					AbstractJmsSessionProxy proxy = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
					
					//recycle if either inactive in this tx, OR if active (since a new session will be created anyway, and 
					//concurrent sessions are allowed on the same underlying connection!
					if ( proxy.isInactiveTransaction(current) || proxy.isInTransaction( current ) ) {
						if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": recycling session " + proxy );
						return session;
					}
				}
			} // synchronized (sessions)
		}
		return null;
	}

	private void close() {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": close()...");
		
		closed = true;		
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": closing " + sessions.size() + " session(s)" );
		
		//close all sessions to make sure the session close notifications are done!
		synchronized (sessions) {
			for (int i=0; i<sessions.size() ;i++)
			{
				Session session = (Session) sessions.get ( i );
				try {
					session.close ();
				} catch (JMSException ex) {
					LOGGER.logWarning ( this + ": error closing session " + session, ex );
				}
			}
		}
		
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": is available ? " + isAvailable() );
		if (isAvailable())
			owner.onTerminated();
		
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": closed." );	
		//leave destroy to the owning pooled connection - that one knows when any and all 2PCs are done
	}


	//should only be called after ALL sessions are done, i.e. when the connection can be pooled again
	synchronized void destroy() {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": closing connection and all " + sessions.size() + " session(s)" );

		//close all sessions to make sure the session close notifications are done!
		synchronized (sessions) {
			for (int i=0; i<sessions.size() ;i++)
			{
				Session session = (Session) sessions.get ( i );
				try {
					session.close ();
				} catch (JMSException ex) {
					LOGGER.logWarning ( this + ": error closing session " + session, ex );
				}
			}
		}

		sessions.clear ();
	}
	
	
	public static Reapable newInstance ( boolean ignoreSessionTransactedFlag, XAConnection c, XATransactionalResource jmsTransactionalResource , SessionHandleStateChangeListener owner , ConnectionPoolProperties props ) 
	{
		 Reapable ret = null;
		 
        AtomikosJmsConnectionProxy proxy = new AtomikosJmsConnectionProxy ( ignoreSessionTransactedFlag, c , jmsTransactionalResource , owner , props );
        Set<Class<?>> interfaces = PropertyUtils.getAllImplementedInterfaces ( c.getClass() );
        interfaces.add ( Reapable.class );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class<?>[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( c.getClass().getClassLoader() );
		classLoaders.add ( AtomikosJmsConnectionProxy.class.getClassLoader() );
        
		ret = ( Reapable ) ClassLoadingHelper.newProxyInstance ( classLoaders , MINIMUM_SET_OF_INTERFACES , interfaceClasses , proxy );
        
        return ret;
    }


	public boolean isAvailable() {
		boolean ret = closed;
		synchronized (sessions) {
			Iterator<Session> it = sessions.iterator();
			while ( it.hasNext() && ret ) {
				Session handle = it.next();
				DynamicProxy dproxy = ( DynamicProxy ) handle;
				AbstractJmsSessionProxy  session = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
				if ( !session.isAvailable() ) {
					ret = false;
				}
			}
		}
		return ret;
	}


	public boolean isErroneous() {
		boolean ret = erroneous;
		synchronized (sessions) {
			Iterator<Session> it = sessions.iterator();
			while ( it.hasNext() && !ret ) {
				Session handle = it.next();
				DynamicProxy dproxy = ( DynamicProxy ) handle;
				AbstractJmsSessionProxy  session = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
				if ( session.isErroneous() ) ret = true;
			}
		}
		return ret;
	}


	public boolean isInTransaction ( CompositeTransaction ct ) {
		boolean ret = false;
		synchronized (sessions) {
			Iterator<Session> it = sessions.iterator();
			while ( it.hasNext() && !ret ) {
				Session handle = it.next();
				DynamicProxy dproxy = ( DynamicProxy ) handle;
				AbstractJmsSessionProxy  session = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
				if ( session.isInTransaction ( ct ) ) ret = true;
			}
		}
		return ret;
	}
	
	boolean isInactiveInTransaction ( CompositeTransaction ct ) {
		boolean ret = false;
		synchronized (sessions) {
			Iterator<Session> it = sessions.iterator();
			while ( it.hasNext() && !ret ) {
				Session handle = it.next();
				DynamicProxy dproxy = ( DynamicProxy ) handle;
				AbstractJmsSessionProxy  session = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
				if ( session.isInactiveTransaction ( ct ) ) ret = true;
			}
		}
		return ret;
	}

	public String toString() {    
		return "atomikos connection proxy for resource " + jmsTransactionalResource.getName();
	}

	public synchronized void onTerminated() 
	{
		//a session has terminated -> remove it from the list of sessions to enable GC
		synchronized (sessions) {
			Iterator<Session> it = sessions.iterator();
			while ( it.hasNext() ) {
				Session handle = it.next();
				DynamicProxy dproxy = ( DynamicProxy ) handle;
				AbstractJmsSessionProxy  session = (AbstractJmsSessionProxy) dproxy.getInvocationHandler();
				if ( session.isAvailable() ) it.remove();
			}
		}
	}

}
