/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

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
import com.atomikos.datasource.xa.session.InvalidSessionHandleStateException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

class AtomikosConnectionProxy extends AbstractConnectionProxy
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosConnectionProxy.class);

	private final static List<String> ENLISTMENT_METHODS = Arrays.asList(new String[] {"createStatement", "prepareStatement", "prepareCall"});
	private final static List<String> CLOSE_METHODS = Arrays.asList(new String[] {"close"});
	private final static List<String> XA_INCOMPATIBLE_METHODS = Arrays.asList(new String[] {"commit", "rollback", "setSavepoint", "releaseSavepoint"});

	private static Class<?>[] MINIMUM_SET_OF_INTERFACES = {Reapable.class, DynamicProxy.class,java.sql.Connection.class };
	
	private final Connection delegate;
	private SessionHandleState sessionHandleState;
	private boolean closed = false;
	private boolean reaped = false;

	private String toString;

	private AtomikosConnectionProxy ( Connection c, SessionHandleState sessionHandleState)
	{
		this.delegate = c;
		this.sessionHandleState = sessionHandleState;
		sessionHandleState.notifySessionBorrowed();
	}

	public String toString()
	{
		if(toString==null){
			StringBuffer ret = new StringBuffer();
			ret.append ( "atomikos connection proxy for ");
			ret.append (delegate);
			toString= ret.toString();
		}
		return toString;
	}

	//no longer synchronized: see case 22101
	public Object invoke ( Object proxy, Method method, Object[] args ) throws SQLException
	{
		final String methodName = method.getName();

		//see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;

		if (methodName.equals("reap")) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": reaping pending connection..." );

			//LOGGER.logDebug("{} : reaping pending connection..", this);
			reap();
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": reap done!" );
			return null;
		}
		if (methodName.equals("isClosed")) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": isClosed()..." );
			Object ret = Boolean.valueOf(closed);
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": isClosed() returning " + ret );
			return ret;
		}

		if ( closed && !methodAllowedAfterClose(method) ) {
			if ( reaped ) {
				//'reaped' is a system-triggered variant of closed -> throw exception that explains this
				String msg = "Connection has been reaped - calling " + methodName + " is no longer allowed! Increase reapTimeout to avoid this problem.";
				AtomikosSQLException.throwAtomikosSQLException ( msg );
			} else {
				String msg = "Connection was already closed - calling " + methodName + " is no longer allowed!";
				AtomikosSQLException.throwAtomikosSQLException ( msg );
			}
			return null;
		}


		// disallow local TX methods in global TX context
		if (isEnlistedInGlobalTransaction()) {
	        if (XA_INCOMPATIBLE_METHODS.contains(methodName))
	        	AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + methodName + "' while a global transaction is running");

	        if (methodName.equals("setAutoCommit") && args[0].equals(Boolean.TRUE)) {
	        	AtomikosSQLException.throwAtomikosSQLException("Cannot call 'setAutoCommit(true)' while a global transaction is running");
	        }
	        if (methodName.equals("getAutoCommit")) {
	        	return Boolean.FALSE;
	        }
		}

		// check for enlistment
        if (ENLISTMENT_METHODS.contains(methodName)) {
        	try {
        		enlist();
        	} catch ( Exception e ) {
        		//fix for bug 25678
        		sessionHandleState.notifySessionErrorOccurred();
        		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
        	}
        }

        Object ret = null;

        // check for delistment
        if (CLOSE_METHODS.contains(methodName) && args == null ) {
        	//check for args needed by case 24683
 			close();
 			return null;
 		}
		else {
			try {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": calling " + formatCallDetails(method,args) + "...");
				ret = method.invoke(delegate, args);

			} catch (Exception ex) {
				sessionHandleState.notifySessionErrorOccurred();
				JdbcConnectionProxyHelper.convertProxyError ( ex , "Error delegating '" + methodName + "' call" );
			}
		}
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": " + methodName + " returning " + ret );
        if ( ret instanceof Statement ) {
        	//keep statement for closing upon timeout/close
        	//see bug 29708
        	Statement s = ( Statement ) ret;
        	addStatement ( s );
        }
        return ret;
	}


	private boolean methodAllowedAfterClose(Method method) {
		return method.getName().equals("close") || ClassLoadingHelper.existsInJavaObjectClass(method);
	}

	private String formatCallDetails(Method method, Object[] args) {
		StringBuffer ret = new StringBuffer();
		ret.append(method.getName());
		if (args != null && args.length>0) {
			ret.append("(");
			for (int i = 0; i < args.length; i++) {
				ret.append(args[i].toString());
				if (i < args.length-1) ret.append(",");
			}
			ret.append(")");
		}
		return ret.toString();
	}

	private void reap() {
		LOGGER.logWarning ( this + ": reaping - check if the application closes connections correctly, or increase the reapTimeout value");
		close();
		//added for 22101
		sessionHandleState.notifySessionErrorOccurred();
		reaped = true;
	}

	private CompositeTransactionManager getCompositeTransactionManager() {
		CompositeTransactionManager ret = Configuration.getCompositeTransactionManager();
		if ( ret == null ) LOGGER.logWarning ( this + ": WARNING: transaction manager not running?" );
		return ret;
	}
	

	/**
	 * Enlist if necessary
	 * @return True if a JTA transaction was found, false otherwise.
	 *
	 * @throws AtomikosSQLException
	 */
	private boolean enlist() throws AtomikosSQLException {
		boolean ret = false;
		try {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": notifyBeforeUse " + sessionHandleState);
			CompositeTransaction ct = null;
			CompositeTransactionManager ctm = getCompositeTransactionManager();
			if ( ctm != null ) {
				ct = ctm.getCompositeTransaction();
				//first notify the session handle - see case 27857
				sessionHandleState.notifyBeforeUse ( ct );
				if (ct != null && ct.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {
					ret = true;
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": detected transaction " + ct );
					if ( ct.getState().equals(TxState.ACTIVE) ) ct.registerSynchronization(new JdbcRequeueSynchronization( this , ct ));
					else AtomikosSQLException.throwAtomikosSQLException("The transaction has timed out - try increasing the timeout if needed");
				}
			}

		} catch (InvalidSessionHandleStateException ex) {
			AtomikosSQLException.throwAtomikosSQLException ( ex.getMessage() , ex);
		}
		return ret;
	}

	private void close() {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": close()...");
		forceCloseAllPendingStatements ( false );
		closed = true;
		sessionHandleState.notifySessionClosed();
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": closed." );
	}

	private boolean isEnlistedInGlobalTransaction()
	{
		CompositeTransactionManager compositeTransactionManager = getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			return false; // TM is not running, we can only be in local TX mode
		}
		CompositeTransaction ct = compositeTransactionManager.getCompositeTransaction();
		return sessionHandleState.isActiveInTransaction ( ct );
	}
	
	

	public static Reapable newInstance ( Connection c , SessionHandleState sessionHandleState )
	{
		Reapable ret = null;
        AtomikosConnectionProxy proxy = new AtomikosConnectionProxy(c, sessionHandleState );
        Set<Class<?>> interfaces = PropertyUtils.getAllImplementedInterfaces ( c.getClass() );
        interfaces.add ( Reapable.class );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class<?>[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );

        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( c.getClass().getClassLoader() );
		classLoaders.add ( AtomikosConnectionProxy.class.getClassLoader() );

		ret = ( Reapable ) ClassLoadingHelper.newProxyInstance ( classLoaders , MINIMUM_SET_OF_INTERFACES , interfaceClasses , proxy );

        return ret;
    }

	private class JdbcRequeueSynchronization implements Synchronization {
		private static final long serialVersionUID = 1L;

		private CompositeTransaction compositeTransaction;
		private AbstractConnectionProxy proxy;
		private boolean afterCompletionDone;

		public JdbcRequeueSynchronization ( AbstractConnectionProxy proxy , CompositeTransaction compositeTransaction) {
			this.compositeTransaction = compositeTransaction;
			this.proxy = proxy;
			this.afterCompletionDone = false;
		}

		public void afterCompletion(TxState state) {

			if ( afterCompletionDone ) return;


			if ( state.equals ( TxState.ABORTING ) ) {
				//see bug 29708: close all pending statements to avoid reuse outside timed-out tx scope
				forceCloseAllPendingStatements ( true );
			}

			if ( state.equals ( TxState.TERMINATED ) || state.isHeuristic()) {

	            // connection is reusable!
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace(  proxy + ": detected termination of transaction " + compositeTransaction );
				sessionHandleState.notifyTransactionTerminated(compositeTransaction);

	            afterCompletionDone = true;

	            // see case 73007 and 84252
	            forceCloseAllPendingStatements ( false );
	        }


		}

		public void beforeCompletion() {
		}

		//override equals: synchronizations for the same tx are equal
		//to avoid receiving double notifications on termination!
		public boolean equals ( Object other )
		{
			boolean ret = false;
			if ( other instanceof JdbcRequeueSynchronization ) {
				JdbcRequeueSynchronization o = ( JdbcRequeueSynchronization ) other;
				ret = this.compositeTransaction.isSameTransaction ( o.compositeTransaction );
			}
		    return ret;
		}

		public int hashCode()
		{
			return compositeTransaction.hashCode();
		}
	}

}
