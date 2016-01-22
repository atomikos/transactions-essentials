/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

import javax.transaction.Status;

import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class Sync2Sync implements com.atomikos.icatch.Synchronization
{
	private static final Logger LOGGER = LoggerFactory.createLogger(Sync2Sync.class);

    protected javax.transaction.Synchronization sync;

    private Boolean committed; //null for readonly
    
    Sync2Sync ( javax.transaction.Synchronization sync )
    {
        this.sync = sync;
    }

    @Override
	public void beforeCompletion ()
    {
        this.sync.beforeCompletion ();
        resetForReuse();
        LOGGER.logInfo("beforeCompletion() called on Synchronization: " + this.sync.toString());
    }

	private void resetForReuse() {
        this.committed = null;
	}

    @Override
	public void afterCompletion ( TxState state )
    {
        if ( state.equals ( TxState.TERMINATED ) ) {
            if ( this.committed == null ) { //readonly: unknown
                this.sync.afterCompletion ( Status.STATUS_UNKNOWN );
                LOGGER.logInfo ( "afterCompletion ( STATUS_UNKNOWN ) called "
                                + " on Synchronization: " + this.sync.toString () );
            } else {
                boolean commit = this.committed.booleanValue ();
                if ( commit ) {
                    this.sync.afterCompletion ( Status.STATUS_COMMITTED );
                    LOGGER.logInfo ( "afterCompletion ( STATUS_COMMITTED ) called "
                                    + " on Synchronization: "
                                    + this.sync.toString () );
                } else {
                    this.sync.afterCompletion ( Status.STATUS_ROLLEDBACK );
                    LOGGER.logInfo ( "afterCompletion ( STATUS_ROLLEDBACK ) called "
                                    + " on Synchronization: "
                                    + this.sync.toString () );
                }
            }
        } else if ( state.equals ( TxState.COMMITTING ) ) this.committed = new Boolean ( true );
          else if ( state.equals ( TxState.ABORTING ) ) this.committed = new Boolean ( false );

    }
}
