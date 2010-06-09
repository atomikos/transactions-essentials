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

import javax.transaction.Status;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An adaptor for the native icatch Synchronization towards JTA synch.
 */

class Sync2Sync implements com.atomikos.icatch.Synchronization
{
    protected javax.transaction.Synchronization sync_;

    private Boolean committed_;

    // null for readonly, True for commit, False for abort

    Sync2Sync ( javax.transaction.Synchronization sync )
    {
        sync_ = sync;

    }

    public void beforeCompletion ()
    {
        sync_.beforeCompletion ();
        // reset flag to allow reuse of sync objects!
        committed_ = null;
    }

    public void afterCompletion ( Object state )
    {
        if ( state.equals ( TxState.TERMINATED ) ) {
            if ( committed_ == null ) {

                // happens on readonly vote -> outcome not known!
                sync_.afterCompletion ( Status.STATUS_UNKNOWN );
                Configuration
                        .logInfo ( "afterCompletion ( STATUS_UNKNOWN ) called "
                                + " on Synchronization: " + sync_.toString () );
            } else {
                boolean commit = committed_.booleanValue ();
                if ( commit ) {
                    sync_.afterCompletion ( Status.STATUS_COMMITTED );
                    Configuration
                            .logInfo ( "afterCompletion ( STATUS_COMMITTED ) called "
                                    + " on Synchronization: "
                                    + sync_.toString () );
                } else {
                    sync_.afterCompletion ( Status.STATUS_ROLLEDBACK );
                    Configuration
                            .logInfo ( "afterCompletion ( STATUS_ROLLEDBACK ) called "
                                    + " on Synchronization: "
                                    + sync_.toString () );
                }
            }
        } else if ( state.equals ( TxState.COMMITTING ) )
            committed_ = new Boolean ( true );
        else if ( state.equals ( TxState.ABORTING ) )
            committed_ = new Boolean ( false );

    }
}
