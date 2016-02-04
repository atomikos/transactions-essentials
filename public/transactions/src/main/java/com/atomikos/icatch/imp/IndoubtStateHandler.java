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

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A state handler for the indoubt coordinator state.
 */

class IndoubtStateHandler extends CoordinatorStateHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(IndoubtStateHandler.class);

	private int inquiries_;
    // how many timeout events have happened?
    // if max allowed -> take heuristic decision
   
    IndoubtStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
        inquiries_ = 0;
    }

    protected TxState getState ()
    {
        return TxState.IN_DOUBT;
    }

    protected void onTimeout ()
    {
        // first check if we are still the current state!
        // otherwise, a COMMITTING tx could be rolled back if it
        // times out in between (i.e. a commit can come in while
        // this state handler gets a timeout event and rolls back)
        if ( !getCoordinator ().getState ().equals ( getState () ) )  return;

        try {

            if ( inquiries_ < getCoordinator ().getMaxIndoubtTicks () ) {

                inquiries_++;
                if ( inquiries_ >= getCoordinator ().getMaxIndoubtTicks () / 2 ) {
                    // WS-AT compliance: only ask for replay if half of timeout ticks has passed,
                    // to avoid hitting the coordinator with replays from the start
                	// (WS-T coordinator will abort if it receives a replay during preparing)
                    if ( getCoordinator ().getSuperiorRecoveryCoordinator () != null ) {
                    	if(LOGGER.isInfoEnabled()){
                    		LOGGER.logInfo("Requesting replayCompletion on behalf of coordinator "
                                    + getCoordinator ().getCoordinatorId ());
                    	}

                        getCoordinator().getSuperiorRecoveryCoordinator().
                        replayCompletion ( getCoordinator () );
                    }
                }
            } else {
                if ( getCoordinator ().getSuperiorRecoveryCoordinator () == null ) {
                	//pending coordinator after failed commit or rollback: cleanup to remove from TransactionServiceImp
                	removePendingOltpCoordinatorFromTransactionService();
                }
            } // else
        } catch ( Exception e ) {
        	LOGGER.logWarning("Error in timeout of INDOUBT state: " + e.getMessage () );

        }
    }

	

    protected void setGlobalSiblingCount ( int count )
    {
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {
        // if we have a repeated prepare in this state, then the
        // first prepare must have been a YES vote, otherwise we
        // would not be in-doubt! -> repeat the same vote
        // (required for WS-AT)
        return Participant.READ_ONLY + 1;
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

         commitWithAfterCompletionNotification ( new CommitCallback() {
    		public void doCommit()
    				throws HeurRollbackException, HeurMixedException,
    				HeurHazardException, IllegalStateException,
    				RollbackException, SysException {
    			commitFromWithinCallback ( false, false );
    		}          	
    	});

    }

    protected void rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
        rollbackWithAfterCompletionNotification(new RollbackCallback() {
			public void doRollback()
					throws HeurCommitException,
					HeurMixedException, SysException,
					HeurHazardException, IllegalStateException {
				 rollbackFromWithinCallback(true,false);
			}});
    }

}
