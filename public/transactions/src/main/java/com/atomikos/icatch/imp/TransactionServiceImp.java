/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

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
import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.icatch.provider.TransactionServiceProvider;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.TaskManager;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;
import com.atomikos.util.UniqueIdMgr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * General implementation of Transaction Service.
 */

public class TransactionServiceImp implements TransactionServiceProvider, FSMEnterListener,
  SubTxAwareParticipant, RecoveryService, AdminLog {

	  private static final Logger LOGGER = LoggerFactory.createLogger(TransactionServiceImp.class);
    private static final int NUMLATCHES = 97;
    
    private long maxTimeout_;
    private Object[] rootLatches_ = null;
    private Map<String,CompositeTransaction> tidToTransactionMap_ = null;
    private Map<String,CoordinatorImp> rootToCoordinatorMap_ = null;
    private boolean shutdownInProgress_ = false;
    private Object shutdownSynchronizer_;
    private UniqueIdMgr tidmgr_ = null;
    private StateRecoveryManager recoverymanager_ = null;
    private boolean initialized_ = false;
    private LogControl control_;

  // true for forced compatibility with OTS;
  // in that case no creation preferences are taken into account
  // concerning orphan checks
    private boolean otsOverride_;

    private List<TransactionServicePlugin> tsListeners_;
    private int maxNumberOfActiveTransactions_;
    private String tmUniqueName_;
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
     * @param recoveryLog
     *
     */

    private TransactionServiceImp ( String name ,
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
        tidToTransactionMap_ = new HashMap<String,CompositeTransaction>();
        shutdownSynchronizer_ = new Object();
        rootToCoordinatorMap_ = new HashMap<String,CoordinatorImp>();
        rootLatches_ = new Object[NUMLATCHES];
        for (int i = 0; i < NUMLATCHES; i++) {
            rootLatches_[i] = new Object();
        }

        maxTimeout_ = maxtimeout;	
        
        tmUniqueName_ = name;
        tsListeners_ = new ArrayList<>();
        single_threaded_2pc_ = single_threaded_2pc;
        this.recoveryLog  = recoveryLog;
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
     * For inspector tool: get a list of all active coordinator instances, to
     * allow admin intervention.
     *
     * @return Vector A copy of the list of active coordinators. Empty vector if
     *         none.
     */

    private List<CoordinatorImp> getCoordinatorImpList()
    {
        List<CoordinatorImp> ret = new ArrayList<>();

        List<String> tids = new ArrayList(rootToCoordinatorMap_.size());

        tids.addAll(rootToCoordinatorMap_.keySet());

        for (String tid : tids)
        {
          CoordinatorImp c = getCoordinatorImp(tid);

          if (c != null)  {
            ret.add(c);
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
    private void removeCoordinator ( CompositeCoordinator coord ) {

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
    private void removeTransaction ( CompositeTransaction ct ) {
        if ( ct == null )
            return;
        tidToTransactionMap_.remove ( ct.getTid ().intern () );

    }

    /**
     * Creation method for composite transactions.
     *
     * @return CompositeTransaction.
     */

    private CompositeTransactionImp createCT ( String tid, CoordinatorImp coordinator,
      Deque<CompositeTransaction> lineage, boolean serial ) throws SysException {

    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Creating composite transaction: " + tid );
        CompositeTransactionImp ct = new CompositeTransactionImp ( this, lineage, tid, serial, coordinator );

        setTidToTx ( ct.getTid (), ct );
        return ct;
    }

    /**
     * Creation method for composite coordinators.
     *
     * @param adaptor
     *            An existing coordinator for the given root. Null if not a
     *            subtx, or an <b>adaptor</b> in other cases.
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

        if (maxTimeout_ > 0 &&  timeout > maxTimeout_ ) {
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
        Set<TxState>  forgetStates = new HashSet<>();

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

    private CoordinatorImp getCoordinatorImp ( String root )
            throws SysException
    {
        String localRoot = root.intern ();

        if ( !initialized_ )
            throw new IllegalStateException ( "Not initialized" );

        CoordinatorImp cc = null;
        synchronized ( shutdownSynchronizer_ ) {
            // Synch on shutdownSynchronizer_ first to avoid
            // deadlock, even if we don't seem to need it here

            synchronized ( getLatch ( localRoot ) ) {
                cc = (CoordinatorImp) rootToCoordinatorMap_.get ( localRoot );
            }
        }

        return cc;
    }

    public String getName ()
    {
        return tmUniqueName_;
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
    			tsListeners_.add ( listener );
    			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace (  "Added TSListener: " + listener );
    		}


    }

    /**
     * @see TransactionService
     */

    public void removeTSListener ( TransactionServicePlugin listener )
    {

        tsListeners_.remove ( listener );
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace  ( "Removed TSListener: " + listener );

    }
    
    private PooledAlarmTimer recoveryTimer;

    /**
     * @see TransactionService
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        shutdownInProgress_ = false;
        control_ = new com.atomikos.icatch.admin.imp.LogControlImp ( (AdminLog) this.recoveryLog );

		ConfigProperties configProperties = new ConfigProperties(properties);
		long recoveryDelay = configProperties.getRecoveryDelay();

        
        recoveryTimer = new PooledAlarmTimer(recoveryDelay);
        
        recoveryTimer.addAlarmTimerListener(new AlarmTimerListener() {
			
			@Override
			public void alarm(AlarmTimer timer) {
				
				performRecovery();
				
			}

			
		});
        
        TaskManager.SINGLETON.executeTask(recoveryTimer);
        
        initialized_ = true;
       
        
    }

    private void performRecovery() {


		List<RecoverableResource> resources = Configuration.getResources();

    for (RecoverableResource recoverableResource : resources) {
			try {
				recoverableResource.recover();
			} catch (Throwable e) {
				LOGGER.logError(e.getMessage(),e);
			}
		}
	}
    /**
     * @see TransactionService
     */

    public Participant getParticipant ( String root ) throws SysException
    {
        return getCoordinatorImp ( root );
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
        CompositeTransaction ret = null;

        synchronized ( tidToTransactionMap_ ) {
            ret = (CompositeTransaction) tidToTransactionMap_.get ( tid.intern () );
        }

        return ret;
    }



    /**
     * Creates a subtransaction for the given parent
     *
     * @param parent parent
     * @return CompositeTransaction
     */
    @SuppressWarnings("unchecked")
    CompositeTransaction createSubTransaction ( CompositeTransaction parent )
    {
    	if (Configuration.getConfigProperties().getAllowSubTransactions()) {
    		CompositeTransactionImp ret;
    		Deque<CompositeTransaction> lineage = ((ArrayDeque<CompositeTransaction>)parent.getLineage()).clone ();
    		lineage.push ( parent );
    		String tid = tidmgr_.get ();
    		CoordinatorImp ccParent = (CoordinatorImp) parent.getCompositeCoordinator ();
    		// create NEW coordinator for subtx, with most of the parent settings
    		// but without orphan checks since subtxs have no orphans
    		CoordinatorImp cc = createCC ( null, tid, false,
          ccParent.prefersHeuristicCommit (), parent.getTimeout () );
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
            Deque<CompositeTransaction> lineage = context.getLineage ();
            if ( lineage.isEmpty () )
                throw new SysException (
                        "Empty lineage in propagation: empty lineage" );
            Deque<CompositeTransaction> tmp = new ArrayDeque<>();

            while ( !lineage.isEmpty () ) {
                tmp.push ( lineage.pop () );
            }

            CompositeTransaction root = (CompositeTransaction) tmp.peek ();

            while ( !tmp.isEmpty () ) {
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
            throw new SysException ( "Error in recreate.", e );
        }

        return ct;
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
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Transaction Service: Entering shutdown ( "
                + force + " )..." );

        // following moved out of synch block to avoid deadlock on immediate
        // shutdown with interleaving entered notification of a terminating
        // coordinator state-handler
        if ( !wasShuttingDown && force ) {
            // If we were already shutting down, then the FIRST thread
            // to enter this method will do the following. Don't do
            // it twice.
            List<String> tidList = new ArrayList<>(rootToCoordinatorMap_.size());

            tidList.addAll(rootToCoordinatorMap_.keySet());

            for(String tid : tidList) {
              LOGGER.logTrace ( "Transaction Service: Stopping thread for root "
                + tid + "..." );
              CoordinatorImp coordinatorImp = (CoordinatorImp) rootToCoordinatorMap_.get ( tid );
              if ( coordinatorImp != null ) { //null if intermediate termination while in enumm
                coordinatorImp.dispose (); //needed for forced shutdown
              }
              LOGGER.logTrace ( "Transaction Service: Thread stopped." );
            }


        } // if wasShuttingDown


        synchronized ( shutdownSynchronizer_ ) {
        	LOGGER.logTrace ( "Transaction Service: Shutdown acquired lock on waiter." );
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
            	
            	performRecovery();
            	recoveryLog.close(maxWaitTime);

            	//PURGE to avoid issue 10079
            	//use a clone to avoid concurrency interference
            	if ( LOGGER.isTraceEnabled() )
            	  LOGGER.logTrace ( "Transaction Service: Purging coordinators for shutdown..." );

            	Map<String,CoordinatorImp> clone = new HashMap<String,CoordinatorImp>(rootToCoordinatorMap_);

//            	Enumeration<String> coordinatorIds = clone.keys();
//            	while ( coordinatorIds.hasMoreElements() ) {
//            		String id = coordinatorIds.nextElement();
//            		rootToCoordinatorMap_.remove ( id );
//            	}

            	for(String id : clone.keySet()) {
                rootToCoordinatorMap_.remove ( id );
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
                recoveryTimer.stop();
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

    /**
     * @see com.atomikos.icatch.TransactionService#getCompositeCoordinator(java.lang.String)
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
        Deque<CompositeTransaction> lineage = new ArrayDeque<>();
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
		List<CoordinatorImp> coordinatorImpVector = getCoordinatorImpList();
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
		List<CoordinatorImp> coordinatorImpList = getCoordinatorImpList();
		for (CoordinatorImp coordinatorImp : coordinatorImpList) {
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
