/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.TransactionServicePlugin;
import com.atomikos.icatch.provider.TransactionServiceProvider;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.thread.InterruptedExceptionHelper;
import com.atomikos.thread.TaskManager;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;
import com.atomikos.util.UniqueIdMgr;

/**
 * General implementation of Transaction Service.
 */

public class TransactionServiceImp implements TransactionServiceProvider,
        FSMEnterListener<TxState>, SubTxAwareParticipant, RecoveryService, AdminLog
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionServiceImp.class);
    private static final int NUMLATCHES = 97;
    
    private long maxTimeout_;
    private Object[] rootLatches_ = null;
    private Hashtable tidToTransactionMap_ = null;
    private Hashtable rootToCoordinatorMap_ = null;
    private boolean shutdownInProgress_ = false;
    private Object shutdownSynchronizer_;
    private Object recoverySynchronizer_;
    private UniqueIdMgr tidmgr_ = null;
    private StateRecoveryManager recoverymanager_ = null;
    private boolean initialized_ = false;
    private LogControl control_;
    private boolean otsOverride_;
    // true for forced compatibility with OTS;
    // in that case no creation preferences are taken into account
    // concerning orphan checks

    private Vector tsListeners_;
    private int maxNumberOfActiveTransactions_;
    private String tmUniqueName_;
    private Properties initProperties_;
    private boolean single_threaded_2pc_;
	private RecoveryLog recoveryLog;

    /**
     * Create a new instance, with orphan checking set.
     *
     * @param name
     *            The unique name of this TM.
     * @param recoverymanager
     *            The recovery manager to use.
     * @param tidmgr
     *            The String manager to use.
     * @param console
     *            The console to use. Null if none.
     * @param maxtimeout
     *            The max timeout for new or imported txs.
     * @param maxActives
     *            The max number of active txs, or negative if unlimited.
     * @param single_threaded_2pc
     *            Whether 2PC commit should happen in the same thread that started the tx.
     */

    public TransactionServiceImp ( String name ,
            StateRecoveryManager recoverymanager , UniqueIdMgr tidmgr ,
            long maxtimeout , int maxActives , boolean single_threaded_2pc, RecoveryLog recoveryLog )
    {
        this ( name , recoverymanager , tidmgr  , maxtimeout , true ,
                maxActives , single_threaded_2pc, recoveryLog  );
    }

    /**
     * Create a new instance, with orphan checking set.
     *
     * @param name
     *            The unique name of this TM.
     * @param recoverymanager
     *            The recovery manager to use.
     * @param tidmgr
     *            The String manager to use.
     * @param console
     *            The console to use. Null if none.
     * @param maxtimeout
     *            The max timeout for new or imported txs.
     * @param checkorphans
     *            If false, orphan checking is disabled
     * @param maxActives
     *            The max number of active txs, or negative if unlimited.
     *            <b>even for creation requests that ask for checks</b>. This
     *            mode may be needed for being compatible with certain
     *            configurations that do not support orphan detection.
     * @param single_threaded_2pc
     *            Whether 2PC commit should happen in the same thread that started the tx.
     * @param recoveryLog2 
     *
     */

    public TransactionServiceImp ( String name ,
            StateRecoveryManager recoverymanager , UniqueIdMgr tidmgr ,
             long maxtimeout , boolean checkorphans ,
            int maxActives , boolean single_threaded_2pc, RecoveryLog recoveryLog )
    {
        maxNumberOfActiveTransactions_ = maxActives;
        if ( !checkorphans ) otsOverride_ = true;
        else otsOverride_ = false;

        initialized_ = false;
        recoverymanager_ = recoverymanager;
        tidmgr_ = tidmgr;
        tidToTransactionMap_ = new Hashtable();
        shutdownSynchronizer_ = new Object();
        recoverySynchronizer_ = new Object();
        rootToCoordinatorMap_ = new Hashtable();
        rootLatches_ = new Object[NUMLATCHES];
        for (int i = 0; i < NUMLATCHES; i++) {
            rootLatches_[i] = new Object();
        }

        maxTimeout_ = maxtimeout;
        tmUniqueName_ = name;
        tsListeners_ = new Vector();
        single_threaded_2pc_ = single_threaded_2pc;
        this.recoveryLog  = recoveryLog;
    }

    /**
     * Get an object to lock for the given root. To increase concurrency and
     * still provide atomic operations within the scope of one root.
     *
     * @return Object The object to lock for the given root.
     */

    protected Object getLatch ( String root )
    {
        return rootLatches_[Math.abs ( root.toString().hashCode() % NUMLATCHES )];
    }

    /**
     * Set the map to ct for this tid.
     *
     * @param tid
     *            The tx id to map.
     * @param ct
     *            The tx to map to.
     * @exception IllegalStateException
     *                If the tid is already mapped.
     */

    void setTidToTx ( String tid , CompositeTransaction ct )
            throws IllegalStateException
    {
        synchronized ( tidToTransactionMap_ ) {
            if ( tidToTransactionMap_.containsKey ( tid.intern () ) )
                throw new IllegalStateException ( "Already mapped: " + tid );
            tidToTransactionMap_.put ( tid.intern (), ct );
            ct.addSubTxAwareParticipant(this); // for GC purposes
        }
    }

    /**
     * For inspector tool: get a list of all active coordinator instances, to
     * allow admin intervention.
     *
     * @return Vector A copy of the list of active coordinators. Empty vector if
     *         none.
     */

    Vector<CoordinatorImp> getCoordinatorImpVector ()
    {
        Vector ret = new Vector ();
        Enumeration tids = rootToCoordinatorMap_.keys ();
        while ( tids.hasMoreElements () ) {
            String next = (String) tids.nextElement ();
            CoordinatorImp c = getCoordinatorImp ( next );
            if ( c != null ) {
                // not synchronized -> may be null if removed
                // between enummeration time and retrieval
                // in that case, merely leave it out of returned list
                ret.addElement ( c );
            }
        }
        return ret;
    }



    /**
     * Removes the coordinator from the root map.
     *
     * @param coord
     *            The coordinator to remove.
     */

    private void removeCoordinator ( CompositeCoordinator coord )
    {

        synchronized ( shutdownSynchronizer_ ) {
            synchronized ( getLatch ( coord.getCoordinatorId ().intern () ) ) {

                rootToCoordinatorMap_.remove ( coord.getCoordinatorId ().intern () );
            }

            // notify any waiting threads for shutdown
            if ( rootToCoordinatorMap_.isEmpty () )
                shutdownSynchronizer_.notifyAll ();
        }
    }

    /**
     * Removes the tx from the map.
     *
     * Does nothing if not found or if ct null.
     *
     * @param ct
     *            The transaction to remove.
     */

    private void removeTransaction ( CompositeTransaction ct )
    {
        if ( ct == null )
            return;
        tidToTransactionMap_.remove ( ct.getTid ().intern () );

    }

    /**
     * Creation method for composite transactions.
     *
     * @return CompositeTransaction.
     */

    private CompositeTransactionImp createCT ( String tid ,
            CoordinatorImp coordinator , Stack lineage , boolean serial )
            throws SysException
    {
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Creating composite transaction: " + tid );
        CompositeTransactionImp ct = new CompositeTransactionImp ( this,
                lineage, tid, serial, coordinator );

        setTidToTx ( ct.getTid (), ct );
        return ct;
    }

    /**
     * Creation method for composite coordinators.
     *
     * @param RecoveryCoordinator
     *            An existing coordinator for the given root. Null if not a
     *            subtx, or an <b>adaptor</b> in other cases.
     * @param lineage
     *            The ancestor information.
     * @param root
     *            The root id.
     * @param checkOrphans
     *            False for OTS, true for real composite txs.
     * @param heuristic_commit
     *            True for heuristic commit on timeout, false for heuristic
     *            abort. ONLY has effect if coordinator is null, otherwise the
     *            given coordinator's settings are taken.
     * @param timeout
     *            The timeout for indoubt states. After this time, indoubts are
     *            terminated heuristically according to the given strategy.
     *
     * @return CoordinatorImp.
     */

    private CoordinatorImp createCC ( RecoveryCoordinator adaptor ,
            String root , boolean checkOrphans , boolean heuristic_commit ,
            long timeout )
    {
        CoordinatorImp cc = null;

        if ( timeout > maxTimeout_ ) {
            timeout = maxTimeout_;
            //FIXED 20188
            LOGGER.logWarning ( "Attempt to create a transaction with a timeout that exceeds maximum - truncating to: " + maxTimeout_ );
        }

        synchronized ( shutdownSynchronizer_ ) {
            // check if shutting down -> do not allow new coordinator objects
            // to be added, so that shutdown will eventually succeed.
            if ( shutdownInProgress_ )
                throw new IllegalStateException ( "Server is shutting down..." );

            if ( otsOverride_ ) {
                // forced OTS mode; we do NEVER check orphans in this case
                checkOrphans = false;
            }
            cc = new CoordinatorImp ( root, adaptor,
                    heuristic_commit, timeout,
                    checkOrphans , single_threaded_2pc_ );

            recoverymanager_.register ( cc );

            // now, add to root map, since we are sure there are not too many active txs
            synchronized ( getLatch ( root.intern () ) ) {
                rootToCoordinatorMap_.put ( root.intern (), cc );
            }
            startlistening ( cc );
        }

        return cc;
    }

    /**
     * Start listening for terminated states, so coordinator can be removed.
     *
     * @param coordinator
     *            The coordinator to listen on.
     *
     */

    private void startlistening ( CoordinatorImp coordinator )
    {
        Set<TxState>  forgetStates = new HashSet<TxState>();
        for (TxState txState : TxState.values()) {
			if(txState.isFinalState()) {
				forgetStates.add(txState);
			}
		}
        
        for (TxState txState : forgetStates) {
        	 coordinator.addFSMEnterListener ( this, txState );
		}

        // on recovery, the end states might have been reached
        // BEFORE listener added -> check and remove if so.
        if ( forgetStates.contains ( coordinator.getState () ) )
            removeCoordinator ( coordinator );
    }

    private CoordinatorImp getCoordinatorImp ( String root )
            throws SysException
    {
        root = root.intern ();
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CoordinatorImp cc = null;
        synchronized ( shutdownSynchronizer_ ) {
            // Synch on shutdownSynchronizer_ first to avoid
            // deadlock, even if we don't seem to need it here

            synchronized ( getLatch ( root ) ) {
                cc = (CoordinatorImp) rootToCoordinatorMap_.get ( root
                        .intern () );
                if ( cc == null ) {
                    // swapped out already, or non-existing?
                    try {
                        cc = (CoordinatorImp) recoverymanager_.recover ( root );
                    } catch ( LogException le ) {
                        throw new SysException (
                                "Error in getting coordinator: "
                                        + le.getMessage (), le );
                    }
                    if ( cc != null ) {
                        startlistening ( cc );
                        rootToCoordinatorMap_.put ( root.intern (), cc );
                    }
                }
            }
        }

        return cc;
    }

    /**
     * Create a new tid.
     *
     * @return String The newly created and unique identifier.
     */

    protected String createTid () throws SysException
    {
        return tidmgr_.get ();
    }

    /**
     * Get the state recovery manager.
     *
     * @return StateRecoveryManager The recovery manager.
     */

    protected StateRecoveryManager getStateRecoveryManager ()
    {
        return recoverymanager_;
    }

    /**
     * Recover instances from a given recovery manager.
     *
     *
     * @exception SysException
     *                For unexpected failure.
     */

    protected synchronized void recoverCoordinators () throws SysException
    {
        try {
            Vector recovered = recoverymanager_.recover ();
            Enumeration enumm = recovered.elements ();
            while ( enumm.hasMoreElements () ) {
                CoordinatorImp coord = (CoordinatorImp) enumm.nextElement ();
                synchronized ( getLatch ( coord.getCoordinatorId ().intern () ) ) {
                    rootToCoordinatorMap_.put ( coord.getCoordinatorId ().intern (),
                            coord );
                }
                startlistening ( coord );
            }
        } catch ( Exception e ) {
            LOGGER.logWarning ( "Error in recoverCoordinators", e );
            throw new SysException ( "Error in recoverCoordinators: "
                    + e.getMessage (), e );
        }

    }

    public String getName ()
    {
        return tmUniqueName_;
    }

    /**
     * @see RecoveryService
     *
     */
    public void recover ()
    {

        if ( ! initialized_ ) {
        		initialized_ = true;
        }

        synchronized ( recoverySynchronizer_ ) {
            // recovery MUST be synchronized to avoid erroneous presumed abort
            // if two different threads interleave!!!
            // for instance: if thread1 starts recovery, but thread2 ends it first, then
            // thread1's endRecovery will REscan the resources in the middle of
            // its recovery scan! this leads to erroneous presumed aborts (since
            // recovery of the first half of the coordinators is no longer considered)

            try {
                Vector coordinators = getCoordinatorImpVector ();
                Iterator it = coordinators.iterator ();
                while ( it.hasNext () ) {
                    CoordinatorImp coord = (CoordinatorImp) it.next ();
                    try {
                        if ( !coord.recover () && LOGGER.isInfoEnabled() )
                        	LOGGER.logInfo ( "Coordinator not recoverable: "
                                    + coord.getCoordinatorId () );
                    } catch ( Exception e ) {
                       // ignore (to avoid VM exit) but log
                        LOGGER.logWarning (
                                "Coordinator not recoverable: "
                                        + coord.getCoordinatorId (), e );

                    }
                }

                Enumeration reslist = Configuration.getResources ();
                while ( reslist.hasMoreElements () ) {
                    RecoverableResource res = (RecoverableResource) reslist
                            .nextElement ();
                    try {
                        res.endRecovery ();
                    } catch ( Exception error ) {
                        LOGGER.logWarning ( "ERROR IN RECOVERY", error );
                        // continue processing to avoid indoubts for other resources
                    }
                }
            } catch ( Exception e ) {
                LOGGER.logWarning ( "Error in recover: "
                        + e.getClass ().getName () + e.getMessage (), e );

                throw new SysException ( "Error in recovering: "
                        + e.getMessage (), e );
            }

        }

    }



    /**
     * Get a LogControl for the service.
     *
     * @return LogControl The instance.
     */

    public com.atomikos.icatch.admin.LogControl getLogControl ()
    {

        return control_;
    }
    
    
    

    /**
     * @see TransactionService
     */

    public CompositeCoordinator getCompositeCoordinator ( String root )
            throws SysException
    {
        return getCoordinatorImp ( root );
    }

    /**
     * @see TransactionService
     */

    public void addTSListener ( TransactionServicePlugin listener )
            throws IllegalStateException
    {

        // NOTE: we do NOT synchronize with init,
        // because compensators will call this method
        // during recovery, and recovery happens inside
        // init!

    		if ( ! tsListeners_.contains ( listener ) ) {
    			tsListeners_.addElement ( listener );
    			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "Added TSListener: " + listener );
    		}


    }

    /**
     * @see TransactionService
     */

    public void removeTSListener ( TransactionServicePlugin listener )
    {

        tsListeners_.removeElement ( listener );
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug  ( "Removed TSListener: " + listener );

    }
    
    PooledAlarmTimer legacyAndObsoleteExecutorService;

    /**
     * @see TransactionService
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        this.initProperties_ = properties;

        try {
            recoverymanager_.init (properties);
        } catch ( LogException le ) {
            throw new SysException ( "Error in init: " + le.getMessage (),
                    le );
        }

        shutdownInProgress_ = false;
        control_ = new com.atomikos.icatch.admin.imp.LogControlImp ( this );
        
        legacyAndObsoleteExecutorService = new PooledAlarmTimer(1000);
        
        legacyAndObsoleteExecutorService.addAlarmTimerListener(new AlarmTimerListener() {
			
			@Override
			public void alarm(AlarmTimer timer) {
				
				Enumeration<RecoverableResource> resources= Configuration.getResources();
				while (resources.hasMoreElements()) {
					RecoverableResource recoverableResource =  resources.nextElement();
					try {
						recoverableResource.recover();
					} catch (Throwable e) {
						LOGGER.logWarning(e.getMessage(),e);
					}
				}
				
			}
		});
        
        TaskManager.getInstance().executeTask(legacyAndObsoleteExecutorService);
        
    }

    /**
     * @see TransactionService
     */

    public Participant getParticipant ( String root ) throws SysException
    {
        return getCoordinatorImp ( root );
    }

    /**
     * @see FSMEnterListener.
     */

    public void entered ( FSMEnterEvent<TxState> event )
    {
        CoordinatorImp cc = (CoordinatorImp) event.getSource ();
        TxState state = event.getState ();
        removeCoordinator(cc);
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
     * @see TransactionService
     */

    public CompositeTransaction getCompositeTransaction ( String tid )
    {
        CompositeTransaction ret = null;

        synchronized ( tidToTransactionMap_ ) {
            ret = (CompositeTransaction) tidToTransactionMap_.get ( tid.intern () );
        }

        return ret;
    }



    /**
     * Creates a subtransaction for the given parent
     *
     * @param parent
     * @return
     */
    CompositeTransaction createSubTransaction ( CompositeTransaction parent )
    {
        CompositeTransactionImp ret = null;
        Stack lineage = (Stack) parent.getLineage ().clone ();
        lineage.push ( parent );
        String tid = tidmgr_.get ();
        CoordinatorImp ccParent = (CoordinatorImp) parent
                .getCompositeCoordinator ();
        // create NEW coordinator for subtx, with most of the parent settings
        // but without orphan checks since subtxs have no orphans
        CoordinatorImp cc = createCC ( null, tid, false ,
                ccParent.prefersHeuristicCommit (), parent.getTimeout () );
        if ( ccParent.isRecoverableWhileActive() != null &&
             ccParent.isRecoverableWhileActive().booleanValue() ) {
            //inherit active recoverability feature
            cc.setRecoverableWhileActive();
        }
        ret = createCT ( tid, cc, lineage, parent.isSerial () );
        ret.noLocalAncestors = false;
        return ret;

    }

    /**
     * @see TransactionService
     */

    public synchronized CompositeTransaction recreateCompositeTransaction (
            Propagation context , boolean orphancheck , boolean heur_commit )
            throws SysException
    {
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        if ( maxNumberOfActiveTransactions_ >= 0 && tidToTransactionMap_.size () >= maxNumberOfActiveTransactions_ )
            throw new IllegalStateException (
                    "Max number of active transactions reached:" + maxNumberOfActiveTransactions_ );

        CoordinatorImp cc = null;
        CompositeTransaction ct = null;

        try {
            String tid = tidmgr_.get ();
            boolean serial = context.isSerial ();
            Stack lineage = context.getLineage ();
            if ( lineage.empty () )
                throw new SysException (
                        "Empty lineage in propagation: empty lineage" );
            Stack tmp = new Stack ();

            while ( !lineage.empty () ) {
                tmp.push ( lineage.pop () );
            }

            CompositeTransaction root = (CompositeTransaction) tmp.peek ();

            while ( !tmp.empty () ) {
                lineage.push ( tmp.pop () );
            }

            CompositeTransaction parent = (CompositeTransaction) lineage
                    .peek ();
            synchronized ( shutdownSynchronizer_ ) {
                synchronized ( getLatch ( root.getTid () ) ) {
                    cc = getCoordinatorImp ( root.getTid () );
                    if ( cc == null ) {
                        RecoveryCoordinator coord = parent
                                .getCompositeCoordinator ()
                                .getRecoveryCoordinator ();
                        cc = createCC ( coord, root.getTid (), orphancheck,
                                heur_commit, context.getTimeOut () );
                    }
                    cc.incLocalSiblingCount (); // for detection of orphans
                }
            }
            ct = createCT ( tid, cc, lineage, serial );

        } catch ( Exception e ) {
            e.printStackTrace ();
            throw new SysException ( "Error in recreate.", e );
        }

        return ct;
    }

    /**
     * @see TransactionService
     */

    public synchronized void shutdown ( boolean force ) throws SysException,
            IllegalStateException
    {

    	
        boolean wasShuttingDown = false;
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: Entering shutdown ( "
                + force + " )..." );

        // following moved out of synch block to avoid deadlock on immediate
        // shutdown with interleaving entered notification of a terminating
        // coordinator state-handler
        if ( !wasShuttingDown && force ) {
            // If we were already shutting down, then the FIRST thread
            // to enter this method will do the following. Don't do
            // it twice.

            Enumeration enumm = rootToCoordinatorMap_.keys ();
            while ( enumm.hasMoreElements () ) {
                String tid = (String) enumm.nextElement ();
                LOGGER.logDebug ( "Transaction Service: Stopping thread for root "
                                + tid + "..." );
                CoordinatorImp c = (CoordinatorImp) rootToCoordinatorMap_
                        .get ( tid );
                if ( c != null ) { //null if intermediate termination while in enumm
                		c.dispose (); //needed for forced shutdown
                }
                LOGGER.logDebug ( "Transaction Service: Thread stopped." );
            }

        } // if wasShuttingDown


        synchronized ( shutdownSynchronizer_ ) {
        	LOGGER.logDebug ( "Transaction Service: Shutdown acquired lock on waiter." );
            wasShuttingDown = shutdownInProgress_;
            shutdownInProgress_ = true;
            // check for active coordinators (who might be indoubt)
            // NOTE: should be thread safe, since createCC
            // is also a synchronized method.
            // Of course, getCoordinator also puts into the
            // roottocoordinatormap_, but that one only adds
            // instances that have been swapped out, hence who
            // can not be indoubt.

            while ( !rootToCoordinatorMap_.isEmpty () && !force ) {
                try {
                	LOGGER.logWarning ( "Transaction Service: Waiting for non-terminated coordinators..." );
                    //wait for max timeout to let actives finish
                    shutdownSynchronizer_.wait ( maxTimeout_ );
                    //PURGE to avoid issue 10079
                    //use a clone to avoid concurrency interference
                    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: Purging coordinators for shutdown..." );
                    Hashtable clone = ( Hashtable ) rootToCoordinatorMap_.clone();
                    Enumeration coordinatorIds = clone.keys();
                    while ( coordinatorIds.hasMoreElements() ) {
                    		String id = ( String ) coordinatorIds.nextElement();
                    		CoordinatorImp c = ( CoordinatorImp ) clone.get ( id );
                    		if ( TxState.TERMINATED.equals ( c.getState() ) ) {
                    			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: removing terminated coordinator: " + id );
                    			rootToCoordinatorMap_.remove ( id );
                    		}
                    }
                    //contine the loop: if not empty then wait again

                } catch ( InterruptedException inter ) {
                	// cf bug 67457
        			InterruptedExceptionHelper.handleInterruptedException ( inter );
                    throw new SysException ( "Error in shutdown: "
                            + inter.getMessage (), inter );
                }
            }

            initialized_ = false;
            if ( !wasShuttingDown ) {
                // If we were already shutting down, then the FIRST thread
                // to enter this method will do the following. Don't do
                // it twice.
                try {
                    recoverymanager_.close ();
                } catch ( LogException le ) {
                    le.printStackTrace();
                    throw new SysException ( "Error in shutdown: "
                            + le.getMessage (), le );
                }
                legacyAndObsoleteExecutorService.stop();
            } 

        }
        
        shutdownSystemExecutors();
    }

	private void shutdownSystemExecutors() {
		TaskManager exec = TaskManager.getInstance();
        if ( exec != null ) {
        		exec.shutdown();
        }
	}

    public synchronized void finalize () throws Throwable
    {

        try {
            if ( !shutdownInProgress_ && initialized_ ) shutdown ( true );
        } catch ( Exception e ) {
            LOGGER.logWarning( "Error in GC of TransactionServiceImp" , e );
        } finally {
            super.finalize ();
        }
    }

    /**
     * @see com.atomikos.icatch.TransactionService#getSuperiorRecoveryCoordinator(java.lang.String)
     */
    public RecoveryCoordinator getSuperiorRecoveryCoordinator ( String root )
    {
        RecoveryCoordinator ret = null;
        CoordinatorImp c = getCoordinatorImp ( root );
        if ( c != null ) {
            ret = c.getSuperiorRecoveryCoordinator ();
        }
        return ret;
    }


    public CompositeTransaction createCompositeTransaction ( long timeout ) throws SysException
    {
        if ( !initialized_ ) throw new IllegalStateException ( "Not initialized" );

        if ( maxNumberOfActiveTransactions_ >= 0 && 
             tidToTransactionMap_.size () >= maxNumberOfActiveTransactions_ ) {
            throw new IllegalStateException ( "Max number of active transactions reached:" + maxNumberOfActiveTransactions_ );
        }
        
        String tid = tidmgr_.get ();
        Stack lineage = new Stack ();
        // create a CC with heuristic preference set to false,
        // since it does not really matter anyway (since we are
        // creating a root)
        CoordinatorImp cc = createCC ( null, tid, true, false, timeout );
        CompositeTransaction ct = createCT ( tid, cc, lineage, false );
        return ct;
    }

	@Override
	public RecoveryService getRecoveryService() {
		return this;
	}

	@Override
	public CoordinatorLogEntry[] getCoordinatorLogEntries() {
		Vector<CoordinatorImp> coordinatorImpVector = getCoordinatorImpVector();
		List<CoordinatorLogEntry> coordinatorLogEntries = new ArrayList<CoordinatorLogEntry>(coordinatorImpVector.size());
		for (CoordinatorImp coordinatorImp : coordinatorImpVector) {
			CoordinatorLogEntry coordinatorLogEntry = coordinatorImp.getCoordinatorLogEntry();
			if (coordinatorLogEntry != null) {
				coordinatorLogEntries.add(coordinatorLogEntry);	
			}
			
		}
		return coordinatorLogEntries.toArray(new CoordinatorLogEntry[coordinatorLogEntries.size()]);
	}

	@Override
	public void remove(String coordinatorId) {
		Vector<CoordinatorImp> coordinatorImpVector = getCoordinatorImpVector();
		for (CoordinatorImp coordinatorImp : coordinatorImpVector) {
			if(coordinatorImp.getId().equals(coordinatorId)){
				coordinatorImp.forget();
			}
		}
	}

	@Override
	public RecoveryLog getRecoveryLog() {
		return this.recoveryLog;
	}

}
