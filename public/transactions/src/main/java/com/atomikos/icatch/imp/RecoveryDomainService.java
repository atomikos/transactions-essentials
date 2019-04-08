/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.RecoveryLog;
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
		
		if (recoveryLog != null) {	// null for logcloud client
			
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
		
	}
	
	public void setMaxTimeout(long maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	protected void performRecovery() {
		
		if (recoveryLog != null) {	// null for logcloud			
			try {
				boolean allOk = true;
				long startOfRecovery = System.currentTimeMillis();
				Set<RecoverableResource> resourcesToRecover = getResourcesForRecovery();
				for (RecoverableResource recoverableResource : resourcesToRecover) {
					try {
						allOk = allOk && recoverableResource.recover(startOfRecovery);
					} catch (Throwable e) {
						allOk = false;
						LOGGER.logError(e.getMessage(), e);
					}
				}
				if (allOk) {
					recoveryLog.forgetCommittingCoordinatorsExpiredSince(startOfRecovery);
					recoveryLog.forgetIndoubtCoordinatorsExpiredSince(startOfRecovery - maxTimeout);
				}
				
				
			} catch (Throwable e) {
				LOGGER.logError(e.getMessage(), e);
			}
		}
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
