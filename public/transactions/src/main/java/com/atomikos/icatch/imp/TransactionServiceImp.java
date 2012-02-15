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

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.diagnostics.Console;
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
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.thread.InterruptedExceptionHelper;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.util.UniqueIdMgr;

/**
 * 
 * 
 * General implementation of Transaction Service.
 */

public class TransactionServiceImp implements TransactionService,
        FSMEnterListener, SubTxAwareParticipant, RecoveryService
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionServiceImp.class);

    private static final int NUMLATCHES = 97;
    // a number of latches to lock on a per-root basis
    // this is the size of a hash array of latch objects


    private long maxTimeout_;
    // timeout for new or import txs must be lower

    private Object[] rootlatches_ = null;
    // for latching on a per-root basis

    private Hashtable tidtotxmap_ = null;
    // maps tid to tx instance

    private Hashtable roottocoordinatormap_ = null;
    // maps root tid to composite coordinator instances

    private boolean shuttingDown_ = false;
    // true asa shutdown called.
    // new txs should not be started

    private Object shutdownWaiter_;
    // for coordinating shutdown / start of new coordinators

    private Object recoveryWaiter_;
    // for synchronizing recovery scans

    private UniqueIdMgr tidmgr_ = null;
    // for creating new Strings

    private StateRecoveryManager recoverymanager_ = null;
    // recovery manager responsible for saving states

    private boolean initialized_ = false;
    // true asa initialize was called.

    private Console console_;
    // the console to log to

    private LogControl control_;
    // for admin tool

    private boolean otsOverride_;
    // true for forced compatibility with OTS;
    // in that case no creation preferences are taken into account
    // concerning orphan checks

    private Vector listeners_;
    // the TSListener objects.
    private int maxActives_;
    // the max number of active transactions, or -1 if unlimited

    private String name_;
    
    // the unique name of this TS, needed for recovery
    // of the resources, and for supplying to each
    // resource to constructs XIDs with
    
    private Properties properties_;
    //the properties used to initialize the system
    
    private boolean single_threaded_2pc_;
    //should 2PC happen in the same thread as the application code?

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
            Console console , long maxtimeout , int maxActives , boolean single_threaded_2pc )
    {
        this ( name , recoverymanager , tidmgr , console , maxtimeout , true ,
                maxActives , single_threaded_2pc );
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
     *
     */

    public TransactionServiceImp ( String name ,
            StateRecoveryManager recoverymanager , UniqueIdMgr tidmgr ,
            Console console , long maxtimeout , boolean checkorphans ,
            int maxActives , boolean single_threaded_2pc )
    {
        maxActives_ = maxActives;
        if ( !checkorphans )
            otsOverride_ = true;
        else
            otsOverride_ = false;

        initialized_ = false;
        recoverymanager_ = recoverymanager;
        tidmgr_ = tidmgr;
        tidtotxmap_ = new Hashtable ();
        shutdownWaiter_ = new Object ();
        recoveryWaiter_ = new Object ();
        roottocoordinatormap_ = new Hashtable ();
        rootlatches_ = new Object[NUMLATCHES];
        for ( int i = 0; i < NUMLATCHES; i++ ) {
            rootlatches_[i] = new Object ();
        }
        console_ = console;
        maxTimeout_ = maxtimeout;
        name_ = name;
        listeners_ = new Vector ();
        single_threaded_2pc_ = single_threaded_2pc;
    }

    /**
     * Get an object to lock for the given root. To increase concurrency and
     * still provide atomic operations within the scope of one root.
     * 
     * @return Object The object to lock for the given root.
     */

    protected Object getLatch ( String root )
    {
        return rootlatches_[Math.abs ( root.toString ().hashCode ()
                % NUMLATCHES )];
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
        synchronized ( tidtotxmap_ ) {
            if ( tidtotxmap_.containsKey ( tid.intern () ) )
                throw new IllegalStateException ( "Already mapped: " + tid );
            tidtotxmap_.put ( tid.intern (), ct );
            // at the same time, start listening for removal,
            // to allow GC of hashtable contents
            ct.addSubTxAwareParticipant ( this );
        }
    }

    /**
     * For inspector tool: get a list of all active coordinator instances, to
     * allow admin intervention.
     * 
     * @return Vector A copy of the list of active coordinators. Empty vector if
     *         none.
     */

    Vector getCoordinatorImpVector ()
    {
        Vector ret = new Vector ();
        Enumeration tids = roottocoordinatormap_.keys ();
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
     * Utility method to notify the registered listeners
     * 
     * @param init
     *            True for init, false for shutdown.
     * @param before
     *            True if init/shutdown is about to be done, false if it has
     *            been done already.
     */

    private void notifyListeners ( boolean init , boolean before )
    {
    		
        Enumeration enumm = listeners_.elements ();
        while ( enumm.hasMoreElements () ) {
            TSListener l = (TSListener) enumm.nextElement ();
            try {
	            if ( init ) {
	                l.init ( before , properties_ );
	            } else {
	                l.shutdown ( before );
	            }
            }
            catch ( Exception e ) {
            		Configuration.logWarning ( "Error in TSListener" , e );
            }
        }

    }

    /**
     * Removes the coordinator from the root map.
     * 
     * @param coord
     *            The coordinator to remove.
     */

    private void removeCoordinator ( CompositeCoordinator coord )
    {

        synchronized ( shutdownWaiter_ ) {
            synchronized ( getLatch ( coord.getCoordinatorId ().intern () ) ) {

                roottocoordinatormap_.remove ( coord.getCoordinatorId ().intern () );
            }

            // notify any waiting threads for shutdown
            if ( roottocoordinatormap_.isEmpty () )
                shutdownWaiter_.notifyAll ();
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
        tidtotxmap_.remove ( ct.getTid ().intern () );

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
            Configuration.logWarning ( "Attempt to create a transaction with a timeout that exceeds " + 
            		AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME + " - truncating to: " + maxTimeout_ );
        }

        // synch for coordinator with :
        // no new coordinators started if shutting down.
        synchronized ( shutdownWaiter_ ) {
            // check if shutting down -> do not allow new coordinator objects
            // to be added, so that shutdown will eventually succeed.
            if ( shuttingDown_ )
                throw new IllegalStateException ( "Server is shutting down..." );

            if ( otsOverride_ ) {
                // forced OTS mode; we do NEVER check orphans in this case
                checkOrphans = false;
            }
            cc = new CoordinatorImp ( root, adaptor, console_,
                    heuristic_commit, timeout,
                    checkOrphans , single_threaded_2pc_ );

            recoverymanager_.register ( cc );

            // now, add to root map, since we are sure there are not too
            // many active txs
            synchronized ( getLatch ( root.intern () ) ) {
                roottocoordinatormap_.put ( root.intern (), cc );
            }
            startlistening ( cc );
        }// synch shutdownWaiter

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
        Hashtable forgetStates = new Hashtable ();
        // forgetStates are those that lead to a removal of the coordinator
        // such that it only exists in the log but no longer in CM

        // heuristic states are NO LONGER reason for removal:
        // this can lead to multiple instances of SAME coordinator
        // which causes inconsistent outcomes!

        // forgetStates.put ( TxState.HEUR_HAZARD , new Object() );
        // forgetStates.put ( TxState.HEUR_MIXED , new Object() );
        // forgetStates.put ( TxState.HEUR_ABORTED , new Object() );
        // forgetStates.put ( TxState.HEUR_COMMITTED , new Object() );

        forgetStates.put ( TxState.TERMINATED, new Object () );

        Object[] finalStates = coordinator.getFinalStates ();
        for ( int i = 0; i < finalStates.length; i++ ) {
            forgetStates.put ( finalStates[i], new Object () );
        }

        Enumeration enumm = forgetStates.keys ();

        while ( enumm.hasMoreElements () ) {
            Object state = enumm.nextElement ();
            coordinator.addFSMEnterListener ( this, state );
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
        Stack errors = new Stack ();
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CoordinatorImp cc = null;
        synchronized ( shutdownWaiter_ ) {
            // Synch on shutdownwaiter first to avoid
            // deadlock, even if we don't seem to need it here

            synchronized ( getLatch ( root ) ) {
                cc = (CoordinatorImp) roottocoordinatormap_.get ( root
                        .intern () );
                if ( cc == null ) {
                    // swapped out coordinator already, OR NONEXISTENT!
                    try {
                        cc = (CoordinatorImp) recoverymanager_.recover ( root );
                    } catch ( LogException le ) {
                        errors.push ( le );
                        throw new SysException (
                                "Error in getting coordinator: "
                                        + le.getMessage (), errors );
                    }
                    if ( cc != null ) {
                        startlistening ( cc );
                        roottocoordinatormap_.put ( root.intern (), cc );
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
        Stack errors = new Stack ();

        try {

            Vector recovered = recoverymanager_.recover ();
            Enumeration enumm = recovered.elements ();
            while ( enumm.hasMoreElements () ) {
                CoordinatorImp coord = (CoordinatorImp) enumm.nextElement ();
                synchronized ( getLatch ( coord.getCoordinatorId ().intern () ) ) {
                    roottocoordinatormap_.put ( coord.getCoordinatorId ().intern (),
                            coord );
                }
                startlistening ( coord );

                // for swapping out after indoubt resolved.
            }
        } catch ( Exception e ) {
            Configuration.logWarning ( "Error in recoverCoordinators", e );
            errors.push ( e );
            throw new SysException ( "Error in recoverCoordinators: "
                    + e.getMessage (), errors );
        }

    }

    public String getName ()
    {
        return name_;
    }

    /**
     * @see RecoveryService
     * 
     */
    public void recover ()
    {

        // MAKE SURE THAT THE RECOVERY SERVICE IS SET OR PRESUMED ABORT
        // WILL BE WRONG
        if ( Configuration.getTransactionService () == null ) {
            Configuration.installTransactionService ( this );
            Configuration.installRecoveryService ( this );
            
        }
        
        //call listeners here: pending listeners may have been installed
        //by the installation step above
        if ( ! initialized_ ) {
        		//only call if not initialized or listeners will get
        		//multiple callbacks, once for each recovery scan!!!
        		notifyListeners ( true, true );
        		initialized_ = true;
        }

        synchronized ( recoveryWaiter_ ) {
            // recovery MUST be synchronized to avoid erroneous presumed abort
            // if two different threads interleave!!!
            // for instance: if thread1 starts recovery, but thread2 ends it
            // first, then
            // thread1's endRecovery will REscan the resources in the middle of
            // its
            // recovery scan! this leads to erroneous presumed aborts (since
            // recovery
            // of the first half of the coordinators is no longer considered)

            try {
                Vector coordinators = getCoordinatorImpVector ();
                Iterator it = coordinators.iterator ();
                while ( it.hasNext () ) {
                    CoordinatorImp coord = (CoordinatorImp) it.next ();
                    try {
                        if ( !coord.recover () && console_ != null )
                            console_.println ( "Coordinator not recoverable: "
                                    + coord.getCoordinatorId () );
                    } catch ( Exception e ) {
                        // ADDED FOR TOMCAT RELEASE
                        // this coordinator is not recoverable, for instance
                        // because one of the
                        // resource in unreachable. Make sure that this does not
                        // crash
                        // the TM so don't throw anything here.
                        Configuration.logWarning (
                                "Coordinator not recoverable: "
                                        + coord.getCoordinatorId (), e );

                    }
                }

                // next, notify all registered resources that recovery is done:
                // any non-collected restxs should be aborted at this time
                Enumeration reslist = Configuration.getResources ();

                while ( reslist.hasMoreElements () ) {
                    RecoverableResource res = (RecoverableResource) reslist
                            .nextElement ();
                    try {
                        res.endRecovery ();
                    } catch ( Exception error ) {
                        Configuration.logWarning ( "ERROR IN RECOVERY", error );
                        // CONTINUE PROCESSING: JUST BECAUSE ONE RESOURCE
                        // DOES BAD THINGS DOESN'T MEAN EVERYONE MUST STAY
                        // IN DOUBT
                    }
                }
            } catch ( Exception e ) {
                Configuration.logWarning ( "Error in recover: "
                        + e.getClass ().getName () + e.getMessage (), e );

                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException ( "Error in recovering: "
                        + e.getMessage (), errors );
            }

        }// end synchronized

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

    public void addTSListener ( TSListener listener )
            throws IllegalStateException
    {

        // NOTE: we do NOT synchronize with init,
        // because compensators will call this method
        // during recovery, and recovery happens inside
        // init!

    		if ( ! listeners_.contains ( listener ) ) {
	        listeners_.addElement ( listener );
	        if ( initialized_ ) {
	            listener.init ( false , properties_ );
	        }
	        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "Added TSListener: " + listener ); 
    		}
        

    }

    /**
     * @see TransactionService
     */

    public void removeTSListener ( TSListener listener )
    {

        listeners_.removeElement ( listener );
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug  ( "Removed TSListener: " + listener );

    }

    /**
     * @see TransactionService
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        Stack errors = new Stack ();
        this.properties_ = properties;
        
        try {
            recoverymanager_.init ();
        } catch ( LogException le ) {
            errors.push ( le );
            throw new SysException ( "Error in init: " + le.getMessage (),
                    errors );
        }
        recoverCoordinators ();
        
        //initialized is now set in recover()
        //initialized_ = true;
        
        shuttingDown_ = false;
        control_ = new LogControlImp ( this );
        // call recovery already, to make sure that the
        // RMI participants can start inquiring and replay

        recover ();
        notifyListeners ( true, false );
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

    public void entered ( FSMEnterEvent event )
    {
        CoordinatorImp cc = (CoordinatorImp) event.getSource ();
        Object state = event.getState ();

        // in all cases, allow GC to cleanup instance

        removeCoordinator ( cc );
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

        synchronized ( tidtotxmap_ ) {
            ret = (CompositeTransaction) tidtotxmap_.get ( tid.intern () );
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
        //but without orphan checks since subtxs have no orphans
        CoordinatorImp cc = createCC ( null, tid, false ,
                ccParent.prefersHeuristicCommit (), parent.getTimeout () );
        if ( ccParent.isRecoverableWhileActive() != null &&
             ccParent.isRecoverableWhileActive().booleanValue() ) {
            //inherit active recoverability feature
            cc.setRecoverableWhileActive();
        }
        ret = createCT ( tid, cc, lineage, parent.isSerial () );
        ret.localRoot_ = false;
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

        if ( maxActives_ >= 0 && tidtotxmap_.size () >= maxActives_ )
            throw new IllegalStateException (
                    "Max number of active transactions reached:" + maxActives_ );

        Stack errors = new Stack ();
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
            synchronized ( shutdownWaiter_ ) {
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
            errors.push ( e );
            e.printStackTrace ();
            throw new SysException ( "Error in recreate.", errors );
        }

        return ct;
    }

    /**
     * @see TransactionService
     */

    public synchronized void shutdown ( boolean force ) throws SysException,
            IllegalStateException
    {
        Stack errors = new Stack ();
        boolean wasShuttingDown = false;
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: Entering shutdown ( "
                + force + " )..." );

        // FOLLOWING MOVED OUT OF SYNCH BLOCK TO HERE TO AVOID DEADLOCK ON
        // IMMEDIATE SHUTDOWN WITH INTERLEAVING ENTERED NOTIFICATION OF
        // TERMINATING COORDINATOR STATEHANDLER
        if ( !wasShuttingDown && force ) {
            // If we were already shutting down, then the FIRST thread
            // to enter this method will do the following. Don't do
            // it twice.

            // notify all remaining coordinators to stop threads
            // needed in case of force!

            Enumeration enumm = roottocoordinatormap_.keys ();
            while ( enumm.hasMoreElements () ) {
                String tid = (String) enumm.nextElement ();
                Configuration
                        .logDebug ( "Transaction Service: Stopping thread for root "
                                + tid + "..." );
                CoordinatorImp c = (CoordinatorImp) roottocoordinatormap_
                        .get ( tid );
                if ( c != null ) {
                		//null if intermediate termination while in enumm
                		c.dispose ();
                }
                Configuration
                        .logDebug ( "Transaction Service: Thread stopped." );
            }

        } // if wasShuttingDown

        synchronized ( shutdownWaiter_ ) {
            Configuration
                    .logDebug ( "Transaction Service: Shutdown acquired lock on waiter." );
            wasShuttingDown = shuttingDown_;
            shuttingDown_ = true;

            // check for active coordinators (who might be indoubt)
            // NOTE: should be thread safe, since createCC
            // is also a synchronized method.
            // Of course, getCoordinator also puts into the
            // roottocoordinatormap_, but that one only adds
            // instances that have been swapped out, hence who
            // can not be indoubt.

            // System.err.println ( "Transaction Service: Checking for existing
            // txs..." );
            while ( !roottocoordinatormap_.isEmpty () && !force ) {
                try {
                    Configuration
                            .logWarning ( "Transaction Service: Waiting for non-terminated coordinators..." );
                    //wait for max timeout to let actives finish
                    shutdownWaiter_.wait ( maxTimeout_ );
                    //PURGE to avoid issue 10079
                    //use a clone to avoid concurrency interference
                    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: Purging coordinators for shutdown..." );
                    Hashtable clone = ( Hashtable ) roottocoordinatormap_.clone();
                    Enumeration coordinatorIds = clone.keys();
                    while ( coordinatorIds.hasMoreElements() ) {
                    		String id = ( String ) coordinatorIds.nextElement();
                    		CoordinatorImp c = ( CoordinatorImp ) clone.get ( id );
                    		if ( TxState.TERMINATED.equals ( c.getState() ) ) {
                    			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Transaction Service: removing terminated coordinator: " + id );
                    			roottocoordinatormap_.remove ( id );
                    		}
                    }
                    //contine the loop: if not empty then wait again
                    
                } catch ( InterruptedException inter ) {
                	// cf bug 67457
        			InterruptedExceptionHelper.handleInterruptedException ( inter );
                    errors.push ( inter );
                    throw new SysException ( "Error in shutdown: "
                            + inter.getMessage (), errors );
                }
            }
            // System.err.println ( "Transaction Service: Check Done." );
            notifyListeners ( false, true );
            initialized_ = false;
            if ( !wasShuttingDown ) {
                // If we were already shutting down, then the FIRST thread
                // to enter this method will do the following. Don't do
                // it twice.

                try {
                    // System.err.println ( "Transaction Service: Closing
                    // logs..." );
                    recoverymanager_.close ();
                    // System.err.println ( "Transaction Service: Logs Closed."
                    // );
                } catch ( LogException le ) {
                    errors.push ( le );
                    le.printStackTrace();
                    throw new SysException ( "Error in shutdown: "
                            + le.getMessage (), errors );
                }
                // recoverymanager_ = null;
                // removed because repeated start/shutdown will fail cause of
                // this.
            } // if wasShuttingDown

        }// synch shutdownWaiter

        notifyListeners ( false, false );
    }

    public synchronized void finalize () throws Throwable
    {

        try {
            if ( !shuttingDown_ && initialized_ )
                shutdown ( true );

        } catch ( Exception e ) {
            System.err.println ( "Error in GC of TransactionServiceImp" );
            System.err.println ( e.getMessage () );
            e.printStackTrace ();
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
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        if ( maxActives_ >= 0 && tidtotxmap_.size () >= maxActives_ )
            throw new IllegalStateException (
                    "Max number of active transactions reached:" + maxActives_ );

        String tid = tidmgr_.get ();
        Stack lineage = new Stack ();
        // create a CC with heuristic preference set to false,
        // since it does not really matter anyway (since we are
        // creating a ROOT!)

        CoordinatorImp cc = createCC ( null, tid, true, false, timeout );
        CompositeTransaction ct = createCT ( tid, cc, lineage, false );
        return ct;
    }

}
