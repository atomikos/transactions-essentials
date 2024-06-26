/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.Vector;

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
import com.atomikos.thread.InterruptedExceptionHelper;

/**
 * A state handler for the active coordinator state.
 */

class ActiveStateHandler extends CoordinatorStateHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ActiveStateHandler.class);

    private long rollbackTicks_;
    // how many timeout events have happened?
    // if max allowed -> rollback on timeout

    private int globalSiblingCount_;
    
    private boolean wasSetToRollbackOnly;

    ActiveStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        rollbackTicks_ = 0;
        wasSetToRollbackOnly = false;
    }


    protected long getRollbackTicks ()
    {
        return rollbackTicks_;
    }

    protected TxState getState ()
    {
        return TxState.ACTIVE;
    }

    protected void onTimeout ()
    {

        try {
            if ( rollbackTicks_ < getCoordinator ().getMaxRollbackTicks () )
                rollbackTicks_++;
            else {
                // first check if we are still the current state!
                // otherwise, a COMMITTING tx could be rolled back
                // in case of 1PC!!!
                if ( getCoordinator().getState() == TxState.ACTIVE) {
                	if ( getCoordinator().hasActiveSiblings() ) {
                		//cf case 71748
                		if (!wasSetToRollbackOnly) {
                			LOGGER.logWarning ( "Transaction " + getCoordinator().getCoordinatorId() + " has timed out and will rollback.");
                			getCoordinator().timedout(true);
                			wasSetToRollbackOnly = true;
                		}
                	} else {
                		LOGGER.logWarning ( "Transaction " + getCoordinator().getCoordinatorId() + " has timed out - rolling back...");
                		rollbackWithAfterCompletionNotification(new RollbackCallback() {
							public void doRollback()
									throws HeurCommitException,
									HeurMixedException, SysException,
									HeurHazardException, IllegalStateException {
							    getCoordinator().timedout(false);
								rollbackFromWithinCallback(false,false);
							}});
                	}
                } else if (getCoordinator().getState().isOneOf(TxState.PREPARING, TxState.COMMITTING, TxState.ABORTING))  {
                	//pending coordinator after failed prepare: cleanup to remove from TransactionServiceImp
                	removePendingOltpCoordinatorFromTransactionService();
                }
            }
        } catch ( Exception e ) {
            LOGGER.logDebug( "Error in timeout: " + e.getMessage ()
                    + " for transaction " + getCoordinator ().getCoordinatorId () );
        }
    }

    protected void setGlobalSiblingCount ( int count )
    {
        globalSiblingCount_ = count;
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        int count = 0; // number of participants
        PrepareResult result = null; // synchronization
        boolean allReadOnly = true; // if still true at end-> readonly vote
        int ret = 0; // return value
        Vector<Participant> participants = getCoordinator ().getParticipants ();
        CoordinatorStateHandler nextStateHandler = null;

        if ( orphansExist() ) {
            try {
                if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Orphans detected: "
                        + getCoordinator ().getLocalSiblingCount () + " vs "
                        + globalSiblingCount_ + " - forcing rollback." );
                rollbackWithAfterCompletionNotification(new RollbackCallback() {
					public void doRollback()
							throws HeurCommitException,
							HeurMixedException, SysException,
							HeurHazardException, IllegalStateException {
						rollbackFromWithinCallback(false,false);
					}});

            } catch ( HeurCommitException hc ) {
                throw new HeurMixedException();
            }

            throw new RollbackException ( "Orphans detected." );
        }

        try {
        	try {
        		getCoordinator().setState ( TxState.PREPARING );
        	} catch ( RuntimeException error ) {
        		//See case 23334
        		String msg = "Error in preparing: " + error.getMessage() + " - rolling back instead";
        		LOGGER.logWarning ( msg , error );
        		try {
					rollbackWithAfterCompletionNotification(new RollbackCallback() {
						public void doRollback()
								throws HeurCommitException,
								HeurMixedException, SysException,
								HeurHazardException, IllegalStateException {
							rollbackFromWithinCallback(false,false);
						}});
					throw new RollbackException ( msg , error);
        		} catch ( HeurCommitException e ) {
					LOGGER.logError ( "Illegal heuristic commit during rollback before prepare:" + e );
					throw new HeurMixedException();
				}
        	}
            count = participants.size ();
            result = new PrepareResult ( count );
            Enumeration<Participant> enumm = participants.elements ();
            String rootId = getCoordinator().getRootId();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                
                // Fix for recursive call to rootId
                if (rootId != null && p.getURI() != null && p.getURI().endsWith(rootId))
    				break;
                PrepareMessage pm = new PrepareMessage ( p, result );
                if ( getCascadeList () != null && p.getURI () != null ) { //null for OTS
                    Integer sibnum = (Integer) getCascadeList ().get ( p.getURI () );
                    if ( sibnum != null ) { // null for local participant!
                        p.setGlobalSiblingCount ( sibnum.intValue () );
                    }
                    p.setCascadeList ( getCascadeList () );
                }

                getPropagator ().submitPropagationMessage ( pm );
            } // while

            result.waitForReplies ();

            boolean voteOK = result.allYes ();
            setReadOnlyTable ( result.getReadOnlyTable () );
            allReadOnly = result.allReadOnly ();

            if ( !voteOK ) {
            	int res = result.getResult ();
            	Exception cause = result.findFirstOriginalException();
                try {
                    rollbackWithAfterCompletionNotification(new RollbackCallback() {
						public void doRollback()
								throws HeurCommitException,
								HeurMixedException, SysException,
								HeurHazardException, IllegalStateException {
							rollbackFromWithinCallback(true,false);
						}});
                } catch ( HeurCommitException hc ) {
                    // should not happen:
                    // means that ALL subordinate work committed heuristically.
                    // this is impossible since it assumes that ALL
                    // participants voted YES in the first place,
                    // which contradicts the fact that we are dealing with
                    // !voteOK
                    throw new SysException ( "Unexpected heuristic: "
                            + hc.getMessage (), hc );
                }
                // let recovery clean up in the background
                String msg = "Prepare failed because one or more resources refused to commit. This transaction has been rolled back instead. The cause could be either:\n" +
                	"1. a transaction timeout (in which case you should see additional timeout warnings in this log file), or\n" +
                	"2. inability to reach the resource (in which case you should see network errors), or\n" +
                	"3. a resource-internal cause that we can’t inspect";
                throw new RollbackException ( msg, cause );
            }
        } catch ( RuntimeException runerr ) {
            throw new SysException ( "Error in prepare: " + runerr.getMessage (), runerr );
        } catch ( InterruptedException err ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( err );
            throw new SysException ( "Error in prepare: " + err.getMessage (), err );
        }
        // here we are if all yes.
        if (discardCoordinatorAfterPrepare(allReadOnly)) {
            nextStateHandler = new TerminatedStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY;
            notifySynchronizationsAfterCompletion(TxState.COMMITTING,TxState.TERMINATED); //cf bug 127485
        } else {
            nextStateHandler = new IndoubtStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY + 1;
        }

        return ret;
    }

    private boolean discardCoordinatorAfterPrepare(boolean allReadOnly) { 
    	boolean ret = false;
    	if (allReadOnly) {
    		if (getCoordinator().isRoot() || getCoordinator().getLocalSiblingCount() == 1) { 
    			//cf case 197506
    			ret = true;
    		}
    	}
    	return ret;
    }


	private boolean orphansExist() {
		return globalSiblingCount_ != getCoordinator ().getLocalSiblingCount();
	}


	protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {
        if ( !onePhase )
            throw new IllegalStateException (
                    "Illegal state for commit: ACTIVE!" );

        if ( getCoordinator ().getParticipants ().size () > 1 ) {
            int prepareResult = Participant.READ_ONLY + 1;

            // happens if client has one remote participant
            // and hence decides for 1PC, but the remote
            // instance (this one) has more participants registered
            // in that case: first prepare and then do normal
            // 2PC commit

            setGlobalSiblingCount ( 1 );
            prepareResult = prepare ();
            
            if ( prepareResult != Participant.READ_ONLY ) {
            	commitWithAfterCompletionNotification ( new CommitCallback() {
            		public void doCommit()
            				throws HeurRollbackException, HeurMixedException,
            				HeurHazardException, IllegalStateException,
            				RollbackException, SysException {
            			 commitFromWithinCallback ( false, false );
            		}          	
            	});
            }
        } else {
        	commitWithAfterCompletionNotification ( new CommitCallback() {
        		public void doCommit()
        				throws HeurRollbackException, HeurMixedException,
        				HeurHazardException, IllegalStateException,
        				RollbackException, SysException {
        			 commitFromWithinCallback ( false, true );
        		}          	
        	});
        }

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
				 rollbackFromWithinCallback(false,false);
			}});
    }

    protected Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {

        throw new IllegalStateException ( "No prepares sent yet." );
    }

}
