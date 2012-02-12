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

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A class for wrapping Synchronization to FSMPreEnterListener.
 */

class SynchToFSM implements FSMEnterListener
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(SynchToFSM.class);

    private Synchronization synch_;

    private boolean aftercompletion_;

    private boolean termination_;

    // to avoid that listeners (e.g. pooled connections)
    // add themselves to the pool twice. This would be
    // very dangerous!

    SynchToFSM ( Synchronization s )
    {
        super ();
        synch_ = s;
        aftercompletion_ = false;
        termination_ = false;
    }
    
    private void doAfterCompletion ( TxState state ) 
    {
    	try {
			synch_.afterCompletion ( state );
		} catch ( RuntimeException e ) {
			//see case 24246: ignore but log
			Configuration.logWarning ( "Error during afterCompletion" , e );
		}
    }

    public void entered ( FSMEnterEvent e )
    {
    	
        if ( e != null ) {
            if ( e.getState ().equals ( TxState.COMMITTING )
                    && (!aftercompletion_) ) {
                doAfterCompletion ( TxState.COMMITTING );
                aftercompletion_ = true;
            } else if ( e.getState ().equals ( TxState.ABORTING )
                    && (!aftercompletion_) ) {
            	doAfterCompletion ( TxState.ABORTING );
                aftercompletion_ = true;
            } else if ( e.getState ().equals ( TxState.TERMINATED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.TERMINATED );
                aftercompletion_ = true;
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_MIXED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_MIXED );
                termination_ = true;

            } else if ( e.getState ().equals ( TxState.HEUR_ABORTED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_ABORTED );
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_HAZARD )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_HAZARD );
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_COMMITTED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_COMMITTED );
                termination_ = true;
            }
        }// if
    }
}
