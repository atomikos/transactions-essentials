/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.fs;


import java.util.Collection;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;

public class RecoveryLogImp implements RecoveryLog {

	private static final Logger LOGGER = LoggerFactory.createLogger(RecoveryLogImp.class);
	
	private Repository repository;

    private String recoveryDomainName;
    
    public RecoveryLogImp() {
        recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
    }

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Collection<PendingTransactionRecord> getPendingTransactionRecords() throws LogReadException {
	    return repository.getAllCoordinatorLogEntries();

	}


	@Override
	public void closing() {
		//don't close repository: OltpLog responsibility.
	}

	@Override
	public Collection<PendingTransactionRecord> getExpiredPendingCommittingTransactionRecordsAt(long time) throws LogReadException {
	    Collection<PendingTransactionRecord> allCoordinatorLogEntries = repository.findAllCommittingCoordinatorLogEntries();
	    
	    return PendingTransactionRecord.collectLineages(
                (PendingTransactionRecord r) -> r.isLocalRoot(recoveryDomainName) && r.expires < time && r.state == TxState.COMMITTING, 
                allCoordinatorLogEntries);
	}


    @Override
    public Collection<PendingTransactionRecord> getIndoubtTransactionRecords()
            throws LogReadException {
        Collection<PendingTransactionRecord> allCoordinatorLogEntries = repository.getAllCoordinatorLogEntries();
        return PendingTransactionRecord.collectLineages(
                (PendingTransactionRecord r) -> r.state == TxState.IN_DOUBT, 
                allCoordinatorLogEntries);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void forgetTransactionRecords(Collection<PendingTransactionRecord> coordinators) {
        try {
            for(PendingTransactionRecord entry : coordinators) {
                PendingTransactionRecord terminated = entry.markAsTerminated();
                repository.put(terminated.id, terminated);
            }
        } catch (Exception e) {
            LOGGER.logDebug("Unexpected exception - ignoring...", e);
        }
    }

    @Override
    public void recordAsCommitting(String coordinatorId) throws LogException {
        PendingTransactionRecord entry = repository.get(coordinatorId);
        if (entry != null) {        	
        	PendingTransactionRecord committing = entry.markAsCommitting();
        	repository.put(committing.id, committing);
        } else {
        	LOGGER.logWarning("Entry for coordinator " + coordinatorId + " no longer found - consider https://www.atomikos.com/Main/LogCloud for full distributed recovery among microservices");
        }
    }

    @Override
    public void forget(String coordinatorId) {
        try {
            PendingTransactionRecord rec = repository.get(coordinatorId);
            if (rec != null) { // case 179295
                PendingTransactionRecord terminated = rec.markAsTerminated();
                repository.put(terminated.id, terminated);
            }
        } catch (Exception e) {
            LOGGER.logDebug("Unexpected exception - ignoring...", e);
        }
    }

    @Override
    public PendingTransactionRecord get(String coordinatorId) throws LogReadException {
        return repository.get(coordinatorId);
    }

    @Override
    public void closed() {
        
    }
	
}