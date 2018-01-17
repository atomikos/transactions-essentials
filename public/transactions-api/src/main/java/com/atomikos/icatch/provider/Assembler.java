/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider;

import com.atomikos.icatch.CompositeTransactionManager;

 /**
  * Abstraction of how the API is instantiated. 
  * Instances are found by the Configuration class,
  * via the ServiceLoader mechanism of the JDK.
  */

public interface Assembler {

	ConfigProperties initializeProperties();
	
	TransactionServiceProvider assembleTransactionService(ConfigProperties configProperties);
	
	CompositeTransactionManager assembleCompositeTransactionManager();

}
