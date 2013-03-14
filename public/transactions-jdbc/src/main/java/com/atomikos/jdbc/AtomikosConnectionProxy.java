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

package com.atomikos.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.session.InvalidSessionHandleStateException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;
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

	private final Connection delegate;
	private SessionHandleState sessionHandleState;
	private boolean closed = false;
	private boolean reaped = false;
	private HeuristicMessage hmsg;

	private String toString;

	private AtomikosConnectionProxy ( Connection c, SessionHandleState sessionHandleState , HeuristicMessage hmsg )
	{
		this.delegate = c;
		this.sessionHandleState = sessionHandleState;
		this.hmsg = hmsg;
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
		boolean jtaTxFound = false;

		//see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;

		if (methodName.equals("reap")) {
			if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": reaping pending connection..." );

			//LOGGER.logDebug("{} : reaping pending connection..", this);
			reap();
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": reap done!" );
			return null;
		}
		if (methodName.equals("isClosed")) {
			if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": isClosed()..." );
			Object ret = Boolean.valueOf(closed);
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": isClosed() returning " + ret );
			return ret;
		}

		if ( closed && !methodName.equals("close") ) {
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
        		jtaTxFound = enlist();
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
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": calling " + formatCallDetails(method,args) + "...");
				ret = method.invoke(delegate, args);

			} catch (Exception ex) {
				sessionHandleState.notifySessionErrorOccurred();
				JdbcConnectionProxyHelper.convertProxyError ( ex , "Error delegating '" + methodName + "' call" );
			}
		}
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": " + methodName + " returning " + ret );
        if ( ret instanceof Statement ) {
        	//keep statement for closing upon timeout/close
        	//see bug 29708
        	Statement s = ( Statement ) ret;
        	addStatement ( s );
        }
        return ret;
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
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( this + ": notifyBeforeUse " + sessionHandleState);
			CompositeTransaction ct = null;
			CompositeTransactionManager ctm = getCompositeTransactionManager();
			if ( ctm != null ) {
				ct = ctm.getCompositeTransaction();
				//first notify the session handle - see case 27857
				sessionHandleState.notifyBeforeUse ( ct , hmsg );
				if (ct != null && ct.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {
					ret = true;
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": detected transaction " + ct );
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
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": close()...");
		forceCloseAllPendingStatements ( false );
		closed = true;
		sessionHandleState.notifySessionClosed();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": closed." );
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

	public static Reapable newInstance ( Connection c , SessionHandleState sessionHandleState , HeuristicMessage hmsg )
	{
		Reapable ret = null;
        AtomikosConnectionProxy proxy = new AtomikosConnectionProxy(c, sessionHandleState , hmsg );
        Set<Class> interfaces = PropertyUtils.getAllImplementedInterfaces ( c.getClass() );
        interfaces.add ( Reapable.class );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );

		Set<Class> minimumSetOfInterfaces = new HashSet<Class>();
		minimumSetOfInterfaces.add ( Reapable.class );
		minimumSetOfInterfaces.add ( DynamicProxy.class );
		minimumSetOfInterfaces.add ( java.sql.Connection.class );
        Class[] minimumSetOfInterfaceClasses = ( Class[] ) minimumSetOfInterfaces.toArray( new Class[0] );

        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( c.getClass().getClassLoader() );
		classLoaders.add ( AtomikosConnectionProxy.class.getClassLoader() );


		ret = ( Reapable ) ClassLoadingHelper.newProxyInstance ( classLoaders , minimumSetOfInterfaceClasses , interfaceClasses , proxy );

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

		public void afterCompletion(Object state) {

			if ( afterCompletionDone ) return;


			if ( state.equals ( TxState.ABORTING ) ) {
				//see bug 29708: close all pending statements to avoid reuse outside timed-out tx scope
				forceCloseAllPendingStatements ( true );
			}

			if ( state.equals ( TxState.TERMINATED )
	                || state.equals ( TxState.HEUR_MIXED )
	                || state.equals ( TxState.HEUR_HAZARD )
	                || state.equals ( TxState.HEUR_ABORTED )
	                || state.equals ( TxState.HEUR_COMMITTED ) ) {

	            // connection is reusable!
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug(  proxy + ": detected termination of transaction " + compositeTransaction );
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
