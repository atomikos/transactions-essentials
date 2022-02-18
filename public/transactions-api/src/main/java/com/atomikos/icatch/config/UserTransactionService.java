/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.config;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionServicePlugin;

 /**
  *
  * The user's (client program) view of the transaction manager's configuration, 
  * with all the information the client program needs.
  *
  */

public interface UserTransactionService
{
	void shutdown ( boolean force ) throws IllegalStateException;
	
	void shutdown (long maxWaitTime) throws IllegalStateException;
 
	void registerResource ( RecoverableResource resource );

	void removeResource ( RecoverableResource res );


	void registerTransactionServicePlugin ( TransactionServicePlugin listener );

	void removeTransactionServicePlugin ( TransactionServicePlugin listener );

	void init ( Properties properties ) throws SysException;
	
	void init() throws SysException;

	CompositeTransactionManager getCompositeTransactionManager();


}
