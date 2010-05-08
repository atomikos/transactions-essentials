//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: CompositeTransactionImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
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
//Revision 1.6  2006/03/21 16:13:01  guy
//Added active recovery as a setter.
//
//Revision 1.5  2006/03/21 14:10:56  guy
//Replaced UnavailableException with UnsupportedOperationException.
//Added feature: suspend/resume of activity when JTA transaction is started.
//
//Revision 1.4  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.3  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:23:40  guy
//Refactored to use 1 coordinator per subtransaction (required for
//activity support and active recovery with compensation).
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.22  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.21  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.20  2004/11/25 10:25:14  guy
//Updated log comments.
//Revision 1.16.2.1  2004/06/14 08:09:08  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.19  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//Revision 1.14.8.3  2002/11/05 09:01:39  guy
//Moved AbstractTransactionService to TransactionServiceImp; no need to have
//an abstract class.
//
//Revision 1.18  2004/09/07 14:51:58  guy
//Improved recovery of 1PC committing txs.
//Added rollback() of pending subtxs upon timeout of coordinator.
//Improved behaviour of addParticipant methods for terminated/marked abort txs.
//
//Revision 1.17  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//Revision 1.16  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.15.2.3  2003/09/11 13:42:08  guy
//Applied STATE pattern to CompositeTransactionImp.
//
//Revision 1.15.2.2  2003/09/10 14:00:47  guy
//Added rollback only support in kernel; corrected bug: subtx allowed addParticipant after commit.
//
//Revision 1.15.2.1  2003/05/15 08:05:16  guy
//Changed to make tests pass with the new state mechanism.
//
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.15  2003/03/11 06:38:53  guy
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: CompositeTransactionImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
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
//Revision 1.6  2006/03/21 16:13:01  guy
//Added active recovery as a setter.
//
//Revision 1.5  2006/03/21 14:10:56  guy
//Replaced UnavailableException with UnsupportedOperationException.
//Added feature: suspend/resume of activity when JTA transaction is started.
//
//Revision 1.4  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.3  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:23:40  guy
//Refactored to use 1 coordinator per subtransaction (required for
//activity support and active recovery with compensation).
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.22  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.21  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.20  2004/11/25 10:25:14  guy
//Updated log comments.
//
//Revision 1.19  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.18  2004/09/07 14:51:58  guy
//Improved recovery of 1PC committing txs.
//Added rollback() of pending subtxs upon timeout of coordinator.
//Improved behaviour of addParticipant methods for terminated/marked abort txs.
//
//Revision 1.17  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.15.4.2  2004/04/30 14:33:01  guy
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Included different log levels, and added immediate rollback for extent
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//participants.
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.15.4.1  2004/04/24 09:06:37  guy
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Changed rollback to also call rollback on EXTENT! (BUG found by MM)
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.15  2003/03/11 06:38:53  guy
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: CompositeTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.14.4.2  2003/01/29 17:19:25  guy
//Changed Synchronization callback context for subtxs.
//
//Revision 1.14.4.1  2002/12/18 18:11:28  guy
//Changed beforeCompletion callback on Synchronization to be done
//BEFORE the subtransaction commit is finished, needed to allow
//extra transactional work on behalf of the calling thread.
//
//Revision 1.14  2002/02/22 17:28:40  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.13  2002/02/22 16:54:39  guy
//Corrected bug in LogControlImp, added debug comments in the rest.
//
//Revision 1.12  2001/11/19 10:05:08  guy
//Eliminated need for state_ attribute: can be decided by looking at commit_.
//
//Revision 1.10  2001/11/14 15:16:17  guy
//Added commit check for createSubTransaction.
//
//Revision 1.8  2001/11/14 14:52:20  guy
//Changed addParticipant to check for commit and throw exception if so.
//
//Revision 1.7  2001/11/14 14:13:34  guy
//Changed getState to return the coordinator state iff the subtx
//was committed already.
//
//Revision 1.6  2001/11/01 08:41:44  guy
//Changed Extent and ExtentImp to include DIRECT participants.
//Changed CompositeTransactionImp to include this effect.
//
//Revision 1.5  2001/10/30 16:00:35  guy
//Added getTimeout method to TransactionControl; needed for OTS version.
//
//Revision 1.4  2001/10/29 16:38:07  guy
//Changed UniqueId for String.
//
//Revision 1.3  2001/10/29 12:10:23  guy
//Added different constructor for testing, and
//a variant implementation of createSubTransaction, also for test
//compatibility.
//
//Revision 1.2  2001/10/28 16:04:53  guy
//Split TM functionality in two parts: one for managing roots and txs,
//and one for mapping these to threads.
//Introduction of the TransactionService interface and implementations.
//These changes were best for implementing JTS.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.12  2001/03/26 16:01:24  pardon
//Updated Proxy to use serial for SubTxAware notification.

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.diagnostics.Console;
import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A complete composite transaction implementation for use in LOCAL VM.
 * 
 */

