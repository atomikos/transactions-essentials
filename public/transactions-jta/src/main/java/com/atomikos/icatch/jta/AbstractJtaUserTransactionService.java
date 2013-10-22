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

package com.atomikos.icatch.jta;

import javax.transaction.TransactionManager;

import com.atomikos.datasource.xa.AcceptAllXATransactionalResource;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

public abstract class AbstractJtaUserTransactionService extends
		AbstractUserTransactionService
{

	public AbstractJtaUserTransactionService()
	{
		super();
	}

	public void init ( TSInitInfo info ) throws SysException
	{

		super.init ( info );
        String autoRegisterProperty = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME, info
                        .getProperties () );
        boolean autoRegister = "true".equals ( autoRegisterProperty );
        if ( Configuration.instance().getResources ().hasMoreElements () && !autoRegister ) {
            AcceptAllXATransactionalResource defaultRes = new AcceptAllXATransactionalResource (
                    "com.atomikos.icatch.DefaultResource" );
            Configuration.instance().addResource ( defaultRes );

        }
	}

	public void shutdown ( boolean force ) throws IllegalStateException
	{
		super.shutdown(force);
        TransactionManagerImp.installTransactionManager ( null, false );
        UserTransactionServerImp.getSingleton ().shutdown ();
	}

    /**
     * @see UserTransactionService
     */

    public TransactionManager getTransactionManager ()
    {
        return com.atomikos.icatch.jta.TransactionManagerImp
                .getTransactionManager ();
    }



}
