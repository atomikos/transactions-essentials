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

package com.atomikos.jms;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

class AtomikosJmsNonXaSessionProxy extends AbstractJmsSessionProxy 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsNonXaSessionProxy.class);

	private final static String CLOSE_METHOD = "close";
	
	public static Object newInstance ( Session s , SessionHandleStateChangeListener pooledConnection , SessionHandleStateChangeListener connectionProxy ) throws JMSException 
	{
        AtomikosJmsNonXaSessionProxy proxy = new AtomikosJmsNonXaSessionProxy ( s , pooledConnection , connectionProxy );
        Set interfaces = PropertyUtils.getAllImplementedInterfaces ( s.getClass() );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        
        Set minimumSetOfInterfaces = new HashSet();
		minimumSetOfInterfaces.add ( Reapable.class );
		minimumSetOfInterfaces.add ( DynamicProxy.class );
		minimumSetOfInterfaces.add ( javax.jms.Session.class );
        Class[] minimumSetOfInterfaceClasses = ( Class[] ) minimumSetOfInterfaces.toArray( new Class[0] );
        
        
        List classLoaders = new ArrayList();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( s.getClass().getClassLoader() );
		classLoaders.add ( AtomikosJmsNonXaSessionProxy.class.getClassLoader() );
		
		return ( Session ) ClassLoadingHelper.newProxyInstance ( classLoaders , minimumSetOfInterfaceClasses , interfaceClasses , proxy );
        
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
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": Failed to get transaction."  , e );
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
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": " + methodName + " returning " + ret );
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
