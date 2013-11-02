package com.atomikos.icatch.provider;

import com.atomikos.icatch.TransactionService;


public interface Assembler {

	ConfigProperties initializeProperties();
	
	TransactionService assembleTransactionService(ConfigProperties configProperties);

}
