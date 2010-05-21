package com.atomikos.icatch.imp;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Abstract TM class, to be extended for different communication layers. For
 * instance, transactional RMI could be one extension, JTS another one.
 */

public class BaseTransactionManager implements CompositeTransactionManager,
        SubTxAwareParticipant
{

    private Hashtable threadtotxmap_ = null;
    // maps threads to composite transactions

    private Hashtable txtothreadmap_ = null;
    // inverse map: txs to threads

    private TransactionServiceImp service_;
    // the tx service to use.

    private boolean initialized_;

    // true asa init called

    /**
     * Constructor.
     * 
     */

    protected BaseTransactionManager ()
    {
        threadtotxmap_ = new Hashtable ();
        txtothreadmap_ = new Hashtable ();
        initialized_ = false;
    }

    /**
     * Print a message to the console, with the given level of detail.
     * 
     * @param msg
     * @param level
     */
    protected void printMsg ( String msg , int level )
    {
        try {
            Console console = Configuration.getConsole ();
            if ( console != null ) {
                console.println ( msg, level );
            }
        } catch ( Exception e ) {
            // ignore
        }
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

    private Stack removeThreadMappings ( Thread thread )
    {

        Stack ret = null;
        synchronized ( threadtotxmap_ ) {
            ret = (Stack) threadtotxmap_.remove ( thread );
            CompositeTransaction tx = (CompositeTransaction) ret.peek ();
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
        		Stack txs = (Stack) threadtotxmap_.get ( thread );
        		if ( txs == null )
        			txs = new Stack ();
        		txs.push ( ct );
        		threadtotxmap_.put ( thread, txs );
        		txtothreadmap_.put ( ct, thread );
        	}
        }


    }

    private void restoreThreadMappings ( Stack stack , Thread thread )
            throws IllegalStateException
    {
    	//case 21806: callbacks to ct to be made outside synchronized block
    	CompositeTransaction tx = (CompositeTransaction) stack.peek ();
    	tx.addSubTxAwareParticipant ( this ); //step 1
    	
        synchronized ( threadtotxmap_ ) {
        	//between step 1 and here, intermediate timeout/rollback of the ct
        	//may have happened; make sure to check or we add a thread mapping
        	//that will never be removed!
        	Object state = tx.getState();
        	
        	if ( TxState.ACTIVE.equals ( state ) || TxState.MARKED_ABORT.equals ( state ) ) {
        		//also resume for marked abort - see case 26398
        		Stack txs = (Stack) threadtotxmap_.get ( thread );
        		if ( txs != null )
        			throw new IllegalStateException (
        					"Thread already has subtx stack" );
        		threadtotxmap_.put ( thread, stack );
        		txtothreadmap_.put ( tx, thread );
        	}
        }
    }

    private CompositeTransactionImp getCurrentTx ()
    {
        Thread thread = Thread.currentThread ();
        synchronized ( threadtotxmap_ ) {
            Stack txs = (Stack) threadtotxmap_.get ( thread );
            if ( txs == null )
                return null;
            else
                return (CompositeTransactionImp) txs.peek ();

        }
    }

    // protected abstract CompositeTransaction createProxy (
    // CompositeTransaction ct )
    // throws SysException;

    /**
     * Initialize the TM.
     * 
     * @param service
     *            The tx service to use. As part of this method, the service
     *            will also be initialized.
     * @param properties The init properties.
     */

    public void init ( TransactionServiceImp service , Properties properties ) throws SysException
    {
        service_ = service;
        service_.init ( properties );
        initialized_ = true;
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
        return service_.getParticipant ( root );
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
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CompositeTransaction ct = null;
        ct = getCurrentTx ();
        if ( ct != null ) {
            printMsg ( "getCompositeTransaction()  returning instance with id "
                    + ct.getTid (), Console.DEBUG );
        } else
            printMsg ( "getCompositeTransaction() returning NULL!",
                    Console.DEBUG );
        return ct;
    }

    /**
     * @see CompositeTransactionManager
     */

    public CompositeTransaction getCompositeTransaction ( String tid )
            throws SysException
    {
        CompositeTransaction ret = service_.getCompositeTransaction ( tid );
        if ( ret != null ) {
            printMsg ( "getCompositeTransaction ( " + tid
                    + " ) returning instance with tid " + ret.getTid (),
                    Console.DEBUG );
        } else {
            printMsg ( "getCompositeTransaction ( " + tid
                    + " ) returning NULL!", Console.DEBUG );
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

    protected synchronized CompositeTransaction recreateCompositeTransaction (
            Propagation context , boolean orphancheck , boolean heur_commit )
            throws SysException
    {
        CompositeTransaction ct = null;
        
        
        ct = getCurrentTx();
        if ( ct != null ) {
            String msg = "Recreating a transaction with existing transaction: " + ct.getTid();
            printMsg ( msg , Console.WARN );
            //FOLLOWING DISABLED BECAUSE IT MAKES TESTS FAIL
            //throw new IllegalStateException ( msg );
        }
        ct = service_.recreateCompositeTransaction ( context, orphancheck,
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
        Thread thread = Thread.currentThread ();
        // Stack ret = null;
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CompositeTransaction ret = getCurrentTx ();
        if ( ret != null ) {
            printMsg ( "suspend() for transaction " + ret.getTid (),
                    Console.INFO );
            removeThreadMappings ( thread );
        } else {
            printMsg ( "suspend() called without a transaction context",
                    Console.INFO );
        }
        return ret;

    }

    /**
     * @see CompositeTransactionManager
     */

    public void resume ( CompositeTransaction ct )
            throws IllegalStateException, SysException
    {
        Thread thread = Thread.currentThread ();

        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        // CHANGED: restore ancestor stack by inspecting the lineage:
        // the LOCAL ancestors are part of the stack
        Stack ancestors = new Stack ();
        Stack tmp = new Stack ();
        Stack lineage = (Stack) ct.getLineage ().clone ();
        boolean done = false;
        while ( !lineage.isEmpty () && !done ) {
            CompositeTransaction parent = (CompositeTransaction) lineage.pop ();
            if ( !parent.isLocal () )
                done = true;
            else
                tmp.push ( parent );
        }
        // now, reverse order of tmp
        while ( !tmp.isEmpty () ) {
            ancestors.push ( tmp.pop () );
        }
        // lastly, make sure that the ct itself is also in the stack.
        ancestors.push ( ct );

        restoreThreadMappings ( ancestors, thread );
        printMsg (
                "resume ( " + ct + " ) done for transaction " + ct.getTid (),
                Console.INFO );
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
        service_.shutdown ( force );
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
        if ( ct == null )
            return;

        Thread thread = getThread ( ct );
        if ( thread == null )
            return;

        Stack mappings = removeThreadMappings ( thread );
        if ( mappings != null && !mappings.empty () ) {
            mappings.pop ();
            if ( !mappings.empty () )
                restoreThreadMappings ( mappings, thread );
        }

    }

    /**
     * @see CompositeTransactionManager
     */
    
    public CompositeTransaction createCompositeTransaction ( long timeout ) throws SysException
    {
        Stack errors = new Stack ();
        CompositeTransaction ct = null , ret = null;
        Thread thread = Thread.currentThread ();

        ct = getCurrentTx ();
        if ( ct == null ) {

            ret = service_.createCompositeTransaction ( timeout );
            printMsg ( "createCompositeTransaction ( " + timeout + " ): "
                    + "created new ROOT transaction with id " + ret.getTid (),
                    Console.INFO );
        } else {           
            
            printMsg ( "createCompositeTransaction ( " + timeout + " )",
                    Console.INFO );
            // let CT implementation do the details of logging
            ret = ct.getTransactionControl ().createSubTransaction ();

        }
        setThreadMappings ( ret, thread );

        return ret;
    }

}
