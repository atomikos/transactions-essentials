package com.atomikos.jdbc;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.datasource.xa.session.InvalidSessionHandleStateException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;
import com.atomikos.util.DynamicProxySupport;
import com.atomikos.util.Proxied;

public class AtomikosJdbcConnectionProxy extends DynamicProxySupport<Connection> {

	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJdbcConnectionProxy.class);
	
	
	private final SessionHandleState sessionHandleState;
	
	public AtomikosJdbcConnectionProxy(Connection delegate, SessionHandleState sessionHandleState) {
		super(delegate);
		this.sessionHandleState = sessionHandleState;
		sessionHandleState.notifySessionBorrowed();
	}
	
	private CompositeTransactionManager getCompositeTransactionManager() {
		CompositeTransactionManager ret = Configuration.getCompositeTransactionManager();
		if ( ret == null ) LOGGER.logWarning ( this + ": WARNING: transaction manager not running?" );
		return ret;
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
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return super.invoke(proxy, method, args);
		} catch (Exception e) {
			sessionHandleState.notifySessionErrorOccurred();
			JdbcConnectionProxyHelper.convertProxyError ( e , "Error delegating '" + method.getName() + "' call" );
			return null;
		}
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
	
	@Proxied
	public Statement createStatement() throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		
		Statement s = delegate.createStatement();
		AtomikosJdbcStatementProxy ajsp = new AtomikosJdbcStatementProxy(this, s);
		Statement p  = ajsp.createDynamicProxy();
		addStatement ( s );
		return p;
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		PreparedStatement ps = delegate.prepareStatement(sql);
		addStatement ( ps );
		return ps;
	}
	
	@Proxied
	public CallableStatement prepareCall(String sql) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		CallableStatement cs = delegate.prepareCall(sql);
		addStatement(cs);
		return cs;
	}
	

	@Proxied
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			if(autoCommit) {
				AtomikosSQLException.throwAtomikosSQLException("Cannot call 'setAutoCommit(true)' while a global transaction is running");
			}	
		} else {
			delegate.setAutoCommit(autoCommit);
		}
	}
	
	@Proxied
	public boolean getAutoCommit() throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			return false;	
		}
		//else
		return delegate.getAutoCommit();
	}
	
	@Proxied
	public void commit() throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "commit" + "' while a global transaction is running");
		}
		//else
		delegate.commit();
		
	}
	
	@Proxied
	public void rollback() throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "rollback" + "' while a global transaction is running");
		}
		//else
		delegate.rollback();
		
	}
	
	@Proxied
	public void close() throws SQLException {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": close()...");
			forceCloseAllPendingStatements ( false );
			markClosed();
			sessionHandleState.notifySessionClosed();
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": closed." );
	}
	
	@Proxied
	public boolean isClosed() throws SQLException {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": isClosed() returning " + closed );
		return closed;
	}
	
	
	
	@Proxied
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return createStatement(resultSetType, resultSetConcurrency);
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}
	
	@Proxied
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
	}
	
	@Proxied
	public Savepoint setSavepoint() throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "setSavepoint()" + "' while a global transaction is running");
		}
		//else
		return delegate.setSavepoint();
	}
	
	@Proxied
	public Savepoint setSavepoint(String name) throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "setSavepoint(name)" + "' while a global transaction is running");
		}
		//else
		return delegate.setSavepoint(name);
	}
	
	@Proxied
	public void rollback(Savepoint savepoint) throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "rollback(Savepoint)" + "' while a global transaction is running");
		}
		//else
		delegate.rollback(savepoint);
		
	}
	
	@Proxied
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if(isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException("Cannot call method '" + "releaseSavepoint(savepoint)" + "' while a global transaction is running");
		}
		//else
		delegate.releaseSavepoint(savepoint);
		
	}
	
	@Proxied
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Proxied
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareStatement(sql, autoGeneratedKeys);
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareStatement(sql, columnIndexes);
	}
	
	@Proxied
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		try {
    		enlist();
    	} catch ( Exception e ) {
    		//fix for bug 25678
    		sessionHandleState.notifySessionErrorOccurred();
    		JdbcConnectionProxyHelper.convertProxyError ( e , "Error enlisting in transaction - connection might be broken? Please check the logs for more information..." );
    	}
		return delegate.prepareStatement(sql, columnNames);
	}


	@Override
	protected void throwInvocationAfterClose(String methodName) throws AtomikosSQLException {
		String msg = "Connection was already closed - calling " + methodName + " is no longer allowed!";
		AtomikosSQLException.throwAtomikosSQLException ( msg );
		
	}
	
	private class JdbcRequeueSynchronization implements Synchronization {
		private static final long serialVersionUID = 1L;

		private CompositeTransaction compositeTransaction;
		private AtomikosJdbcConnectionProxy proxy;
		private boolean afterCompletionDone;

		public JdbcRequeueSynchronization ( AtomikosJdbcConnectionProxy proxy , CompositeTransaction compositeTransaction) {
			this.compositeTransaction = compositeTransaction;
			this.proxy = proxy;
			this.afterCompletionDone = false;
		}

		public void afterCompletion(TxState state) {

			if ( afterCompletionDone ) return;


			if ( state == TxState.ABORTING  ) {
				//see bug 29708: close all pending statements to avoid reuse outside timed-out tx scope
				forceCloseAllPendingStatements ( true );
			}
			
			if ( state == TxState.TERMINATED || state.isHeuristic()) {

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
	
	private List<Statement> statements = new ArrayList<Statement>();

	protected synchronized void addStatement ( Statement s )
	{
		statements.add ( s );
	}
	
	protected synchronized void removeStatement ( Statement s )
	{
		statements.remove( s );
	}
	
	protected synchronized void forceCloseAllPendingStatements ( boolean warn ) 
	{
		Iterator<Statement> it = statements.iterator();
		while ( it.hasNext() ) {
			Statement s =  it.next();
			try {
				String msg = "Forcing close of pending statement: " + s;
				if ( warn ) LOGGER.logWarning ( msg );
				else LOGGER.logTrace ( msg );
				s.close();
			} catch ( Exception e ) {
				//ignore but log
				LOGGER.logWarning ( "Error closing pending statement: " , e );
			}
			//cf case 31275: also remove statement from list!
			it.remove();
		}
	}
}
