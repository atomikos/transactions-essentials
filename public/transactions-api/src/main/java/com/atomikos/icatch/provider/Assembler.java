package com.atomikos.icatch.provider;

import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.TransactionService;


public interface Assembler {

	ConfigProperties initializeProperties();
	
	TransactionService assembleTransactionService(ConfigProperties configProperties);

	RecoveryService assembleRecoveryService(ConfigProperties configProperties);
	
	CompositeTransactionManager assembleCompositeTransactionManager(ConfigProperties configProperties);
}
