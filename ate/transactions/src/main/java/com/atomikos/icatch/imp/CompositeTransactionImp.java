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
