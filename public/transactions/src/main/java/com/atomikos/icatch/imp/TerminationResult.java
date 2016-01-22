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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.TxState;


class TerminationResult extends Result
{
    protected boolean allRepliesProcessed;
    protected Hashtable<Participant,TxState> heuristicparticipants_;
    protected Hashtable<Participant,TxState> possiblyIndoubts_;

    public TerminationResult ( int numberOfRepliesToWaitFor )
    {
        super ( numberOfRepliesToWaitFor );
        allRepliesProcessed = false;
        heuristicparticipants_ = new Hashtable<Participant,TxState>();
        possiblyIndoubts_ = new Hashtable<Participant,TxState>();
    }

    /**
     * @exception IllegalStateException
     *                If not done yet.
     */

    public Hashtable<Participant,TxState> getHeuristicParticipants () throws IllegalStateException,
            InterruptedException
    {
        calculateResultFromAllReplies();
        return heuristicparticipants_;
    }

    /**
     * 
     * @exception IllegalStateException
     *                If comm. not done yet.
     */

    public Hashtable<Participant,TxState> getPossiblyIndoubts () throws IllegalStateException,
            InterruptedException
    {
        calculateResultFromAllReplies ();
        return possiblyIndoubts_;
    }

    protected synchronized void calculateResultFromAllReplies () throws IllegalStateException,
            InterruptedException

    {
        if (allRepliesProcessed) return;

        boolean atLeastOneHeuristicMixedException = false;
        boolean atLeastOneHeuristicRollbackException = false;
        boolean atLeastOneHeuristicCommitException = false;
        boolean atLeastOneHeuristicHazardException = false;
        boolean noFailedReplies = true;
        boolean onePhaseCommitWithRollbackException = false;

        Stack<Reply> replies = getReplies();
        Enumeration<Reply> enumm = replies.elements ();

        while ( enumm.hasMoreElements () ) {

            Reply reply = (Reply) enumm.nextElement ();

            if ( reply.hasFailed () ) {
                noFailedReplies = false;
                Exception err = reply.getException ();
                if ( err instanceof RollbackException ) {
                    onePhaseCommitWithRollbackException = true;
                } else if ( err instanceof HeurMixedException ) {
                    atLeastOneHeuristicMixedException = true;
                    HeurMixedException hm = (HeurMixedException) err;
                    heuristicparticipants_.put ( reply.getParticipant (),
                            TxState.HEUR_MIXED );
                } else if ( err instanceof HeurCommitException ) {
                    atLeastOneHeuristicCommitException = true;
                    HeurCommitException hc = (HeurCommitException) err;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicRollbackException || atLeastOneHeuristicHazardException);
                    heuristicparticipants_.put ( reply.getParticipant (),
                            TxState.HEUR_COMMITTED );

                } else if ( err instanceof HeurRollbackException ) {
                    atLeastOneHeuristicRollbackException = true;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicCommitException || atLeastOneHeuristicHazardException);
                    HeurRollbackException hr = (HeurRollbackException) err;
                    heuristicparticipants_.put ( reply.getParticipant (),
                            TxState.HEUR_ABORTED );

                } else {

                    atLeastOneHeuristicHazardException = true;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicRollbackException || atLeastOneHeuristicCommitException);
                    heuristicparticipants_.put ( reply.getParticipant (),
                            TxState.HEUR_HAZARD );
                    possiblyIndoubts_.put ( reply.getParticipant (),
                            TxState.HEUR_HAZARD );

                }
            }
        } 

        if ( onePhaseCommitWithRollbackException )
            result_ = ROLLBACK;
        else if ( atLeastOneHeuristicMixedException || atLeastOneHeuristicRollbackException
                && heuristicparticipants_.size () != replies.size ()
                || atLeastOneHeuristicCommitException
                && heuristicparticipants_.size () != replies.size () )
            result_ = HEUR_MIXED;
        else if ( atLeastOneHeuristicHazardException ) {
            // heur hazard BEFORE heur abort or commit!
            // see OTS definitions: hazard ASA some unknown, 
        	// but ALL KNOWN ARE COMMIT OR ALL ARE ABORT
            result_ = HEUR_HAZARD;
        } else if ( atLeastOneHeuristicRollbackException ) {
            result_ = HEUR_ROLLBACK;
            // here, there can be no heur commits as well, since otherwise mixed
            // would have fitted. Same for hazards.

        } else if ( atLeastOneHeuristicCommitException ) {
            // here, there can be no heur aborts as well, since mixed would have
            // fitted. no hazards either, since hazards would have fitted.
            result_ = HEUR_COMMIT;

        } else if ( noFailedReplies )
            result_ = ALL_OK;

        allRepliesProcessed = true;
    }

}
