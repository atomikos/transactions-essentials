/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.TaskManager;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;

public class RecoveryDomainService {

	private static final Logger LOGGER = LoggerFactory.createLogger(RecoveryDomainService.class);
    
	private RecoveryLog recoveryLog;    

	public RecoveryDomainService(RecoveryLog recoveryLog) {
		this.recoveryLog = recoveryLog;
	}

	private long maxTimeout;
	private PooledAlarmTimer recoveryTimer;
	private String recoveryDomainName;

	public void init() {

	    long recoveryDelay = Configuration.getConfigProperties().getRecoveryDelay();
	    setMaxTimeout(Configuration.getConfigProperties().getMaxTimeout());
	    recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
	    recoveryTimer = new PooledAlarmTimer(recoveryDelay);

	    recoveryTimer.addAlarmTimerListener(new AlarmTimerListener() {

	        @Override
	        public void alarm(AlarmTimer timer) {				
	            performRecovery();
	        }
	    });			
	    TaskManager.SINGLETON.executeTask(recoveryTimer);
	  
	}

    public void setMaxTimeout(long maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	protected synchronized void performRecovery() {
		
		if (recoveryLog.isActive()) {				
			try {
				boolean allOk = true;
				long startOfRecovery = System.currentTimeMillis();
				Set<RecoverableResource> resourcesToRecover = getResourcesForRecovery();
				Collection<PendingTransactionRecord> indoubtCoordinators = recoveryLog.getIndoubtTransactionRecords();
				Collection<PendingTransactionRecord> foreignIndoubtCoordinators = extractForeignRecords(indoubtCoordinators);
				Collection<PendingTransactionRecord> foreignCoordinatorsForHeuristicAbort = extractForeignIndoubtCoordinatorsForHeuristicAbort(foreignIndoubtCoordinators, startOfRecovery);
				Collection<PendingTransactionRecord> expiredCommittingCoordinators = recoveryLog.getExpiredPendingCommittingTransactionRecordsAt(startOfRecovery);
			
				for (RecoverableResource recoverableResource : resourcesToRecover) {
					try {
						allOk = allOk && recoverableResource.recover(startOfRecovery, expiredCommittingCoordinators, foreignIndoubtCoordinators);
					} catch (Throwable e) {
						allOk = false;
						LOGGER.logError(e.getMessage(), e);
					}
				}
				
				Collection<PendingTransactionRecord> recordsToDelete = new HashSet<>();
				if (allOk) {
				    recordsToDelete.addAll(expiredCommittingCoordinators);
				    Collection<PendingTransactionRecord> expiredNativeIndoubtCoordinators = extractNativeIndoubtCoordinatorsExpiredSince(startOfRecovery - maxTimeout, indoubtCoordinators);
				    recordsToDelete.addAll(expiredNativeIndoubtCoordinators);
				}
				recordsToDelete.addAll(foreignCoordinatorsForHeuristicAbort);
				recoveryLog.forgetTransactionRecords(recordsToDelete);
				
			} catch (Throwable e) {
				LOGGER.logError(e.getMessage(), e);
			}
		}
	}


    private Collection<PendingTransactionRecord> extractNativeIndoubtCoordinatorsExpiredSince(long momentInThePast,
            Collection<PendingTransactionRecord> collection) {
        return PendingTransactionRecord.collectLineages(
                (PendingTransactionRecord r) -> r.isLocalRoot(recoveryDomainName) && !r.isForeignInDomain(recoveryDomainName) && r.expires < momentInThePast && r.state == TxState.IN_DOUBT ,
                collection);
    }

    private Collection<PendingTransactionRecord> extractForeignRecords(
            Collection<PendingTransactionRecord> collection) {
        return PendingTransactionRecord.collectLineages(
                (PendingTransactionRecord r) -> r.isForeignInDomain(recoveryDomainName), 
                collection);
    }

    private Collection<PendingTransactionRecord> extractForeignIndoubtCoordinatorsForHeuristicAbort(
            Collection<PendingTransactionRecord> foreignIndoubtCoordinators, long startOfRecovery) {
        HashSet<PendingTransactionRecord> ret = new HashSet<>();
        Iterator<PendingTransactionRecord> it = foreignIndoubtCoordinators.iterator();
        while (it.hasNext()) {
            PendingTransactionRecord record = it.next();
            if (record.expires + maxTimeout < startOfRecovery) {
                if (record.allowsHeuristicTermination(recoveryDomainName)) {
                    ret.add(record);           
                } else {
                    //pending expired in-doubt => generate warning
                    TransactionHeuristicEvent event = new TransactionHeuristicEvent(record.id, record.superiorId, TxState.IN_DOUBT);
                    EventPublisher.INSTANCE.publish(event);
                }
            }
            for (PendingTransactionRecord entry : ret) {
                foreignIndoubtCoordinators.remove(entry); //remove - so presumed abort will terminate this one
                PendingTransactionRecord.removeAllDescendants(entry, foreignIndoubtCoordinators); //make sure that local descendants also abort
                TransactionHeuristicEvent event = new TransactionHeuristicEvent(record.id, record.superiorId, TxState.HEUR_ABORTED);
                EventPublisher.INSTANCE.publish(event);
            }
        }
        return ret;
    }


    private Set<RecoverableResource> getResourcesForRecovery() {
	    Collection<RecoverableResource> resources = null;
        resources = Configuration.getResources();
	    return filterDuplicates(resources); //cf case 170618
    }

    private Set<RecoverableResource> filterDuplicates(Collection<RecoverableResource> resources) {
       return new HashSet<RecoverableResource>(resources);
    }

    public void stop() {
		if (recoveryTimer != null) {
			recoveryTimer.stopTimer();
			recoveryTimer = null;
		}
	}
    
    /**
     * 
     * @return False if nothing more to do.
     */
    public synchronized boolean hasMoreToRecover() {
        boolean ret = false;
        if (!recoveryLog.isActive()) {
            // another instance has taken over => we're off the hook
            return false;
        }
        for (RecoverableResource res : getResourcesForRecovery()) {
            if (res.hasMoreToRecover()) {
                return true;
            }
        }
        return ret;
    }
}
