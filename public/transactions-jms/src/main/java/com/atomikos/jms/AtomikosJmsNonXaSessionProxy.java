/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

class AtomikosJmsNonXaSessionProxy extends AbstractJmsSessionProxy 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsNonXaSessionProxy.class);

	private final static String CLOSE_METHOD = "close";
	
	private static Class<?>[] MINIMUM_SET_OF_INTERFACES = {Reapable.class, DynamicProxy.class, javax.jms.Session.class };
	
	public static Object newInstance ( Session s , SessionHandleStateChangeListener pooledConnection , SessionHandleStateChangeListener connectionProxy ) throws JMSException 
	{
        AtomikosJmsNonXaSessionProxy proxy = new AtomikosJmsNonXaSessionProxy ( s , pooledConnection , connectionProxy );
        Set<Class<?>> interfaces = PropertyUtils.getAllImplementedInterfaces ( s.getClass() );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class<?>[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( s.getClass().getClassLoader() );
		classLoaders.add ( AtomikosJmsNonXaSessionProxy.class.getClassLoader() );
		
		return ( Session ) ClassLoadingHelper.newProxyInstance ( classLoaders , MINIMUM_SET_OF_INTERFACES , interfaceClasses , proxy );
        
    }
	
	private Session delegate;
	private boolean closed = false;
	private boolean errorsOccurred = false;
	private SessionHandleStateChangeListener owner;
	private SessionHandleStateChangeListener connectionProxy;
	
	private AtomikosJmsNonXaSessionProxy ( Session s , SessionHandleStateChangeListener pooledConnection , SessionHandleStateChangeListener connectionProxy ) 
	{
		this.delegate = s;
		this.owner = pooledConnection;
		this.connectionProxy = connectionProxy;
	}
	
	private void checkForTransactionContextAndLogWarningIfSo() 
	{
		TransactionManager tm = TransactionManagerImp.getTransactionManager();
		if ( tm != null ) {
			Transaction tx = null;
			try {
				tx = tm.getTransaction();
			} catch (SystemException e) {
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": Failed to get transaction."  , e );
				//ignore
			}
			if ( tx != null ) {
				String msg =  this + ": WARNING - detected JTA transaction context while using non-transactional session." + "\n" +
						"Beware that any JMS operations you perform are NOT part of the JTA transaction." + "\n" +
						"To enable JTA, make sure to do all of the following:" + "\n" +
						"1. Make sure that the AtomikosConnectionFactoryBean is configured with localTransactionMode=false, and" + "\n" +
						"2. Make sure to call create JMS sessions with the transacted flag set to true.";
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( msg );
			}
		}
	}
	
	//threaded: invoked by application thread as well as by pool maintenance thread
	public Object invoke ( Object proxy, Method method, Object[] args ) throws JMSException 
	{
		String methodName = method.getName();
		
		//see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;
		
		//synchronized only now to avoid deadlock - cf case 33703
		synchronized ( this ) {
			if (closed) {
				if (!methodName.equals(CLOSE_METHOD)) {
					String msg = "Session was closed already - calling " + methodName + " is no longer allowed.";
					LOGGER.logWarning ( this + ": " + msg );
					throw new javax.jms.IllegalStateException(msg);
				}
				return null;
			}

			if ( CLOSE_METHOD.equals ( methodName ) ) {
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": close...");
				destroy();
				return null;
			}

			checkForTransactionContextAndLogWarningIfSo();

			try {
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": calling " + methodName + " on vendor session..." );
				Object ret =  method.invoke(delegate, args);
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": " + methodName + " returning " + ret );
				return ret;
			} catch (Exception ex) {
				errorsOccurred = true;
				String msg =  "Error delegating " + methodName + " call to JMS driver";
				convertProxyError ( ex , msg );
			}
		}
		//dummy return to make compiler happy
		return null;
	}
	


	protected void destroy() {
		try {
			if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": destroying session...");
			if ( !closed ) {
				closed = true;
				delegate.close(); 
				owner.onTerminated();
				connectionProxy.onTerminated();
			}
		} catch  ( JMSException e ) {
			LOGGER.logWarning ( this + ": could not close JMS session" , e );
		}
	
	}


	protected boolean isAvailable() {
		return closed;
	}


	protected boolean isErroneous() {
		return errorsOccurred;
	}


	protected boolean isInTransaction ( CompositeTransaction ct ) {
		return false;
	}

	
	public String toString()
	{
		return "atomikos non-xa session proxy for vendor instance " + delegate;
	}

	



}
