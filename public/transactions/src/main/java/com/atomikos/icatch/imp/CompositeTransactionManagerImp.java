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

package com.atomikos.icatch.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Reusable (generic) composite transaction manager implementation.
 */

public class CompositeTransactionManagerImp implements CompositeTransactionManager,
        SubTxAwareParticipant
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CompositeTransactionManagerImp.class);
	
	private Map<Thread, Stack<CompositeTransaction>> threadtotxmap_ = null;
    private Map<CompositeTransaction, Thread> txtothreadmap_ = null;


    public CompositeTransactionManagerImp ()
    {
        threadtotxmap_ = new HashMap<Thread, Stack<CompositeTransaction>> ();
        txtothreadmap_ = new HashMap<CompositeTransaction, Thread> ();
    }



    /**
     * Get the thread associated with a given transaction.
     */

    private Thread getThread ( CompositeTransaction ct )
    {
        Thread thread = null;

        synchronized ( txtothreadmap_ ) {
            thread = (Thread) txtothreadmap_.get ( ct );
        }
        return thread;
    }

    /**
     * Remove mappings for given thread.
     *
     * @return Stack The tx stack that was for current thread.
     */

    private Stack<CompositeTransaction> removeThreadMappings ( Thread thread )
    {

        Stack<CompositeTransaction> ret = null;
        synchronized ( threadtotxmap_ ) {
            ret = threadtotxmap_.remove ( thread );
            CompositeTransaction tx = ret.peek ();
            txtothreadmap_.remove ( tx );
        }
        return ret;
    }

    /**
     * Set mappings for current thread.
     *
     * @param ct
     *            The transaction to map to. Also sets the coordinator mapping
     *            by getting ct's coordinator.
     */

    private void setThreadMappings ( CompositeTransaction ct , Thread thread )
            throws IllegalStateException, SysException
    {
        //case 21806: callbacks to ct to be made outside synchronized block
    	ct.addSubTxAwareParticipant ( this ); //step 1

        synchronized ( threadtotxmap_ ) {
        	//between step 1 and here, intermediate timeout/rollback of the ct
        	//may have happened; make sure to check or we add a thread mapping
        	//that will never be removed!
        	if ( TxState.ACTIVE.equals ( ct.getState() )) {
        		Stack<CompositeTransaction> txs = threadtotxmap_.get ( thread );
        		if ( txs == null )
        			txs = new Stack<CompositeTransaction>();
        		txs.push ( ct );
        		threadtotxmap_.put ( thread, txs );
        		txtothreadmap_.put ( ct, thread );
        	}
        }


    }

    private void restoreThreadMappings ( Stack<CompositeTransaction> stack , Thread thread )
            throws IllegalStateException
    {
    	//case 21806: callbacks to ct to be made outside synchronized block
    	CompositeTransaction tx = stack.peek ();
    	tx.addSubTxAwareParticipant ( this ); //step 1

        synchronized ( threadtotxmap_ ) {
        	//between step 1 and here, intermediate timeout/rollback of the ct
        	//may have happened; make sure to check or we add a thread mapping
        	//that will never be removed!
        	TxState state = tx.getState();
        	
        	if ( state.isOneOf(TxState.ACTIVE, TxState.MARKED_ABORT) ) {
        		//also resume for marked abort - see case 26398
        		Stack<CompositeTransaction> txs = threadtotxmap_.get ( thread );
        		if ( txs != null ) throw new IllegalStateException ("Thread already has subtx stack" );
        		threadtotxmap_.put ( thread, stack );
        		txtothreadmap_.put ( tx, thread );
        	}
        }
    }

    private CompositeTransaction getCurrentTx ()
    {
        Thread thread = Thread.currentThread ();
        synchronized ( threadtotxmap_ ) {
            Stack<CompositeTransaction> txs = threadtotxmap_.get ( thread );
            if ( txs == null )
                return null;
            else
                return txs.peek ();

        }
    }



    /**
     * Get the participant for the given root. Needed for recovery of JCA
     * inbound transactions.
     *
     * @param root
     * @return The participant.
     */
    public Participant getParticipant ( String root )
    {
        return getTransactionService().getParticipant ( root );
    }

    private TransactionService getTransactionService() {
    	TransactionService ret = Configuration.getTransactionService();
    	if (ret == null) throw new IllegalStateException("Not initialized");
    	return ret;
	}



	/**
     * Called if a tx is ended successfully. In order to remove the tx from the
     * mapping.
     *
     * @see SubTxAwareParticipant
     */

    public void committed ( CompositeTransaction tx )
    {
        removeTransaction ( tx );
    }

    /**
     * Called if a tx is ended with failure. In order to remove tx from mapping.
     *
     * @see SubTxAwareParticipant
     */

    public void rolledback ( CompositeTransaction tx )
    {
        removeTransaction ( tx );

    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction getCompositeTransaction () throws SysException
    {
        
        CompositeTransaction ct = null;
        ct = getCurrentTx ();
        if ( ct != null ) {
        	if(LOGGER.isDebugEnabled()){
            	LOGGER.logDebug("getCompositeTransaction()  returning instance with id "
                        + ct.getTid ());
        	}
        } else{
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.logDebug("getCompositeTransaction() returning NULL!");
        	}
        }

        return ct;
    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction getCompositeTransaction ( String tid )
            throws SysException
    {
        CompositeTransaction ret = getTransactionService().getCompositeTransaction ( tid );
        if ( ret != null ) {
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.logDebug("getCompositeTransaction ( " + tid
                    + " ) returning instance with tid " + ret.getTid ());
        	}
        } else {
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.logDebug( "getCompositeTransaction ( " + tid
                    + " ) returning null");
        	}
        }
        return ret;
    }




    /**
     * Recreate a composite transaction based on an imported context. Needed by
     * the application's communication layer.
     *
     * @param context
     *            The propagationcontext.
     * @param orphancheck
     *            If true, real composite txs are done. If false, OTS like
     *            behavior applies.
     * @param heur_commit
     *            True for heuristic commit, false for heuristic rollback.
     *
     * @return CompositeTransaction The recreated local instance.
     * @exception SysException
     *                Failure.
     */

    public synchronized CompositeTransaction recreateCompositeTransaction (
            Propagation context , boolean orphancheck , boolean heur_commit )
            throws SysException
    {
        CompositeTransaction ct = null;


        ct = getCurrentTx();
        if ( ct != null ) {
            LOGGER.logWarning("Recreating a transaction with existing transaction: " + ct.getTid());
        }
        ct = getTransactionService().recreateCompositeTransaction ( context, orphancheck,
                heur_commit );
        Thread t = Thread.currentThread ();
        setThreadMappings ( ct, t );
        return ct;
    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction suspend () throws SysException
    {
    	

        CompositeTransaction ret = getCurrentTx ();
        if ( ret != null ) {
        	if(LOGGER.isInfoEnabled()){
        		LOGGER.logInfo("suspend() for transaction " + ret.getTid ());
        	}
        	Thread thread = Thread.currentThread();   
            removeThreadMappings ( thread );
        } else {
        	if(LOGGER.isInfoEnabled()){
        		LOGGER.logInfo("suspend() called without a transaction context");
        	}
        }
        return ret;

    }

    /**
     * @see CompositeTransactionManager
     */
    @SuppressWarnings("unchecked")
    public void resume ( CompositeTransaction ct )
            throws IllegalStateException, SysException
    {
        Stack<CompositeTransaction> ancestors = new Stack<CompositeTransaction>();
        Stack<CompositeTransaction> tmp = new Stack<CompositeTransaction>();
        Stack<CompositeTransaction> lineage = (Stack<CompositeTransaction>) ct.getLineage ().clone ();
        boolean done = false;
        while ( !lineage.isEmpty () && !done ) {
            CompositeTransaction parent = lineage.pop ();
            if ( !parent.isLocal () )
                done = true;
            else
                tmp.push ( parent );
        }
        while ( !tmp.isEmpty () ) {
            ancestors.push ( tmp.pop () );
        }
        ancestors.push ( ct );

        Thread thread = Thread.currentThread ();
        restoreThreadMappings ( ancestors, thread );
        if(LOGGER.isInfoEnabled()) LOGGER.logInfo("resume ( " + ct + " ) done for transaction " + ct.getTid ());
        
    }

    /**
     * Shut down the server in a clean way.
     *
     * @param force
     *            If true, shutdown will not wait for possibly indoubt txs to
     *            finish. Calling shutdown with force being true implies that
     *            shutdown will not fail, but there may be remaining timer
     *            threads that stay asleep until there timeouts expire. Such
     *            remaining active transactions will NOT be able to finish,
     *            because the recovery manager will be shutdown by that time.
     *            New transactions will not be allowed.
     *
     * @exception SysException
     *                For unexpected errors.
     * @exception IllegalStateException
     *                If active txs exist, and not force.
     */

    public void shutdown ( boolean force ) throws SysException,
            IllegalStateException
    {
        getTransactionService().shutdown ( force );
    }

    protected void startlistening ( CompositeTransaction transaction )
            throws SysException
    {
        transaction.addSubTxAwareParticipant ( this );
    }

    /**
     * Removes the tx associated with calling thread. Restores context to the
     * last locally started parent, if any. Does nothing if no thread found or
     * if ct null.
     *
     * @param ct
     *            The transaction to remove.
     */

    private void removeTransaction ( CompositeTransaction ct )
    {
        if ( ct == null ) return;

        Thread thread = getThread ( ct );
        if ( thread == null ) return;

        Stack<CompositeTransaction> mappings = removeThreadMappings ( thread );
        if ( mappings != null && !mappings.empty () ) {
            mappings.pop ();
            if ( !mappings.empty () ) restoreThreadMappings ( mappings, thread );
        }

    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction createCompositeTransaction ( long timeout ) throws SysException
    {
        CompositeTransaction ct = null , ret = null;
        
        ct = getCurrentTx ();
        if ( ct == null ) {
            ret = getTransactionService().createCompositeTransaction ( timeout );
            if(LOGGER.isInfoEnabled()){
            	LOGGER.logInfo("createCompositeTransaction ( " + timeout + " ): "
                    + "created new ROOT transaction with id " + ret.getTid ());
            }
        } else {
        	 if(LOGGER.isInfoEnabled()) LOGGER.logInfo("createCompositeTransaction ( " + timeout + " )");
            ret = ct.createSubTransaction ();

        }
        Thread thread = Thread.currentThread ();
        setThreadMappings ( ret, thread );

        return ret;
    }

}
