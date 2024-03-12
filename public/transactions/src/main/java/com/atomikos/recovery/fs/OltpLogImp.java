/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.fs;


import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.OltpLog;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.TxState;

public class OltpLogImp implements OltpLog {


	private static final Logger LOGGER = LoggerFactory.createLogger(OltpLogImp.class);
	private Repository repository;
    private String recoveryDomainName;

	public void setRepository(Repository repository) {
		this.repository = repository;
		this.recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
	}

	
	@Override
	public void write(PendingTransactionRecord pendingTransactionRecord) throws LogWriteException {
	    TxState state = pendingTransactionRecord.state;
	    if (pendingTransactionRecord.expires < System.currentTimeMillis()) {
	        if (state == TxState.IN_DOUBT) {
	            throw new IllegalArgumentException("Transaction has expired - " + state + " no longer allowed");
	        } else if (state == TxState.COMMITTING && pendingTransactionRecord.isRecoveredByDomain(recoveryDomainName)
	                 ){
	            throw new IllegalArgumentException("Transaction may have been subject to abort by recovery - COMMITTING no longer allowed");
	        }
	            
	    }
		if(pendingTransactionRecord.state.isRecoverableState()) {
			repository.put(pendingTransactionRecord.id, pendingTransactionRecord);	
		} else {
			LOGGER.logWarning("Attempt to log a record with unexpected state : " + pendingTransactionRecord.state);
		}
			
		
	}

	@Override
	public void close() {
		repository.close();
	}


}
