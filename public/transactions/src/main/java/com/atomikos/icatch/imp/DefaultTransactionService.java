/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;

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
import com.atomikos.thread.ConditionalWaiter;
import com.atomikos.thread.InterruptedExceptionHelper;
import com.atomikos.thread.TaskManager;
import com.atomikos.util.UniqueIdMgr;

/**
 * General implementation of Transaction Service.
 * 
 * See see https://www.atomikos.com/Documentation/ConcurrencyModel for threading design.
 */

public class DefaultTransactionService implements TransactionServiceProvider,
        FSMEnterListener, SubTxAwareParticipant, RecoveryService
{
	private static final Logger LOGGER = LoggerFactory.createLogger(DefaultTransactionService.class);
    private static final int NUMLATCHES = 97;
    private static final ReadWriteLock shutdownSynchronizer = new ReentrantReadWriteLock();

    
    private long maxTimeout_;
    private Object[] rootLatches_ = null;
    private Map<String,CompositeTransaction> tidToTransactionMap_;
    private Map<String, CoordinatorImp> recreatedCoordinatorsByRootId = new ConcurrentHashMap<>();
    private Map<String, CoordinatorImp> allCoordinatorsByCoordinatorId = new ConcurrentHashMap<>();
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
     * @param recoveryLog
     *
     */

    public DefaultTransactionService ( String name ,
            StateRecoveryManager recoverymanager , UniqueIdMgr tidmgr ,
             long maxtimeout , 
            int maxActives , boolean single_threaded_2pc, RecoveryLog recoveryLog )
    {
        maxNumberOfActiveTransactions_ = maxActives;
       
        initialized_ = false;
        recoverymanager_ = recoverymanager;
        tidmgr_ = tidmgr;
        tidToTransactionMap_ = new ConcurrentHashMap<>();
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
        if (tidToTransactionMap_.containsKey(tid.intern()))
            throw new IllegalStateException("Already mapped: " + tid);
        tidToTransactionMap_.put(tid.intern(), ct);
        ct.addSubTxAwareParticipant(this); // for GC purposes
    }

    /**
     * Removes the coordinator from the root map.
     *
     * @param coord
     *            The coordinator to remove.
     */

    private void removeCoordinator ( CompositeCoordinator coord )
    {
    	// don't lock shutdownSynchronizer: allow removal during shutdown !!!
    	// see https://www.atomikos.com/Documentation/ConcurrencyModel
    	synchronized ( getLatch ( coord.getRootId()) ) {
    		recreatedCoordinatorsByRootId.remove (coord.getRootId());
    		allCoordinatorsByCoordinatorId.remove(coord.getCoordinatorId());
    	}
    	LOGGER.logDebug("Removed cc with id: " + coord.getCoordinatorId());
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
        if (ct != null) {
        	tidToTransactionMap_.remove (ct.getTid().intern());
        }

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

        coordinator.incLocalSiblingsStarted(); //orphan detection and timeout handling
        setTidToTx ( ct.getTid (), ct );
        return ct;
    }

    /**
     * Creation method for composite coordinators.
     *
     * @param recoveryDomainName The recovery domain of the superior of this coordinator.
     * 
     * @param adaptor
     *            An existing coordinator for the given root. Null if not a
     *            subtx, or an <b>adaptor</b> in other cases.
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
        CoordinatorImp cc;

        if (maxTimeout_ > 0 &&  timeout > maxTimeout_ ) {
            timeout = maxTimeout_;
            //FIXED 20188
            LOGGER.logWarning ( "Attempt to create a transaction with a timeout that exceeds maximum - truncating to: " + maxTimeout_ );
        }

        shutdownSynchronizer.readLock().lock();
        try {
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

            synchronized (getLatch(root)) {
            	//cf case 178075
            	recreatedCoordinatorsByRootId.putIfAbsent(root, cc);
            	allCoordinatorsByCoordinatorId.put(coordinatorId, cc);
            }
            startlistening ( cc );
            LOGGER.logDebug("Created cc with id: " + cc.getCoordinatorId());
        }finally {
            shutdownSynchronizer.readLock().unlock();
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
        root = root.intern();
        if (!initialized_) {
            throw new IllegalStateException ( "Not initialized" );
        }
        return recreatedCoordinatorsByRootId.get(root);
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
     * @see FSMEnterListener
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
        return tidToTransactionMap_.get(tid.intern());
    }



    /**
     * Creates a subtransaction for the given parent
     *
     * @param parent
     * @return
     */
    @SuppressWarnings("unchecked")
    public CompositeTransaction createSubTransaction ( CompositeTransaction parent )
    {
    	if (Configuration.getConfigProperties().getAllowSubTransactions()) {
    		CompositeTransactionImp ret;
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

    public CompositeTransaction recreateCompositeTransaction (Propagation context) throws SysException {
        
    	assertInitialized();
    	assertMaxNumberOfActiveTransactionsNotExceeded();
        
        // cf case 228139: used to throw here; now warns instead - see ForeignDomainImportGuard
        if (ForeignDomainImportGuard.shouldWarnAboutUnrecognizedDomain(Configuration.getRecoveryLog(), tmUniqueName_, context.getRecoveryDomainName())) {
            LOGGER.logWarning("Importing a transaction from recovery domain " + context.getRecoveryDomainName() +
                    " which is not recognized by this recovery log - recovery of this transaction (if needed) " +
                    "will depend on the exporting service being reachable, rather than on a shared recovery log.");
        }

        CoordinatorImp cc;
        CompositeTransaction ct;

        try {
            String tid = tidmgr_.get ();
            boolean serial = context.isSerial ();
            
            CompositeTransaction root = context.getRootTransaction();
            CompositeTransaction parent = context.getParentTransaction();
            
            shutdownSynchronizer.readLock().lock();
            try {
                synchronized ( getLatch ( root.getTid () ) ) {
                    cc = getCoordinatorImpForRoot ( root.getTid () );
                    if ( cc == null ) {
                        RecoveryCoordinator coord = parent
                                .getCompositeCoordinator ()
                                .getRecoveryCoordinator ();
                        cc = createCC (context.getRecoveryDomainName(), coord, root.getTid (), context.getTimeout () );

                    }
                }
            }finally {
                shutdownSynchronizer.readLock().unlock();
            }
            ct = createCT ( tid, cc, context.getLineage(), serial );

        } catch ( Exception e ) {
            throw new SysException ( "Error in recreate.", e );
        }

        return ct;
    }

	private void assertMaxNumberOfActiveTransactionsNotExceeded() {
		if ( maxNumberOfActiveTransactions_ >= 0 && tidToTransactionMap_.size () >= maxNumberOfActiveTransactions_ )
            throw new IllegalStateException (
                    "Max number of active transactions reached:" + maxNumberOfActiveTransactions_ );
	}
    
    private boolean usesDefaultRecovery() {
        return Configuration.getRecoveryLog() instanceof RecoveryLogImp;
    }

    /**
     * @see TransactionService
     */

     public void shutdown(boolean force) {
    	
        boolean wasShuttingDown = false;
        LOGGER.logInfo ( "Entering shutdown (" + force + ")..." );

        shutdownSynchronizer.writeLock().lock();
        try {
            LOGGER.logDebug ( "Shutdown acquired lock on waiter." );
            wasShuttingDown = shutdownInProgress_;
            shutdownInProgress_ = true;
            
            if (!force) {
            	waitForActiveCoordinatorsToFinish();
            } 
            boolean remainingCoordinators = disposeRemainingCoordinators();
            if (usesDefaultRecovery()) {
            	if (!remainingCoordinators) {
            		performRecoveryPass();
            	}
            	if (remainingCoordinators || recoveryDomainService.hasPendingParticipantsFromLastRecoveryScan()) {
            		LOGGER.logWarning("Shutdown leaves pending transactions in log - do NOT delete logfiles!");
            	} else {
            		// if we are here then there are NO pending XIDs in any resource 
            		// => whatever is left in the transaction log files: we don't need it any more
            		LOGGER.logInfo("Shutdown leaves no pending transactions - ok to delete logfiles");
            	}
            } else {
            	recoveryLog.closing(); // allow other cluster node to take over
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
        }finally {
            shutdownSynchronizer.writeLock().unlock();
        }
        
        shutdownSystemExecutors();
    }
     
    private boolean disposeRemainingCoordinators() {
    	boolean ret = false;
    	for (String next : allCoordinatorsByCoordinatorId.keySet()) {
            CoordinatorImp c  = allCoordinatorsByCoordinatorId.get(next);
            if (c != null) { // null on concurrent termination / removal
            	LOGGER.logTrace ( "Stopping thread for coordinatorId " + next + "..." );
            	c.dispose (); 
           	 	ret = true;
           	 	LOGGER.logTrace ( "Thread stopped." );
            }
    	}
    	return ret;
    }

	private boolean waitForActiveCoordinatorsToFinish() {
		ConditionalWaiter waiter = new ConditionalWaiter(maxTimeout_);
        boolean timeout = waiter.waitWhile(() -> {
            boolean allCoordinatorsDone = allCoordinatorsByCoordinatorId.isEmpty();
            if (!allCoordinatorsDone) {
                LOGGER.logWarning("Shutdown; waiting for all active transactions to finish...");
            }
            return !allCoordinatorsDone;
        });
        return timeout;
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
        assertInitialized();
        assertMaxNumberOfActiveTransactionsNotExceeded();
        
        String tid = tidmgr_.get ();
        Stack<CompositeTransaction> lineage = new Stack<>();
        // create a CC with heuristic preference set to false,
        // since it does not really matter anyway (since we are
        // creating a root)
        CoordinatorImp cc = createCC(tmUniqueName_, null, tid, timeout);
        CompositeTransaction ct = createCT ( tid, cc, lineage, false );
        return ct;
    }

	private void assertInitialized() {
		if ( !initialized_ ) throw new IllegalStateException ( "Not initialized" );
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

	@Override
	public void preEnter(FSMEnterEvent e) throws IllegalStateException {
	}

}
