/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
