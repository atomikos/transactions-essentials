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

import java.util.Map;
import java.util.Stack;

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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A complete composite transaction implementation for use in the local VM.
 */

public class CompositeTransactionImp
extends AbstractCompositeTransaction implements
        CompositeTerminator, TransactionControl, FSMEnterListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CompositeTransactionImp.class);

    protected CoordinatorImp coordinator = null;

    protected TransactionServiceImp txservice;

    protected Extent extent = null;

    protected boolean noLocalAncestors;

    private TransactionStateHandler stateHandler;

    /**
     * This constructor is kept for compatibility with the test classes.
     */

    CompositeTransactionImp ( Stack<CompositeTransaction> lineage , String tid , boolean serial ,
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

    CompositeTransactionImp ( TransactionServiceImp txservice ,
            Stack<CompositeTransaction> lineage , String tid , boolean serial ,
            CoordinatorImp coordinator ) throws IllegalStateException
    {

        super ( tid , lineage , serial );
        this.coordinator = coordinator;
        this.txservice = txservice;
        this.extent = null;
        this.noLocalAncestors = true;
        this.stateHandler = new TxActiveStateHandler ( this );
        coordinator.addFSMEnterListener ( this, TxState.TERMINATED );

    }

    synchronized void localSetTransactionStateHandler ( TransactionStateHandler handler )
    {
        stateHandler = handler;
    }

    synchronized void localTestAndSetTransactionStateHandler ( TransactionStateHandler expected , TransactionStateHandler newHandler )
    {
    	if ( stateHandler != expected ) throw new IllegalStateException ( "State is no longer " + expected.getState() + " but " + newHandler.getState()  );
    	localSetTransactionStateHandler( newHandler );
    }

    synchronized TransactionStateHandler localGetTransactionStateHandler()
    {
    	return stateHandler;
    }

    boolean isLocalRoot ()
    {
        return noLocalAncestors;
    }

    TransactionServiceImp getTransactionService ()
    {
        return txservice;
    }

    CoordinatorImp getCoordinatorImp ()
    {
        return coordinator;
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
        if ( !isRoot () ) throw new IllegalStateException ( "Not a root tx." );
        serial_ = true;
    }


    /**
     * @see TransactionControl.
     */

    public CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        CompositeTransaction ret = localGetTransactionStateHandler().createSubTransaction ();
        if(LOGGER.isInfoEnabled()){
        	LOGGER.logInfo("createSubTransaction(): created new SUBTRANSACTION "
                    + ret.getTid () + " for existing transaction " + getTid ());
        }

        return ret;
    }

    /**
     * @see CompositeTransaction
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException
    {

        RecoveryCoordinator ret = localGetTransactionStateHandler().addParticipant ( participant );
        if(LOGGER.isInfoEnabled()){
        	LOGGER.logInfo("addParticipant ( " + participant + " ) for transaction "
                    + getTid ());
        }
        return ret;
    }

    /**
     * @see CompositeTransaction
     */

    public void registerSynchronization ( Synchronization sync ) throws // RollbackException,
            IllegalStateException, UnsupportedOperationException, SysException
    {
    	localGetTransactionStateHandler().registerSynchronization ( sync );
    	if(LOGGER.isInfoEnabled()){
    		LOGGER.logInfo("registerSynchronization ( " + sync + " ) for transaction "
                    + getTid ());
    	}
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
    	if(LOGGER.isInfoEnabled()){
    		LOGGER.logInfo("rollback() done of transaction " + getTid ());
    	}

    }

    /**
     * @see CompositeTransaction.
     */

    public CompositeCoordinator getCompositeCoordinator () throws SysException
    {
        return coordinator;
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
        return this;
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
    	if(LOGGER.isInfoEnabled()){
    		LOGGER.logInfo("commit() done (by application) of transaction " + getTid ());
    	}
    }

    /**
     * @see TransactionControl
     */

    public long getTimeout ()
    {
        return coordinator.getTimeOut ();
    }

    /**
     * @see TransactionControl.
     */

    public synchronized Extent getExtent ()
    {
    		if ( extent == null )
            extent = new ExtentImp ();
    		return extent;
    }



    /**
     * @see TransactionControl.
     */

    public void setRollbackOnly ()
    {
    	localGetTransactionStateHandler().setRollbackOnly ();
    	if(LOGGER.isInfoEnabled()){
    		LOGGER.logInfo("setRollbackOnly() called for transaction " + getTid ());
    	}


    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#commit()
     */
    public void commit () throws HeurRollbackException, HeurMixedException,
            HeurHazardException, SysException, SecurityException,
            RollbackException
    {
        doCommit ();
        setSiblingInfoForIncoming1pcRequestFromRemoteClient();
        
        if ( isRoot () ) {
            try {
                coordinator.terminate ( true );
            }

            catch ( RollbackException rb ) {
                throw rb;
            } catch ( HeurHazardException hh ) {
                throw hh;
            } catch ( HeurRollbackException hr ) {
                throw hr;
            } catch ( HeurMixedException hm ) {
                throw hm;
            } catch ( SysException se ) {
                throw se;
            } catch ( Exception e ) {
                throw new SysException (
                        "Unexpected error: " + e.getMessage (), e );
            }
        }
    }

    private void setSiblingInfoForIncoming1pcRequestFromRemoteClient() {
    	Map<String,Integer> cascadelist = getExtent().getRemoteParticipants ();
        coordinator.setGlobalSiblingCount ( coordinator.getLocalSiblingCount () );
        coordinator.setCascadeList ( cascadelist );
	}


    /**
     * @see com.atomikos.icatch.CompositeTransaction#rollback()
     */
    public void rollback () throws IllegalStateException, SysException
    {
    	doRollback ();
        if ( isRoot () ) {
            try {
                coordinator.terminate ( false );
            } catch ( Exception e ) {
                throw new SysException ( "Unexpected error in rollback: " + e.getMessage (), e );
            }
        }
    }

    /**
     * @see com.atomikos.finitestates.Stateful.
     */

    public TxState getState ()
    {
        return localGetTransactionStateHandler().getState ();
    }

    /**
     * @see com.atomikos.finitestates.FSMEnterListener#preEnter(com.atomikos.finitestates.FSMEnterEvent)
     */
    public void entered ( FSMEnterEvent coordinatorTerminatedEvent )
    {
        if (getState().isOneOf(TxState.ACTIVE,TxState.MARKED_ABORT )) {
        	// our coordinator terminated and we did not -> coordinator must have seen a timeout/abort 
            try {
            	if (!( stateHandler instanceof TxTerminatedStateHandler ) ) {
            		// note: check for TxTerminatedStateHandler differentiates regular rollback from timeout/rollback !!!           		          		
            		setRollbackOnly(); // see case 27857: keep tx context for thread on timeout
            	} else  {
            		rollback();
            	}

            } catch ( Exception e ) {
                // ignore but log
            	LOGGER.logDebug("Ignoring error during event callback",e);
            }
        }

    }


}