public class CompositeTransactionImp 
extends AbstractCompositeTransaction implements
        TransactionControl, FSMEnterListener
{

    protected CoordinatorImp coordinator_ = null;
    // the coordinator for this invocation

    protected TransactionServiceImp txservice_;
    // the tx service we are working for

    protected Extent extent_ = null;
    // information about remote participants on behalf of this tx.

    protected boolean localRoot_;
    // true iff LOCALLY there are no ancestor txs

    private TransactionStateHandler stateHandler_;

    /**
     * This constructor is kept for compatibility with the test classes.
     */

    CompositeTransactionImp ( Stack lineage , String tid , boolean serial ,
            CoordinatorImp coordinator )
    {
        this ( null , lineage , tid , serial , coordinator );
    }

    /**
     * Constructor.
     * 
     * @param txservice
     *            The Transaction Service this is for.
     * @param lineage
     *            The ancestor information.
     * @param tid
     *            The identifier for this one.
     * @param serial
     *            If true, no parallel calls allowed.
     * @param coordinator
     *            The coordinator to use.
     * @exception IllegalStateException
     *                If coordinator no longer activatable.
     */

    public CompositeTransactionImp ( TransactionServiceImp txservice ,
            Stack lineage , String tid , boolean serial ,
            CoordinatorImp coordinator ) throws IllegalStateException
    {

        super ( tid , lineage , serial );
        coordinator_ = coordinator;
        txservice_ = txservice;
        // state_ = TxState.ACTIVE; // SUBTX ABORT
        // remoteParticipants_ = new Hashtable();

        extent_ = null;
        // coordinator_.incActiveSiblings(); COORD

        localRoot_ = true;
        stateHandler_ = new TxActiveStateHandler ( this );
        coordinator.addFSMEnterListener ( this, TxState.TERMINATED );

    }

    synchronized void localSetTransactionStateHandler ( TransactionStateHandler handler )
    {
        stateHandler_ = handler;
    }
    
    synchronized void localTestAndSetTransactionStateHandler ( TransactionStateHandler expected , TransactionStateHandler newHandler )
    {
    	if ( stateHandler_ != expected ) throw new IllegalStateException ( "State is no longer " + expected.getState() + " but " + newHandler.getState()  );
    	localSetTransactionStateHandler( newHandler );
    }

    synchronized TransactionStateHandler localGetTransactionStateHandler() 
    {
    	return stateHandler_;
    }
    
    boolean isLocalRoot ()
    {
        return localRoot_;
    }

    TransactionServiceImp getTransactionService ()
    {
        return txservice_;
    }

    CoordinatorImp getCoordinatorImp ()
    {
        return coordinator_;
    }

    //    
    // Stack getParticipants()
    // {
    // return stateHandler_.getParticipants();
    // }

    private void printMsg ( String msg , int level )
    {
        try {
            Console console = Configuration.getConsole ();
            if ( console != null ) {
                console.println ( msg, level );
            }
        } catch ( Exception ignore ) {
        }
    }

    /**
     * @see CompositeTransaction.
     */

    public TransactionControl getTransactionControl ()
    {
        return this;
    }

    /**
     * @see TransactionControl
     */

    public int getLocalSubTxCount ()
    {
        return localGetTransactionStateHandler().getSubTransactionCount ();
    }

    /**
     * @see TransactionControl.
     */

    public synchronized void setSerial () throws IllegalStateException,
            SysException
    {
        if ( !isRoot () )
            throw new IllegalStateException ( "setSerial() not allowed:"
                    + " not root tx." );
        // if context_ not null: remote calls might exist,
        // and hence changing serial mode not allowed.

        serial_ = true;

    }

    // 
    // CompositeTransaction createSubTransaction ( String tid )
    // {
    // return stateHandler_.createSubTransaction ( tid );
    // }

    /**
     * @see TransactionControl.
     */

    public CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        CompositeTransaction ret = localGetTransactionStateHandler().createSubTransaction ();
        printMsg ( "createSubTransaction(): created new SUBTRANSACTION "
                + ret.getTid () + " for existing transaction " + getTid (),
                Console.INFO );
        return ret;
    }

    /**
     * @see CompositeTransaction
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException
    // RollbackException
    {

        RecoveryCoordinator ret = localGetTransactionStateHandler().addParticipant ( participant );
        printMsg ( "addParticipant ( " + participant + " ) for transaction "
                + getTid (), Console.INFO );
        return ret;
    }

    /**
     * @see CompositeTransaction
     */

    public void registerSynchronization ( Synchronization sync ) throws // RollbackException,
            IllegalStateException, UnsupportedOperationException, SysException
    {
    	localGetTransactionStateHandler().registerSynchronization ( sync );
    	 printMsg ( "registerSynchronization ( " + sync + " ) for transaction "
                 + getTid (), Console.INFO );
    }

    /**
     * @see CompositeTransaction
     */

    public void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware )
            throws SysException, java.lang.IllegalStateException
    {
    	localGetTransactionStateHandler().addSubTxAwareParticipant ( subtxaware );
    }

    /**
     * @see TransactionControl.
     */

    protected void doRollback () throws java.lang.IllegalStateException,
            SysException
    {

    	localGetTransactionStateHandler().rollbackWithStateCheck ();
        printMsg ( "rollback() done of transaction " + getTid (), Console.INFO );

    }

    /**
     * @see CompositeTransaction.
     */

    public CompositeCoordinator getCompositeCoordinator () throws SysException
    {
        return coordinator_;
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isLocal ()
    {
        return true;
    }

    /**
     * @see TransactionControl.
     */

    public CompositeTerminator getTerminator ()
    {
        return new CompositeTerminatorImp ( txservice_, this, coordinator_ );
    }

    /**
     * Successfully end the composite transaction. Marks it as inactive. Called
     * by Terminator implementation only! NOTE: this does NOT commit the
     * participants, but rather only marks the (sub)transaction as being
     * ELIGIBLE FOR PREPARE IN 2PC.
     * 
     * @exception IllegalStateException
     *                If no longer active.
     * @exception SysException
     *                Unexpected failure.
     */

    protected void doCommit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {

    	localGetTransactionStateHandler().commit ();
        printMsg (
                "commit() done (by application) of transaction " + getTid (),
                Console.INFO );

    }

    /**
     * @see TransactionControl
     */

    public long getTimeout ()
    {
        return coordinator_.getTimeOut ();
    }

    /**
     * @see TransactionControl.
     */

    public synchronized Extent getExtent ()
    {
    		if ( extent_ == null )
            extent_ = new ExtentImp ();
    		return extent_;

        
    }
    
   

    /**
     * @see TransactionControl.
     */

    public void setRollbackOnly ()
    {
    	localGetTransactionStateHandler().setRollbackOnly ();
        printMsg ( "setRollbackOnly() called for transaction " + getTid (),
                Console.INFO );
       
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#commit()
     */
    public void commit () throws HeurRollbackException, HeurMixedException,
            HeurHazardException, SysException, SecurityException,
            RollbackException
    {
        getTerminator ().commit ();

    }



    /**
     * @see com.atomikos.icatch.CompositeTransaction#rollback()
     */
    public void rollback () throws IllegalStateException, SysException
    {
        getTerminator ().rollback ();

    }

    //
    //
    //
    // IMPLEMENTATION OF STATEFUL
    //
    //
    //

    /**
     * @see com.atomikos.finitestates.Stateful.
     */

    public Object getState ()
    {
        return localGetTransactionStateHandler().getState ();
    }

    /**
     * @see com.atomikos.finitestates.FSMEnterListener#preEnter(com.atomikos.finitestates.FSMEnterEvent)
     */
    public void entered ( FSMEnterEvent event ) 
    {
        // if the state of this subtx is still active then
        // we have a timedout subtx, meaning it has to be
        // rolled back
    	
        if ( getState ().equals ( TxState.ACTIVE )
                || getState ().equals ( TxState.MARKED_ABORT ) ) {
            try {
            	boolean recoverableWhileActive = false;
            	Boolean pref = coordinator_.isRecoverableWhileActive();
            	if ( pref != null ) recoverableWhileActive = pref.booleanValue();
            	if ( !recoverableWhileActive && !( stateHandler_ instanceof TxTerminatedStateHandler ) ) {
            		//see case 27857: keep tx context for thread
            		//note: check for TxTerminatedStateHandler differentiates regular rollback from timeout/rollback !!!
            		setRollbackOnly();
            	} else  {
            		rollback ();
            	}

            } catch ( Exception e ) {
                // ignore
            }
        } 

    }

	

	


	

}
