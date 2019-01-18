package com.atomikos.recovery.fs;


import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.OltpLog;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.TxState;

public class OltpLogImp implements OltpLog {


	private static final Logger LOGGER = LoggerFactory.createLogger(OltpLogImp.class);
	private Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	
	@Override
	public void write(PendingTransactionRecord pendingTransactionRecord) throws LogWriteException {
		if(pendingTransactionRecord.state == TxState.COMMITTING && pendingTransactionRecord.expires < System.currentTimeMillis()) {
			throw new IllegalArgumentException("Transaction has expired - commit not longer allowed");
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
