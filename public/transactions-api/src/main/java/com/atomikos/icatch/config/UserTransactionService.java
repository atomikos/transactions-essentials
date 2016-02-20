/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.provider.TransactionServicePlugin;

 /**
  *
  * The user's (client program) view of the transaction manager's configuration, 
  * with all the information the client program needs.
  *
  */

public interface UserTransactionService
{
	public void shutdown ( boolean force ) throws IllegalStateException;
	
	public void shutdown (long maxWaitTime) throws IllegalStateException;
 
	public void registerResource ( RecoverableResource resource );

	public void removeResource ( RecoverableResource res );

	public void registerLogAdministrator ( LogAdministrator admin );

	public void removeLogAdministrator ( LogAdministrator admin );

	public void registerTransactionServicePlugin ( TransactionServicePlugin listener );

	public void removeTransactionServicePlugin ( TransactionServicePlugin listener );

	public void init ( Properties properties ) throws SysException;
	
	public void init() throws SysException;

	public CompositeTransactionManager getCompositeTransactionManager();


}
