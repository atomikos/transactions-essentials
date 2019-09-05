/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;


class TerminationResult extends Result
{
	private boolean allRepliesProcessed;
    private Set<Participant> heuristicparticipants_;
    private Set<Participant> possiblyIndoubts_;

    public TerminationResult ( int numberOfRepliesToWaitFor )
    {
        super ( numberOfRepliesToWaitFor );
        allRepliesProcessed = false;
        heuristicparticipants_ = new HashSet<Participant>();
        possiblyIndoubts_ = new HashSet<Participant>();
    }

    /**
     * @exception IllegalStateException
     *                If not done yet.
     */

    public Set<Participant> getHeuristicParticipants () throws IllegalStateException,
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

    public Set<Participant> getPossiblyIndoubts () throws IllegalStateException,
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
                    heuristicparticipants_.add ( reply.getParticipant ());
                } else if ( err instanceof HeurCommitException ) {
                    atLeastOneHeuristicCommitException = true;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicRollbackException || atLeastOneHeuristicHazardException);
                    heuristicparticipants_.add ( reply.getParticipant () );

                } else if ( err instanceof HeurRollbackException ) {
                    atLeastOneHeuristicRollbackException = true;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicCommitException || atLeastOneHeuristicHazardException);
                    heuristicparticipants_.add ( reply.getParticipant ());

                } else {

                    atLeastOneHeuristicHazardException = true;
                    atLeastOneHeuristicMixedException = (atLeastOneHeuristicMixedException || atLeastOneHeuristicRollbackException || atLeastOneHeuristicCommitException);
                    heuristicparticipants_.add ( reply.getParticipant ());
                    possiblyIndoubts_.add ( reply.getParticipant ());

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
