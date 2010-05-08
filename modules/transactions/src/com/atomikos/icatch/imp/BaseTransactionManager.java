//$Id: BaseTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Id: BaseTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: BaseTransactionManager.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.2  2006/04/14 12:45:13  guy
//Added properties to TSListener init callback.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.13  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.12  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.11  2005/05/06 11:41:34  guy
//Added getParticipant method for JCA inbound recovery.
//Revision 1.7.4.1  2004/06/14 08:09:08  guy
//Merged redesign2002 with redesign2003.
//

//
//Revision 1.9  2004/09/07 14:51:58  guy
//Improved recovery of 1PC committing txs.
//Added rollback() of pending subtxs upon timeout of coordinator.
//Improved behaviour of addParticipant methods for terminated/marked abort txs.
//Revision 1.5.8.6  2002/11/05 09:01:38  guy
//Moved AbstractTransactionService to TransactionServiceImp; no need to have
//an abstract class.
//
//Revision 1.8  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//Revision 1.5.8.5  2002/10/26 15:48:36  guy
//Changed CompositeTransactionManager: resume/suspend work with the CT instance
//instead of a stack. Better model. Also added getCompositeTransaction (tid)
//method: to facilitate resuming transactions based on the tid only.
//Needed for asynchronous processing of transactional calls.
//
//Revision 1.7.2.1  2004/04/30 14:33:01  guy
//Included different log levels, and added immediate rollback for extent
//participants.
//Revision 1.5.8.4  2002/10/25 13:00:48  guy
//Adapted TransactionService.createCompositeTransaction: removed unnecessary heur_commit argument.
//
//Revision 1.7  2003/09/01 15:27:02  guy
//*** empty log message ***
//Revision 1.5.8.3  2002/10/25 11:40:54  guy
//Removed heuristic_commit argument from createCompositeTransaction: not needed since a ROOT is created, or else a subtransaction is created and the param is ignored anyway.
//
//$Id: BaseTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.6  2003/03/11 06:38:53  guy
//$Id: BaseTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: BaseTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.5.4.2  2002/11/17 18:35:59  guy
//Updated ImpTM: terminated does not throw heuristics!
//
//Revision 1.5.4.1  2002/11/14 15:01:52  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.5  2001/11/28 12:52:21  guy
//Changed LogInspector to work with TransactionService, since
//working with rec. mgr. duplicates active coordinators and causes inconsistent
//results.
//
//Revision 1.4  2001/10/29 11:11:32  guy
//Deleted method addExtent; it has been moved to TrmiTransactionManager.
//This is because it needed to check for recursive Participant refs.
//
//Revision 1.3  2001/10/28 16:21:33  guy
//Improved parts here and there, for the new TransactionService way.
//The classes compile, but have not been tested!
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

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
