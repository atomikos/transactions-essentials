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

package com.atomikos.datasource.xa.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;
 
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
	private TransactionContext currentContext;
	private Set allContexts;
	private XATransactionalResource resource;
	private XAResource xaResource;
	private boolean erroneous;
	private boolean closed;
	private List sessionHandleStateChangeListeners = new ArrayList();
	
	
	public SessionHandleState ( XATransactionalResource resource , XAResource xaResource )
	{
		this.resource = resource;
		this.xaResource = xaResource;
		this.allContexts = new HashSet();
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
		Iterator it = allContexts.iterator();
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
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": notifySessionBorrowed" );
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
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": entering notifySessionClosed" );
		boolean notifyOfClosedEvent = false;
	
		synchronized ( this ) {
			boolean alreadyTerminated = isTerminated();
			Iterator it = allContexts.iterator();
			while ( it.hasNext() ) {
				TransactionContext b = ( TransactionContext ) it.next();
				if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": delegeting session close to " + b ) ;
				b.sessionClosed();
			}
			closed = true;
			if ( isTerminated() && !alreadyTerminated ) notifyOfClosedEvent = true;
		}
		//do callbacks out of synch!!!
		if ( notifyOfClosedEvent ) {
			if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": all contexts terminated, firing TerminatedEvent" );
			fireTerminatedEvent();
		}
	}
	
	/**
	 * Notification that the session handle is about to be used in the current
	 * transaction context (i.e. whatever transaction exists for the calling thread). 
	 * This method MUST be called BEFORE any work is delegated to the underlying
	 * vendor connection.
	 * @param ct The current transaction, or null if none. 
	 * @param HeuristicMessage hmsg The heuristic message, null if none.
	 * 
	 * @throws InvalidSessionHandleStateException 
	 */
	
	public synchronized void notifyBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg ) throws InvalidSessionHandleStateException
	{
		if ( closed ) throw new InvalidSessionHandleStateException ( "The underlying XA session is closed" );
		
		try {
			//first check if a suspended context exists for the current tx;
			//this happens if a transaction was suspended and now resumed
			TransactionContext suspended = null;			
			if ( ct != null ) {
				Iterator it = allContexts.iterator();
				while ( it.hasNext() && suspended == null ) {
					TransactionContext b = ( TransactionContext ) it.next();
					if ( b.isSuspendedInTransaction ( ct ) ) {
						suspended = b;
					}
				}
			}
			//check enlistment
			if ( suspended != null ) {
				if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( this + ": resuming suspended XA context for transaction " + ct.getTid() );
				currentContext = suspended;
				currentContext.transactionResumed();
			}
			else {
				//no suspended branch was found -> try to use the current branch
				try {
					if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": checking XA context for transaction " + ct );
					currentContext.checkEnlistBeforeUse ( ct , hmsg );
				}
				catch ( UnexpectedTransactionContextException txBoundaryPassed ) {
					//we are being used in a different context than expected -> suspend!
					if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo (  this + ": suspending existing XA context and creating a new one for transaction " + ct );
					currentContext.transactionSuspended();
					currentContext = new TransactionContext ( resource , xaResource );
					allContexts.add ( currentContext );
					//note: we keep all branches - if the new current branch is a Subtransaction 
					//then it will not terminate early and needs to stay around
					try {
						currentContext.checkEnlistBeforeUse ( ct , hmsg );
					} catch ( UnexpectedTransactionContextException e )  {
						String msg = "Unexpected error in session handle";
						Configuration.logWarning ( msg , e );
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
			Iterator it = allContexts.iterator();
			while ( it.hasNext() ) {
				TransactionContext b = ( TransactionContext ) it.next();
				b.transactionTerminated ( ct );
			}
			if ( isTerminated() && !alreadyTerminated ) notifyOfTerminatedEvent = true;
		}
		
		//check termination status CHANGES - only fire event once for safety!
		if ( notifyOfTerminatedEvent ) {
			if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug( this + ": all contexts terminated, firing TerminatedEvent for " + this);
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
		return "a SessionHandleState with " + allContexts.size() + " context(s)";
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
		if ( currentContext != null && tx != null ) ret = currentContext.isInactiveInTransaction ( tx );
		return ret;
	}
}
