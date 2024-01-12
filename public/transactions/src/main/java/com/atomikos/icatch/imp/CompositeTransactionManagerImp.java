/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

/**
 * Reusable (generic) composite transaction manager implementation.
 */

public class CompositeTransactionManagerImp implements CompositeTransactionManager,
        SubTxAwareParticipant
{
    private static final Logger LOGGER = LoggerFactory.createLogger(CompositeTransactionManagerImp.class);

    private final Map<Thread, Deque<CompositeTransaction>> threadToTxMap;
    private final Map<CompositeTransaction, Thread> txToThreadMap;


    public CompositeTransactionManagerImp ()
    {
        threadToTxMap = new ConcurrentHashMap<>();
        txToThreadMap = new ConcurrentHashMap<>();
    }



    /**
     * Get the thread associated with a given transaction.
     */

    private Thread getThread ( CompositeTransaction ct )
    {
        return txToThreadMap.get ( ct );
    }

    /**
     * Remove mappings for given thread.
     *
     * @return Stack The tx stack that was for current thread.
     */

    private Deque<CompositeTransaction> removeThreadMappings ( Thread thread )
    {
        Deque<CompositeTransaction> ret = threadToTxMap.remove ( thread );
        txToThreadMap.remove ( ret.peek () );
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


        if ( TxState.ACTIVE.equals ( ct.getState() )) {
            Deque<CompositeTransaction> txs = threadToTxMap.computeIfAbsent ( thread , k -> new ConcurrentLinkedDeque<>());
            synchronized ( ct ) {
                //between step 1 and here, intermediate timeout/rollback of the ct
                //may have happened; make sure to check or we add a thread mapping
                //that will never be removed!
                if ( TxState.ACTIVE.equals ( ct.getState() )) {
                    txs.push ( ct );
                    threadToTxMap.put ( thread, txs );
                    txToThreadMap.put ( ct, thread );
                }
            }
        }
    }

    private void restoreThreadMappings ( Deque<CompositeTransaction> stack , Thread thread )
            throws IllegalStateException
    {
        //case 21806: callbacks to ct to be made outside synchronized block
        CompositeTransaction tx = stack.peek ();
        tx.addSubTxAwareParticipant(this); //step 1



        if ( tx.getState().isOneOf(TxState.ACTIVE, TxState.MARKED_ABORT) ) {
            //also resume for marked abort - see case 26398
            Deque<CompositeTransaction> txs = threadToTxMap.get ( thread );
            if ( txs != null ) {
                throw new IllegalStateException ("Thread already has subtx stack" );
            }
            synchronized (tx) {
                //between step 1 and here, intermediate timeout/rollback of the tx
                //may have happened; make sure to check or we add a thread mapping
                //that will never be removed!
                if ( tx.getState().isOneOf(TxState.ACTIVE, TxState.MARKED_ABORT) ) {
                    threadToTxMap.put ( thread, stack );
                    txToThreadMap.put ( tx, thread );
                }
            }
        }
    }

    private CompositeTransaction getCurrentTx ()
    {
        Thread thread = Thread.currentThread ();
        Deque<CompositeTransaction> txs = threadToTxMap.getOrDefault ( thread, new ConcurrentLinkedDeque<>() );
        return txs.peek ();
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

        CompositeTransaction ct;
        ct = getCurrentTx ();
        if ( ct != null ) {
        	if(LOGGER.isTraceEnabled()){
            	LOGGER.logTrace("getCompositeTransaction()  returning instance with id "
                        + ct.getTid ());
        	}
        } else{
        	if(LOGGER.isTraceEnabled()){
        		LOGGER.logTrace("getCompositeTransaction() returning NULL!");
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
        	if(LOGGER.isTraceEnabled()){
        		LOGGER.logTrace("getCompositeTransaction ( " + tid
                    + " ) returning instance with tid " + ret.getTid ());
        	}
        } else {
        	if(LOGGER.isTraceEnabled()){
        		LOGGER.logTrace( "getCompositeTransaction ( " + tid
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
     *
     * @return CompositeTransaction The recreated local instance.
     * @exception SysException
     *                Failure.
     */

    public synchronized CompositeTransaction recreateCompositeTransaction( Propagation context )throws SysException {
        CompositeTransaction ct;


        ct = getCurrentTx();
        if ( ct != null ) {
            LOGGER.logWarning("Recreating a transaction with existing transaction: " + ct.getTid());
        }
        ct = getTransactionService().recreateCompositeTransaction(context);
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
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.logDebug("suspend() for transaction " + ret.getTid ());
        	}
            removeThreadMappings(Thread.currentThread());
        } else {
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.logDebug("suspend() called without a transaction context");
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
        Deque<CompositeTransaction> ancestors = new ConcurrentLinkedDeque<>();
        Deque<CompositeTransaction> tmp = new ConcurrentLinkedDeque<>();
        Deque<CompositeTransaction> lineage = (Deque<CompositeTransaction>) ct.getLineage ().clone ();
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

        restoreThreadMappings(ancestors, Thread.currentThread());
        if(LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("resume ( " + ct + " ) done for transaction " + ct.getTid ());
        }

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

        Deque<CompositeTransaction> mappings = removeThreadMappings ( thread );
        if ( mappings != null && !mappings.isEmpty() ) {
            mappings.pop();
            if ( !mappings.isEmpty()) {
                restoreThreadMappings(mappings, thread);
            }
        }

    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction createCompositeTransaction ( long timeout ) throws SysException
    {
        CompositeTransaction ct, ret;

        ct = getCurrentTx ();
        if ( ct == null ) {
            ret = getTransactionService().createCompositeTransaction ( timeout );
            if(LOGGER.isDebugEnabled()){
            	LOGGER.logDebug("createCompositeTransaction ( " + timeout + " ): "
                    + "created new ROOT transaction with id " + ret.getTid ());
            }
        } else {
        	 if(LOGGER.isDebugEnabled()) {
        	     LOGGER.logDebug("createCompositeTransaction ( " + timeout + " )");
        	 }
            ret = ct.createSubTransaction ();

        }
        Thread thread = Thread.currentThread ();
        setThreadMappings ( ret, thread );

        return ret;
    }

}
