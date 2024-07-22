/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
 
 /**
  * 
  * 
  * A reusable state tracker for XA session/connection handles.
  * An instance of this class can be used for automatically tracking the 
  * enlistment and termination states of all branches
  * that a connection handle is involved in. It does this by switching states
  * behind the scenes, so the same instance can be used for several branches.
  */

public class SessionHandleState 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(SessionHandleState.class);

	private TransactionContext currentContext;
	private Set<TransactionContext> allContexts;
	private XATransactionalResource resource;
	private XAResource xaResource;
	private boolean erroneous;
	private boolean closed;
	private List<SessionHandleStateChangeListener> sessionHandleStateChangeListeners = new ArrayList<SessionHandleStateChangeListener>();
	
	
	public SessionHandleState ( XATransactionalResource resource , XAResource xaResource )
	{
		this.resource = resource;
		this.xaResource = xaResource;
		this.allContexts = new HashSet<TransactionContext>();
		this.erroneous = false;
		this.closed = true;
	}
	
	/**
	 * Checks if the session handle is terminated (i.e., can be discarded) and the
	 * underlying vendor xa connection/session can be reused or destroyed. 
	 * 
	 * @return True if the underlying vendor connection can be reused or destroyed. 
	 * The session handle itself (i.e., the Atomikos proxy) should be discarded.
	 */
	
	public synchronized boolean isTerminated()
	{
		boolean terminated = true;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace("isTerminated: checking " + allContexts.size() + " contexts...");
		}
		Iterator<TransactionContext> it = allContexts.iterator();
		while ( it.hasNext() ) {
			TransactionContext b = ( TransactionContext ) it.next();
			if ( b.isTerminated() ) {
				it.remove();
			}
			else terminated = false;
		}
		
		if ( terminated ) currentContext = null;
		
		return terminated;
	}
	
	/**
	 * Notification that the session was gotten from the pool.
	 * 
	 * 
	 */
	public synchronized void notifySessionBorrowed()
	{
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": notifySessionBorrowed" );
		currentContext = new TransactionContext ( resource , xaResource );
		allContexts.add ( currentContext );
		closed = false;
	}
	
	/**
	 * Notification that the session handle has been closed by 
	 * the application. 
	 *
	 */
	
	public void notifySessionClosed()
	{
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": entering notifySessionClosed" );
		boolean notifyOfClosedEvent = false;
	
		synchronized ( this ) {
			boolean alreadyTerminated = isTerminated();
			Iterator<TransactionContext> it = allContexts.iterator();
			while ( it.hasNext() ) {
				TransactionContext b =  it.next();
				if ( LOGGER.isTraceEnabled() ) {
					LOGGER.logTrace ("delegating session close to " + b ); // avoid "this" / toString - see case 201164
				}
				b.sessionClosed();
			}
			closed = true;
			if ( isTerminated() && !alreadyTerminated ) notifyOfClosedEvent = true;
		}
		//do callbacks out of synch!!!
		if ( notifyOfClosedEvent ) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": all contexts terminated, firing TerminatedEvent" );
			fireTerminatedEvent();
		}
	}
	
	/**
	 * Notification that the session handle is about to be used in the current
	 * transaction context (i.e. whatever transaction exists for the calling thread). 
	 * This method MUST be called BEFORE any work is delegated to the underlying
	 * vendor connection.
	 * @param ct The current transaction, or null if none. 
	 * 
	 * @throws InvalidSessionHandleStateException 
	 */
	
	public synchronized void notifyBeforeUse ( CompositeTransaction ct ) throws InvalidSessionHandleStateException
	{
		if ( closed ) throw new InvalidSessionHandleStateException ( "The underlying XA session is closed" );
		
		try {
			//first check if a suspended context exists for the current tx;
			//this happens if a transaction was suspended and now resumed
			TransactionContext suspended = null;			
			if ( ct != null ) {
				Iterator<TransactionContext> it = allContexts.iterator();
				while ( it.hasNext() && suspended == null ) {
					TransactionContext b = ( TransactionContext ) it.next();
					if ( b.isSuspendedInTransaction ( ct ) ) {
						suspended = b;
					}
				}
			}
			//check enlistment
			if ( suspended != null ) {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": resuming suspended XA context for transaction " + ct.getTid() );
				currentContext = suspended;
				currentContext.transactionResumed();
			}
			else {
				//no suspended branch was found -> try to use the current branch
				try {
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": checking XA context for transaction " + ct );
					currentContext.checkEnlistBeforeUse ( ct );
				}
				catch ( UnexpectedTransactionContextException txBoundaryPassed ) {
					//we are being used in a different context than expected -> suspend!
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  this + ": suspending existing XA context and creating a new one for transaction " + ct );
					currentContext.transactionSuspended();
					currentContext = new TransactionContext ( resource , xaResource );
					allContexts.add ( currentContext );
					//note: we keep all branches - if the new current branch is a Subtransaction 
					//then it will not terminate early and needs to stay around
					try {
						currentContext.checkEnlistBeforeUse ( ct );
					} catch ( UnexpectedTransactionContextException e )  {
						String msg = "Unexpected error in session handle";
						LOGGER.logError ( msg , e );
						throw new InvalidSessionHandleStateException ( msg );
					}
				}
			}
		} catch ( InvalidSessionHandleStateException e ) {
			//avoid reuse in pool
			notifySessionErrorOccurred();
			throw e;
		}
		
	}
	
	/**
	 * Checks if the session has had any errors.
	 * This method can be used to decide whether or not to reuse the underlying 
	 * vendor connection in the pool.
	 * 
	 * @return True if sessionErrorOccurred has been called, false if not.
	 */
	
	public boolean isErroneous()
	{
		return erroneous;
	}
	
	/**
	 * Marks this session as erroneous. This has no other effect than that
	 * isErroneous returns true. 
	 *
	 */
	
	public void notifySessionErrorOccurred()
	{
		this.erroneous = true;
	}
	
	/**
	 * Notifies the session that the transaction was terminated.
	 * 
	 * @param ct
	 */
	
	public void notifyTransactionTerminated ( CompositeTransaction ct ) 
	{
		
		boolean notifyOfTerminatedEvent = false;
		synchronized ( this ) {
			boolean alreadyTerminated = isTerminated();
			Iterator<TransactionContext> it = allContexts.iterator();
			while ( it.hasNext() ) {
				TransactionContext b = it.next();
				b.transactionTerminated ( ct );
			}
			if ( isTerminated() && !alreadyTerminated ) notifyOfTerminatedEvent = true;
		}
		
		//check termination status CHANGES - only fire event once for safety!
		if ( notifyOfTerminatedEvent ) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( this + ": all contexts terminated, firing TerminatedEvent for " + this);
			fireTerminatedEvent();
		}
	}

	
	public void registerSessionHandleStateChangeListener(SessionHandleStateChangeListener listener) 
	{
		sessionHandleStateChangeListeners.add(listener);
	}
	
	public void unregisterSessionHandleStateChangeListener(SessionHandleStateChangeListener listener) 
	{
		sessionHandleStateChangeListeners.remove(listener);
	}

	private void fireTerminatedEvent() 
	{
		for (int i=0; i<sessionHandleStateChangeListeners.size() ;i++) {
			SessionHandleStateChangeListener listener = (SessionHandleStateChangeListener) sessionHandleStateChangeListeners.get(i);
			listener.onTerminated();
		}
	}
	
	public String toString() 
	{
		return "sessionHandleState (" + allContexts.size() + " context(s), isTerminated = " + isTerminated() + ") for resource " + resource.getName();
	}
	
	/**
	 * Tests if the session is active (enlisted) in the given transaction.
	 * @param tx
	 * @return
	 */
	public boolean isActiveInTransaction ( CompositeTransaction  tx ) 
	{
		boolean ret = false;
		if ( currentContext != null && tx != null ) ret = currentContext.isInTransaction ( tx );
		return ret;
	}

	/**
	 * Tests if the session is inactive (delisted) for the given transaction.
	 * @param tx
	 * @return
	 */
	public boolean isInactiveInTransaction( CompositeTransaction tx ) 
	{
		boolean ret = false;
		//if (closed) { // see case 159940: if not closed then be pessimistic and assume still active in terms of recycling
			if ( currentContext != null && tx != null ) ret = currentContext.isInactiveInTransaction ( tx );
		//}
		return ret;
	}
}
