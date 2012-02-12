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

package com.atomikos.jdbc.nonxa;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.PooledConnection;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 *
 * A dynamic proxy class that wraps JDBC local connections to enable them for
 * JTA transactions and the J2EE programming model: different modules in the
 * same thread (and transaction) can get a connection from the datasource, and
 * rollback will undo all that work.
 *
 *
 *
 */

class ThreadLocalConnection implements InvocationHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(ThreadLocalConnection.class);

	private final static List NON_TRANSACTIONAL_METHOD_NAMES = Arrays.asList(new String[] {
			"equals",
			"hashCode",
			"notify",
			"notifyAll",
			"toString",
			"wait"
			});


    private int useCount;
    // incremented by 1 for every time this connection is gotten

    private Connection wrapped;
    // the wrapped JDBC Connection

    private NonXAPooledConnectionImp pooledConnection;
    // the PooledConnection to which we belong

    private CompositeTransaction transaction;
    // not null iff we are in an active tx

    private boolean stale;
    // true if underlying connection is back in pool

    private NonXAParticipant participant;

    // the participant; kept here to add heuristic msgs to
    // note: this will be null for non-transactional use!

    static Object newInstance ( NonXAPooledConnectionImp pooledConnection )
            throws SQLException
    {
        Object obj = pooledConnection.getConnection ();
        Set interfaces = getAllImplementedInterfaces ( obj.getClass() );
        Class[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        return java.lang.reflect.Proxy.newProxyInstance ( obj.getClass ()
                .getClassLoader (), interfaceClasses,
                new ThreadLocalConnection ( pooledConnection ) );
    }

    //FIXED ISSUE 10080
    static Set getAllImplementedInterfaces ( Class clazz )
    {
    		Set ret = null;

    		if ( clazz.getSuperclass() != null ) {
    			//if superclass exists: first add the superclass interfaces!!!
    			ret = getAllImplementedInterfaces ( clazz.getSuperclass() );
    		}
    		else {
    			//no superclass: start with empty set
    			ret = new HashSet();
    		}

    		//add the interfaces in this class
    		Class[] interfaces = clazz.getInterfaces();
    		for ( int i = 0 ; i < interfaces.length ; i++ ) {
    			ret.add ( interfaces[i] );
    		}

    		return ret;
    }

    /**
     * Private constructor: instances should be gotten through factory method.
     */

    private ThreadLocalConnection ( NonXAPooledConnectionImp pooledConnection )
            throws SQLException
    {
        useCount = 0;
        this.wrapped = pooledConnection.getConnection ();
        this.pooledConnection = pooledConnection;
        setStale ( false );
        resetForTransaction ();
        updateInTransaction ();
    }

    private void resetForTransaction ()
    {

        setTransaction ( null );
        participant = null;

    }

    private void setStale ( boolean val )
    {
        stale = val;
    }

    boolean isStale ()
    {
        return stale;
    }

    private void setTransaction ( CompositeTransaction val )
    {
        transaction = val;

    }

    private void updateInTransaction () throws SQLException
    {
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();
        if ( ctm == null )
            return;

        CompositeTransaction ct = ctm.getCompositeTransaction ();
        if ( ct != null && ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {

            // if we are already in another (parent) tx then reject this,
            // because nested tx rollback can not be supported!!!
            if ( isInTransaction () && !isInTransaction ( ct ) )
                throw new SQLException (
                        "Connection accessed by transaction "
                                + ct.getTid ()
                                + " is already in use in another transaction: "
                                + transaction.getTid ()
                                + " Non-XA connections are not compatible with nested transaction use." );

            setTransaction ( ct );
            if ( participant == null ) {
                // System.out.println ( "Adding participant");
                // make sure we add a participant for commit/rollback
                // notifications
                participant = new NonXAParticipant ( this );
                ct.addParticipant ( participant );

                wrapped.setAutoCommit ( false );

            }
        } else {
            // the current thread has NO tx, which means none was started (OK)
            // or
            // the previous one was terminated by the application. In that case,
            // check that there was no ACTIVE rollback on timeout (meaning that
            // transactionTerminated was never called!!!)
            if ( isInTransaction () )
                transactionTerminated ( false );
        }
    }

    private boolean isInTransaction ()
    {
        return transaction != null;
    }

    private boolean isInTransaction ( CompositeTransaction ct )
    {
        boolean ret = false;
        if ( isInTransaction () ) {
            ret = transaction.isSameTransaction ( ct );
        }
        return ret;
    }

    private void decUseCount ()
    {

        useCount--;
        checkReusability ();
    }

    private void checkReusability ()
    {

        if ( useCount == 0 && !isInTransaction () ) {
            Configuration
                    .logDebug ( "ThreadLocalConnection: detected reusability" );
            setStale ( true );
            pooledConnection.notifyCloseListeners ();
        } else {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "ThreadLocalConnection: not reusable yet" );
        }

    }

    void addHeuristicMessage ( HeuristicMessage msg )
    {
        if ( participant != null ) {
            participant.addHeuristicMessage ( msg );
        }
    }

    void incUseCount ()
    {
        useCount++;
    }

    void transactionTerminated ( boolean commit ) throws SQLException
    {
        // delegate commit or rollback to the underlying connection
        try {
            if ( commit ) {
                if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "ThreadLocalConnection: committing on connection...");
                wrapped.commit ();
            } else {
            	   if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "ThreadLocalConnection: rolling back on connection...");
                wrapped.rollback ();
            }
        } catch ( SQLException e ) {
            // make sure that reuse in pool is not possible
            pooledConnection.setInvalidated ();
            // e.printStackTrace();
            throw e;
        } finally {
            // reset attributes for next tx
            resetForTransaction ();
            // put connection in pool if no longer used
            checkReusability ();

        }
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke ( Object o , Method m , Object[] args )
            throws Throwable
    {
        if (NON_TRANSACTIONAL_METHOD_NAMES.contains(m.getName())) {
        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ("Calling non-transactional method '" + m.getName() + "' on ThreadLocal connection, bypassing enlistment");
        	return m.invoke ( wrapped, args );
        }


        Object result = null;

        // detect illegal use after connection was resubmitted to the pool
        if ( isStale () )
            throw new SQLException (
                    "Attempt to use connection after it was closed." );

        // because connections can be gotten BEFORE a transaction is started,
        // this is
        // the place to check whether we are being used transactionally or not
        updateInTransaction ();

        if ( m.getName ().equals ( "close" ) ) {
            decUseCount ();
        } else if ( isInTransaction ()
                && (m.getName ().equals ( "rollback" ) || m.getName ().equals (
                        "commit" )) ) {
            // transactional use forbids connection-level rollback or commit
            throw new SQLException (
                    "Rollback or Commit not allowed inside a managed transaction!" );
        } else {
            // delegate to the underlying JDBC connection
            try {
                // System.out.println ( "Proxy: calling method" );
            	   if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "ThreadLocalConnection: delegating method " + m +
            			   " to wrapped connection with args: " + args );
                result = m.invoke ( wrapped, args );


            } catch ( InvocationTargetException e ) {

                throw e.getTargetException ();
            } catch ( Exception e ) {
                Configuration
                        .logDebug (
                                "ThreadLocalConnection: Unexpected invocation exception",
                                e );
                throw new RuntimeException (
                        "Unexpected invocation exception: " + e.getMessage () );
            }

        }
        return result;
    }

    public boolean usesConnection ( PooledConnection conn )
    {
        return pooledConnection.equals ( conn );
    }

}
