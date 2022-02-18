/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

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
import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.TransactionServiceProvider;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.recovery.fs.RecoveryLogImp;
import com.atomikos.thread.InterruptedExceptionHelper;
import com.atomikos.thread.TaskManager;
import com.atomikos.util.UniqueIdMgr;

/**
 * General implementation of Transaction Service.
 */

public class TransactionServiceImp implements TransactionServiceProvider,
        FSMEnterListener, SubTxAwareParticipant, RecoveryService
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionServiceImp.class);
    private static final int NUMLATCHES = 97;
    private static final Object shutdownSynchronizer = new Object();

    
    private long maxTimeout_;
    private Object[] rootLatches_ = null;
    private Hashtable<String,CompositeTransaction> tidToTransactionMap_ = null;
    private Map<String, CoordinatorImp> recreatedCoordinatorsByRootId = new HashMap<>();
    private Map<String, CoordinatorImp> allCoordinatorsByCoordinatorId = new HashMap<>();
    private boolean shutdownInProgress_ = false;
    private UniqueIdMgr tidmgr_ = null;
    private StateRecoveryManager recoverymanager_ = null;
    private boolean initialized_ = false;
   

    private Set<TransactionServicePlugin> tsListeners = new HashSet<>();
    private int maxNumberOfActiveTransactions_;
    private String tmUniqueName_;
    private boolean single_threaded_2pc_;
	private RecoveryLog recoveryLog;	
	private RecoveryDomainService recoveryDomainService;


    /**
     * Create a new instance, with orphan checking set.
     *
     * @param name
     *            The unique name of this TM.
     * @param recoverymanager
     *            The recovery manager to use.
     * @param tidmgr
     *            The String manager to use.
     * @param maxtimeout
     *            The max timeout for new or imported txs.
     *            
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
             long maxtimeout , 
            int maxActives , boolean single_threaded_2pc, RecoveryLog recoveryLog )
    {
        maxNumberOfActiveTransactions_ = maxActives;
       
        initialized_ = false;
        recoverymanager_ = recoverymanager;
        tidmgr_ = tidmgr;
        tidToTransactionMap_ = new Hashtable<String,CompositeTransaction>();
        rootLatches_ = new Object[NUMLATCHES];
        for (int i = 0; i < NUMLATCHES; i++) {
            rootLatches_[i] = new Object();
        }

        maxTimeout_ = maxtimeout;	
        
        tmUniqueName_ = name;
        single_threaded_2pc_ = single_threaded_2pc;
        this.recoveryLog  = recoveryLog;
        this.recoveryDomainService = new RecoveryDomainService(recoveryLog);
    }

    /**
     * Get an object to lock for the given root. To increase concurrency and
     * still provide atomic operations within the scope of one root.
     *
     * @return Object The object to lock for the given root.
     */
    private Object getLatch ( String root )
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

    private void setTidToTx ( String tid , CompositeTransaction ct )
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
     * Removes the coordinator from the root map.
     *
     * @param coord
     *            The coordinator to remove.
     */

    private void removeCoordinator ( CompositeCoordinator coord )
    {

        synchronized ( shutdownSynchronizer ) {
            synchronized ( getLatch ( coord.getRootId()) ) {
                recreatedCoordinatorsByRootId.remove (coord.getRootId());
                allCoordinatorsByCoordinatorId.remove(coord.getCoordinatorId());
            }

            // notify any waiting threads for shutdown
            if ( allCoordinatorsByCoordinatorId.isEmpty() )
                shutdownSynchronizer.notifyAll ();
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
            CoordinatorImp coordinator , Stack<CompositeTransaction> lineage , boolean serial )
            throws SysException
    {
    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Creating composite transaction: " + tid );
        CompositeTransactionImp ct = new CompositeTransactionImp ( this,
                lineage, tid, serial, coordinator );

        setTidToTx ( ct.getTid (), ct );
        coordinator.incLocalSiblingsStarted(); //orphan detection and timeout handling
        return ct;
    }

    /**
     * Creation method for composite coordinators.
     *
     * @param recoveryDomainName The recovery domain of the superior of this coordinator.
     * 
     * @param RecoveryCoordinator
     *            An existing coordinator for the given root. Null if not a
     *            subtx, or an <b>adaptor</b> in other cases.
     * @param lineage
     *            The ancestor information.
     * @param root
     *            The root id.
     * @param timeout
     *            The timeout for indoubt states. After this time, indoubts are
     *            terminated heuristically according to the given strategy.
     *
     * @return CoordinatorImp.
     */

    private CoordinatorImp createCC (String recoveryDomainName, RecoveryCoordinator adaptor ,
            String root, long timeout )
    {
        CoordinatorImp cc = null;

        if (maxTimeout_ > 0 &&  timeout > maxTimeout_ ) {
            timeout = maxTimeout_;
            //FIXED 20188
            LOGGER.logWarning ( "Attempt to create a transaction with a timeout that exceeds maximum - truncating to: " + maxTimeout_ );
        }

        synchronized ( shutdownSynchronizer ) {
            // check if shutting down -> do not allow new coordinator objects
            // to be added, so that shutdown will eventually succeed.
            if ( shutdownInProgress_ )
                throw new IllegalStateException ( "Server is shutting down..." );

           
            String coordinatorId = root;
            boolean subTransaction = (adaptor != null);
            if (subTransaction) { //not a root
            	coordinatorId = tidmgr_.get();
            }
            cc = new CoordinatorImp (recoveryDomainName, coordinatorId, root, adaptor, timeout, single_threaded_2pc_ );

            recoverymanager_.register ( cc );

            // now, add to root map, since we are sure there are not too many active txs
            synchronized ( getLatch ( root) ) {
                CoordinatorImp entryForRoot = recreatedCoordinatorsByRootId.get(root); 
                if (entryForRoot == null) { //cf case 178075
                	recreatedCoordinatorsByRootId.put(root, cc);
                }
                allCoordinatorsByCoordinatorId.put(coordinatorId, cc);
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
			if(txState.isFinalStateForOltp()) {
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

    private CoordinatorImp getCoordinatorImpForRoot ( String root )
            throws SysException
    {
        root = root.intern ();
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CoordinatorImp cc = null;
        synchronized ( shutdownSynchronizer ) {
            // Synch on shutdownSynchronizer_ first to avoid
            // deadlock, even if we don't seem to need it here

            synchronized ( getLatch ( root ) ) {
                cc = recreatedCoordinatorsByRootId.get(root);
            }
        }

        return cc;
    }
    
   
    public String getName ()
    {
        return tmUniqueName_;
    }

    

    /**
     * @see TransactionService
     */

    public CompositeCoordinator getCompositeCoordinator ( String root )
            throws SysException
    {
        return getCoordinatorImpForRoot ( root );
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

    	tsListeners.add( listener );
    	if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace (  "Added TSListener: " + listener );

    }

    /**
     * @see TransactionService
     */

    public void removeTSListener ( TransactionServicePlugin listener )
    {

        tsListeners.remove(listener);
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace  ( "Removed TSListener: " + listener );

    }
    

    /**
     * @see TransactionService
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        shutdownInProgress_ = false;
		recoveryDomainService.init();;
        initialized_ = true;
    }

    /**
     * @see TransactionService
     */

    public Participant getParticipant ( String root ) throws SysException
    {
        return getCoordinatorImpForRoot ( root );
    }

    /**
     * @see FSMEnterListener.
     */

    public void entered ( FSMEnterEvent event )
    {
        CoordinatorImp cc = (CoordinatorImp) event.getSource ();
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
    @SuppressWarnings("unchecked")
    CompositeTransaction createSubTransaction ( CompositeTransaction parent )
    {
    	if (Configuration.getConfigProperties().getAllowSubTransactions()) {
    		CompositeTransactionImp ret = null;
    		Stack<CompositeTransaction> lineage = (Stack<CompositeTransaction>) parent.getLineage ().clone ();
    		lineage.push ( parent );
    		String tid = tidmgr_.get ();
    		CoordinatorImp ccParent = (CoordinatorImp) parent
    				.getCompositeCoordinator ();
    		SubTransactionRecoveryCoordinator rc = new SubTransactionRecoveryCoordinator(ccParent.getCoordinatorId(), tmUniqueName_);
    		// create NEW coordinator for subtx, with most of the parent settings
    		// but without orphan checks since subtxs have no orphans
    		CoordinatorImp cc = createCC ( tmUniqueName_, rc, parent.getCompositeCoordinator().getRootId(), parent.getTimeout () );
    		ret = createCT ( tid, cc, lineage, parent.isSerial () );
    		ret.noLocalAncestors = false;
    		return ret;
    	} else {
    		throw new SysException("Subtransactions not allowed - set config property com.atomikos.icatch.allow_subtransactions=true to enable");
    	}
    	
    }

    /**
     * @see TransactionService
     */

    public synchronized CompositeTransaction recreateCompositeTransaction (Propagation context) throws SysException {
        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );
        
        if (!tmUniqueName_.equals(context.getRecoveryDomainName()) && !usesDefaultRecovery()) {
            throw new IllegalArgumentException("Cannot import a transaction from a different recovery domain: " + 
                    context.getRecoveryDomainName() + ".\n" +
                    "Only transactions within the same domain (a.k.a. LogCloud) are allowed!");
        }

        if ( maxNumberOfActiveTransactions_ >= 0 && tidToTransactionMap_.size () >= maxNumberOfActiveTransactions_ )
            throw new IllegalStateException (
                    "Max number of active transactions reached:" + maxNumberOfActiveTransactions_ );

        CoordinatorImp cc = null;
        CompositeTransaction ct = null;

        try {
            String tid = tidmgr_.get ();
            boolean serial = context.isSerial ();
            
            CompositeTransaction root = context.getRootTransaction();
            CompositeTransaction parent = context.getParentTransaction();
            
            synchronized ( shutdownSynchronizer ) {
                synchronized ( getLatch ( root.getTid () ) ) {
                    cc = getCoordinatorImpForRoot ( root.getTid () );
                    if ( cc == null ) {
                        RecoveryCoordinator coord = parent
                                .getCompositeCoordinator ()
                                .getRecoveryCoordinator ();
                        cc = createCC (context.getRecoveryDomainName(), coord, root.getTid (), context.getTimeout () );
                    }
                }
            }
            ct = createCT ( tid, cc, context.getLineage(), serial );

        } catch ( Exception e ) {
            throw new SysException ( "Error in recreate.", e );
        }

        return ct;
    }
    
    private boolean usesDefaultRecovery() {
        return Configuration.getRecoveryLog() instanceof RecoveryLogImp;
    }

    /**
     * @see TransactionService
     */

    public void shutdown ( boolean force ) {
    	shutdown(force, Long.MAX_VALUE);
    }
    
    public void shutdown(long maxWaitTime) {
    	shutdown(false, maxWaitTime);
    }
    
    private void shutdown(boolean force, long maxWaitTime) {
    	
        boolean wasShuttingDown = false;
       LOGGER.logInfo ( "Entering shutdown (" + force + ", " + maxWaitTime + ")..." );

        // following moved out of synch block to avoid deadlock on immediate
        // shutdown with interleaving entered notification of a terminating
        // coordinator state-handler
        if ( !wasShuttingDown && force ) {
            // If we were already shutting down, then the FIRST thread
            // to enter this method will do the following. Don't do
            // it twice.

            for (String next : allCoordinatorsByCoordinatorId.keySet()) {
                 LOGGER.logTrace ( "Stopping thread for coordinatorId "
                                 + next + "..." );
                 CoordinatorImp c  = allCoordinatorsByCoordinatorId.get(next);
                 if (c != null) { // null on concurrent termination / removal
                	 c.dispose (); // needed for forced shutdown
                 }
                 LOGGER.logTrace ( "Thread stopped." );
            }
               
            

        } 


        synchronized ( shutdownSynchronizer ) {
        	LOGGER.logTrace ( "Shutdown acquired lock on waiter." );
            wasShuttingDown = shutdownInProgress_;
            shutdownInProgress_ = true;
            
            if (!force) {                
                recoveryDomainService.performRecovery();
                recoveryLog.closing(); // allow other node to take over
                ConditionalWaiter waiter = new ConditionalWaiter(maxWaitTime);
                boolean timeout = waiter.waitWhile(() -> {
                    boolean allCoordinatorsDone = allCoordinatorsByCoordinatorId.isEmpty();
                    boolean recoveryDone = !recoveryDomainService.hasMoreToRecover();
                    boolean waiting = !allCoordinatorsDone || !recoveryDone;
                    if (waiting) {
                        LOGGER.logWarning("Shutdown waiting: allCoordinatorsDone=" + allCoordinatorsDone + ", recoveryDone="+recoveryDone);
                    }
                    return !allCoordinatorsDone || !recoveryDone;
                });
                if (usesDefaultRecovery()) {
                    if (timeout) {
                        LOGGER.logWarning("Shutdown leaves pending transactions in log - do NOT delete logfiles!");
                    } else {
                        LOGGER.logInfo("Shutdown leaves no pending transactions - ok to delete logfiles");
                    }
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
                    throw new SysException ( "Error in shutdown: "
                            + le.getMessage (), le );
                }
                recoveryDomainService.stop();
                recoveryLog.closed();
            } 

        }
        
        shutdownSystemExecutors();
    }

	private void shutdownSystemExecutors() {
		TaskManager exec = TaskManager.SINGLETON;
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

    public CompositeTransaction createCompositeTransaction ( long timeout ) throws SysException
    {
        if ( !initialized_ ) throw new IllegalStateException ( "Not initialized" );

        if ( maxNumberOfActiveTransactions_ >= 0 && 
             tidToTransactionMap_.size () >= maxNumberOfActiveTransactions_ ) {
            throw new IllegalStateException ( "Max number of active transactions reached:" + maxNumberOfActiveTransactions_ );
        }
        
        String tid = tidmgr_.get ();
        Stack<CompositeTransaction> lineage = new Stack<CompositeTransaction>();
        // create a CC with heuristic preference set to false,
        // since it does not really matter anyway (since we are
        // creating a root)
        CoordinatorImp cc = createCC(tmUniqueName_, null, tid, timeout);
        CompositeTransaction ct = createCT ( tid, cc, lineage, false );
        return ct;
    }

	@Override
	public RecoveryService getRecoveryService() {
		return this;
	}

	@Override
	public RecoveryLog getRecoveryLog() {
		return this.recoveryLog;
	}

    @Override
    public void transactionSuspended(CompositeTransaction ct) {
        CoordinatorImp cc = (CoordinatorImp) ct.getCompositeCoordinator();
        try {
            cc.incLocalSiblingsTerminated(); //allow rollback on timeout
        } catch (Exception e) {
            //ignore: courtesy method
            LOGGER.logDebug("Unexpected error trying to suspend transaction - ignoring", e);
        }
    }

    @Override
    public void transactionResumed(CompositeTransaction ct) {
        CoordinatorImp cc = (CoordinatorImp) ct.getCompositeCoordinator();
        try {
            cc.incLocalSiblingsStarted(); //prevent rollback on timeout
        } catch (Exception e) {
            //ignore: courtesy method
            LOGGER.logDebug("Unexpected error trying to resume transaction - ignoring", e);
        }
    }

	@Override
	public boolean performRecovery() {
		boolean perform = performRecoveryPass();
		if (perform) {
			try {
				Thread.currentThread().sleep(maxTimeout_ + 1000);
			} catch (InterruptedException e) {
				InterruptedExceptionHelper.handleInterruptedException(e);
			} 
			performRecoveryPass();
		}
		return perform;
	}

	protected boolean performRecoveryPass() {
		boolean ret = false;
		RecoveryDomainService rds = recoveryDomainService;
		if (rds != null) { // null on concurrent shutdown
			ret = rds.performRecovery();
		}
		return ret;
	}

	@Override
	public boolean performRecovery(boolean lax) {
		return performRecovery();
	}

}
