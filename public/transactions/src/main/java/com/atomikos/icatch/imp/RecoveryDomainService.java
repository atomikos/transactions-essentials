/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
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

    private ResourceLookup lookup;
    

	public RecoveryDomainService(RecoveryLog recoveryLog) {
		this.recoveryLog = recoveryLog;
	}
	
	public RecoveryDomainService(RecoveryLog recoveryLog, ResourceLookup lookup) {
	    this(recoveryLog);
	    this.lookup = lookup;
	}

	private long maxTimeout;
	private PooledAlarmTimer recoveryTimer;

	public void init() {

	    long recoveryDelay = Configuration.getConfigProperties().getRecoveryDelay();
	    setMaxTimeout(Configuration.getConfigProperties().getMaxTimeout());
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

	protected void performRecovery() {
		
		if (recoveryLog.isActive()) {				
			try {
				boolean allOk = true;
				long startOfRecovery = System.currentTimeMillis();
				Set<RecoverableResource> resourcesToRecover = getResourcesForRecovery();
				Collection<PendingTransactionRecord> foreignIndoubtCoordinators = recoveryLog.getForeignIndoubtTransactionRecords();
				Collection<PendingTransactionRecord> expiredCommittingCoordinators = recoveryLog.getExpiredPendingCommittingTransactionRecordsAt(startOfRecovery);
				
				Collection<PendingTransactionRecord> foreignCoordinatorsForHeuristicAbort = extractForeignIndoubtCoordinatorsForHeuristicAbort(foreignIndoubtCoordinators, startOfRecovery);

				for (RecoverableResource recoverableResource : resourcesToRecover) {
					try {
						allOk = allOk && recoverableResource.recover(startOfRecovery, expiredCommittingCoordinators, foreignIndoubtCoordinators);
					} catch (Throwable e) {
						allOk = false;
						LOGGER.logError(e.getMessage(), e);
					}
				}
				if (allOk) {
					recoveryLog.forgetCommittingCoordinatorsExpiredSince(startOfRecovery);
					recoveryLog.forgetNativeIndoubtCoordinatorsExpiredSince(startOfRecovery - maxTimeout);
				}
				recoveryLog.forgetTransactionRecords(foreignCoordinatorsForHeuristicAbort);
				Collection<PendingTransactionRecord> resolvedForeignCoordinators = extractResolvedForeignCoordinators(foreignIndoubtCoordinators);
				recoveryLog.forgetTransactionRecords(resolvedForeignCoordinators);

				
			} catch (Throwable e) {
				LOGGER.logError(e.getMessage(), e);
			}
		}
	}


    private Collection<PendingTransactionRecord> extractResolvedForeignCoordinators(
            Collection<PendingTransactionRecord> foreignIndoubtCoordinators) {
        HashSet<PendingTransactionRecord> ret = new HashSet<>();
        for (PendingTransactionRecord rec : foreignIndoubtCoordinators) {
            if (!TxState.IN_DOUBT.equals(rec.state)) {
                ret.add(rec);
            }
        }
        return ret;
    }

    private Collection<PendingTransactionRecord> extractForeignIndoubtCoordinatorsForHeuristicAbort(
            Collection<PendingTransactionRecord> foreignIndoubtCoordinators, long startOfRecovery) {
        HashSet<PendingTransactionRecord> ret = new HashSet<>();
        Iterator<PendingTransactionRecord> it = foreignIndoubtCoordinators.iterator();
        while (it.hasNext()) {
            PendingTransactionRecord record = it.next();
            if (record.expires + maxTimeout < startOfRecovery) {
                String recoveryCoordinatorURI = record.superiorId;
                if (!recoveryCoordinatorURI.startsWith("http")) {
                    ret.add(record);           
                    it.remove(); //remove - so presumed abort will terminate this one
                    TransactionHeuristicEvent event = new TransactionHeuristicEvent(record.id, record.superiorId, TxState.HEUR_ABORTED);
                    EventPublisher.publish(event);
                } else {
                    //pending expired in-doubt => generate warning
                    TransactionHeuristicEvent event = new TransactionHeuristicEvent(record.id, record.superiorId, TxState.IN_DOUBT);
                    EventPublisher.publish(event);
                }
            }
        }
        return ret;
    }

    private Set<RecoverableResource> getResourcesForRecovery() {
	    Collection<RecoverableResource> resources = null;
	    if (lookup == null) {
	        resources = Configuration.getResources();
	    } else {
	        resources = lookup.getResources();
	    }
	    return filterDuplicates(resources); //cf case 170618
    }

    private Set<RecoverableResource> filterDuplicates(Collection<RecoverableResource> resources) {
       return new HashSet<RecoverableResource>(resources);
    }

    public void stop() {
		if (recoveryTimer != null) {
			recoveryTimer.stop();
			recoveryTimer = null;
		}
	}
}
