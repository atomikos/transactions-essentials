/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

/**
 * A state handler for the indoubt coordinator state.
 */

class IndoubtStateHandler extends CoordinatorStateHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(IndoubtStateHandler.class);
   
	private long timeoutTicks = 0;
	
    IndoubtStateHandler ( ActiveStateHandler previous )
    {
        super ( previous );
        timeoutTicks = previous.getRollbackTicks();
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
        if ( !getCoordinator().getState().equals(getState())) {
            return;
        }
        
        if (timeoutTicks < getCoordinator().getMaxIndoubtTicks()) { 
            timeoutTicks++;
        } else { 
            try {
                if (getCoordinator().requiresHeuristics()) { 
                    //local recovery will automatically do presumed abort after max_timeout
                    //for a root this is OK but for imported transactions this means a heuristic
                    LOGGER.logWarning ( "Transaction " + getCoordinator().getCoordinatorId() + " has timed out - performing heuristic rollback. See https://www.atomikos.com/Documentation/HeuristicExceptions for more details or try https://www.atomikos.com/Main/ExtremeTransactions for self-healing recovery.");
                    rollbackWithAfterCompletionNotification(new RollbackCallback() {
                        public void doRollback()
                                throws HeurCommitException,
                                HeurMixedException, SysException,
                                HeurHazardException, IllegalStateException {
                            rollbackFromWithinCallback(true, true);
                        }});
                } else {
                    //no heuristics => pending coordinator after failed commit or rollback: 
                    //cleanup to remove from TransactionServiceImp and let recovery work in the background
                    removePendingOltpCoordinatorFromTransactionService();    
                }
               
            } catch ( Exception e ) {
                LOGGER.logWarning("Error in timeout of INDOUBT state: " + e.getMessage () );
            } 
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
