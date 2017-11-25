/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.jdbc.AbstractConnectionProxy;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.jdbc.JdbcConnectionProxyHelper;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

/**
 *
 *
 *
 * A dynamic proxy class that wraps JDBC local connections to enable them for
 * JTA transactions and the J2EE programming model: different modules in the
 * same thread (and transaction) can get a connection from the datasource, and
 * rollback will undo all that work.
 *
 * This proxy also maintains state on behalf of the application/transaction;
 * the underlying connection can be re-pooled if two conditions evaluate to true:
 * <ul>
 * <li>There is no pending SQL work in a pending transaction; i.e. all SQL has been committed or rolled back.</li>
 * <li>There are no pending close() calls, i.e. no further SQL will arrive on the application's behalf.</li>
 * </ul>
 *
 */

class AtomikosThreadLocalConnection extends AbstractConnectionProxy
implements JtaAwareNonXaConnection
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosThreadLocalConnection.class);


	private final static List<String> ENLISTMENT_METHODS = Arrays.asList(new String[] {"createStatement", "prepareStatement", "prepareCall"});
	private final static List<String> CLOSE_METHODS = Arrays.asList(new String[] {"close"});
	private final static List<String> XA_INCOMPATIBLE_METHODS = Arrays.asList(new String[] {"commit", "rollback", "setSavepoint", "releaseSavepoint"});
	private final static List<String> NON_TRANSACTIONAL_METHOD_NAMES = Arrays.asList(new String[] {
			"equals",
			"hashCode",
			"notify",
			"notifyAll",
			"toString",
			"wait"
			});

	private static Class<?>[] MINIMUM_SET_OF_INTERFACES = {Reapable.class, DynamicProxy.class, java.sql.Connection.class };
	
	private int useCount;

	private CompositeTransaction transaction;

	private boolean stale;

	private AtomikosNonXAPooledConnection pooledConnection;

	private Connection wrapped;

	private boolean originalAutoCommitState;

	private AtomikosNonXAParticipant participant;
	// the participant; kept here to add heuristic msgs to
	// note: this will be null for non-transactional use!

	private boolean readOnly;

	private String resourceName;

	
	
	static Object newInstance ( AtomikosNonXAPooledConnection pooledConnection , String resourceName )
	{
		Object ret = null;
		Object obj = pooledConnection.getConnection();
		Set<Class<?>> interfaces = PropertyUtils.getAllImplementedInterfaces ( obj.getClass() );
		interfaces.add ( Reapable.class );
		//see case 24532
		interfaces.add ( DynamicProxy.class );
		Class<?>[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );

		List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( obj.getClass().getClassLoader() );
		classLoaders.add ( AtomikosThreadLocalConnection.class.getClassLoader() );

		ret = ClassLoadingHelper.newProxyInstance ( classLoaders , MINIMUM_SET_OF_INTERFACES , interfaceClasses , new AtomikosThreadLocalConnection ( pooledConnection ) );

		DynamicProxy dproxy = (DynamicProxy) ret;
		AtomikosThreadLocalConnection c = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
		c.resourceName = resourceName;

		return ret;
	}

	private AtomikosThreadLocalConnection ( AtomikosNonXAPooledConnection pooledConnection )
	{
		this.stale = false;
		this.useCount = 0;
		this.transaction = null;
		this.pooledConnection = pooledConnection;
		this.wrapped = pooledConnection.getConnection();
		this.readOnly = pooledConnection.getReadOnly();
	}

	private void setStale ()
	{
		this.stale = true;
	}

    private void resetForNextTransaction ()
    {

        try {
        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": resetting autoCommit to " + originalAutoCommitState );
        	//see case 24567
            wrapped.setAutoCommit ( originalAutoCommitState );
        }catch ( Exception ex ){
            LOGGER.logError ( "Failed to reset original autoCommit state: "+ex.getMessage(), ex);
        }
        setTransaction ( null );
        participant = null;

    }

    boolean isStale ()
    {
        return stale;
    }

    private void decUseCount ()
    {
        useCount--;
        markForReuseIfPossible ();
    }

    public void incUseCount ()
    {
        useCount++;
    }


    private void setTransaction ( CompositeTransaction tx )
    {
    	this.transaction = tx;
    }

    private void updateInTransaction () throws SQLException
    {
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager ();
        if ( ctm == null )
            return;

        CompositeTransaction ct = ctm.getCompositeTransaction ();
        if ( ct != null && ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {

            // if we are already in another (parent) tx then reject this,
            // because nested tx rollback can not be supported!!!
            if ( isInTransaction () && !isInTransaction ( ct ) )
                AtomikosSQLException.throwAtomikosSQLException (
                        "Connection accessed by transaction "
                                + ct.getTid ()
                                + " is already in use in another transaction: "
                                + transaction.getTid ()
                                + " Non-XA connections are not compatible with nested transaction use." );

            setTransaction ( ct );
            if ( participant == null ) {
                // make sure we add a participant for commit/rollback
                // notifications
                participant = new AtomikosNonXAParticipant ( this , resourceName );
                participant.setReadOnly ( readOnly );
                ct.addParticipant ( participant );
                originalAutoCommitState = wrapped.getAutoCommit();
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


	public Object invoke ( Object o , Method m , Object[] args )
			throws Throwable
	{


        final String methodName = m.getName();

        //see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;

		if (methodName.equals("reap")) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": reap()..." );
			reap();
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": reap done." );
			return null;
		}
		else if ( methodName.equals ("isNoLongerInUse") ) {
			return Boolean.valueOf ( isNoLongerInUse() );
		}
		else if ( methodName.equals ("isInTransaction") ) {
			return m.invoke( this , args);
		}
		else if (methodName.equals("isClosed")) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": isClosed()..." );
			Object ret = Boolean.valueOf ( isStale() );
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": isClosed() returning " + ret );
			return ret;
		}
        // detect illegal use after connection was resubmitted to the pool
		else if ( isStale () && !NON_TRANSACTIONAL_METHOD_NAMES.contains ( methodName ) ) {
        	if (!methodName.equals("close"))
        		AtomikosSQLException.throwAtomikosSQLException ( "Attempt to use connection after it was closed." );
        }
        // disallow local TX methods in global TX context
		else if ( isInTransaction() ) {
	        if (XA_INCOMPATIBLE_METHODS.contains(methodName))
	        	AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + methodName + "' while a global transaction is running");

	        if (methodName.equals("setAutoCommit") && args[0].equals(Boolean.TRUE)) {
	        	AtomikosSQLException.throwAtomikosSQLException("Cannot call 'setAutoCommit(true)' while a global transaction is running");
	        }
	        if (methodName.equals("getAutoCommit")) {
	        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getAutoCommit()..." );
	        	Object ret = Boolean.FALSE;
	        	if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": getAutoCommit() returning false." );
	        	return ret;
	        }

	        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager ();
	        CompositeTransaction ct = ctm.getCompositeTransaction();

            // if we are already in another (parent) tx then reject this,
            // because nested tx rollback can not be supported!!!
            if ( ct != null && !isInTransaction ( ct ) )
                AtomikosSQLException.throwAtomikosSQLException (
                        "Connection accessed by transaction "
                                + ct.getTid ()
                                + " is already in use in another transaction: "
                                + transaction.getTid ()
                                + " Non-XA connections are not compatible with nested transaction use." );
		}
		// check for enlistment
		else if (ENLISTMENT_METHODS.contains(methodName)) {
        	updateInTransaction();
        }

		Object ret = null;

        // check for delistment
        if (CLOSE_METHODS.contains(methodName)) {
        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": close..." );
			decUseCount();
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": close done." );
			return null;
		}
		else {
			try {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": calling " + methodName + " on vendor connection..." );
				ret =  m.invoke ( wrapped , args);

			} catch (Exception ex) {
				pooledConnection.setErroneous();
				JdbcConnectionProxyHelper.convertProxyError ( ex , "Error delegating '" + methodName + "' call" );
			}
		}
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": " + methodName + " returning " + ret );
        if ( ret instanceof Statement ) {
        	Statement s = ( Statement ) ret;
        	addStatement ( s );
        }
		return ret;

	}


	/**
	 * Checks if the connection is being used on behalf the a transaction.
	 *
	 * @return
	 */
	boolean isInTransaction()
	{
		return transaction != null;
	}

	/**
	 * Checks if the connection is being used on behalf of the given transaction.
	 *
	 * @param ct
	 * @return
	 */

	boolean isInTransaction ( CompositeTransaction ct )
	{
		boolean ret = false;
		//See case 29060 and 28683 :COPY attribute to avoid race conditions with NPE results
		CompositeTransaction tx = transaction;
		if ( tx != null && ct != null ) {
			ret = tx.isSameTransaction ( ct );
		}
		return ret;
	}

	//can the underlying connection be pooled again?
	boolean isNoLongerInUse()
	{
		return useCount <= 0 && !isInTransaction();
	}

	private void markForReuseIfPossible ()
    {

        if ( isNoLongerInUse() ) {
        	LOGGER
                    .logTrace ( "ThreadLocalConnection: detected reusability" );
            setStale();
            pooledConnection.fireOnXPooledConnectionTerminated();
        } else {
            if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "ThreadLocalConnection: not reusable yet" );
        }

    }

	private void reap() {
		LOGGER.logWarning ( this + ": reaping - check if the application closes connections correctly, or increase the reapTimeout value");
		setStale();
		useCount =0;
		markForReuseIfPossible();
		//added for BugzID 22101
		pooledConnection.setErroneous();
		forceCloseAllPendingStatements ( true );
	}

	//notification of transaction termination
	public void transactionTerminated ( boolean commit ) throws SQLException
	{

		// delegate commit or rollback to the underlying connection
        try {
            if ( commit ) {
                if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": committing on connection...");
                wrapped.commit ();

            } else {
            	// see case 84252
            	forceCloseAllPendingStatements ( false );
 				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": transaction aborting - " +
 						"pessimistically closing all pending statements to avoid autoCommit after timeout" );
            	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": rolling back on connection...");
                wrapped.rollback ();

            }
        } catch ( SQLException e ) {
            // make sure that reuse in pool is not possible
            pooledConnection.setErroneous ();
            String msg = "Error in commit on vendor connection";
            if ( ! commit ) msg = "Error in rollback on vendor connection";
            AtomikosSQLException.throwAtomikosSQLException ( msg , e );
        } finally {
            // reset attributes for next tx
            resetForNextTransaction ();
            // put connection in pool if no longer used
            // note: if erroneous then the pool will destroy the connection (cf case 30752)
            // which seems desirable to avoid pool exhaustion
            markForReuseIfPossible ();
            //see case 30752: resetting autoCommit should not be done here:
            //connection may have been reused already!
        }

	}

	public void registerXPooledConnectionEventListener ( XPooledConnectionEventListener l )
	{
		pooledConnection.registerXPooledConnectionEventListener ( l );
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append ( "atomikos non-xa connection proxy for ");
		ret.append(wrapped);
		return ret.toString();
	}

}
