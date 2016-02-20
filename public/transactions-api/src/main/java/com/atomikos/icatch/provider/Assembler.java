/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider;

import com.atomikos.icatch.CompositeTransactionManager;



public interface Assembler {

	ConfigProperties initializeProperties();
	
	TransactionServiceProvider assembleTransactionService(ConfigProperties configProperties);
	
	CompositeTransactionManager assembleCompositeTransactionManager();

}
