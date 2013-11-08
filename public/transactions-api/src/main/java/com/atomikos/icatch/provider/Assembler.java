package com.atomikos.icatch.provider;

import com.atomikos.icatch.CompositeTransactionManager;



public interface Assembler {

	ConfigProperties initializeProperties();
	
	TransactionServiceProvider assembleTransactionService(ConfigProperties configProperties);
	
	CompositeTransactionManager assembleCompositeTransactionManager(ConfigProperties configProperties);

}
