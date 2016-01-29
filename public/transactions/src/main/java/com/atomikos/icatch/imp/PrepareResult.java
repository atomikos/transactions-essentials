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
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;

/**
 * A result for prepare messages.
 */

class PrepareResult extends Result
{

    private Set<Participant> readonlytable_ = new HashSet<Participant> ();
    // for read only voters
    private Set<Participant> indoubts_ = new HashSet<Participant>();
    // for indoubt participants
    // should be rolled back in case of failure!

    private boolean analyzed_ = false;

    /**
     * Constructor.
     *
     * @param count
     *            The number of replies to deal with.
     */

    public PrepareResult ( int count )
    {
        super ( count );
    }

    protected synchronized void calculateResultFromAllReplies () throws IllegalStateException,
            InterruptedException
    {
        if ( analyzed_ )
            return;

        boolean allReadOnly = true;
        boolean allYes = true;
        boolean heurmixed = false;
        boolean heurhazards = false;
        boolean heurcommits = false;
        Stack<Reply> replies = getReplies ();
        Enumeration<Reply> enumm = replies.elements ();

        while ( enumm.hasMoreElements () ) {
            boolean yes = false;
            boolean readonly = false;

            Reply reply = (Reply) enumm.nextElement ();

            if ( reply.hasFailed () ) {
                yes = false;
                readonly = false;

                Exception err = reply.getException ();
                if ( err instanceof HeurMixedException ) {
                    heurmixed = true;
                } else if ( err instanceof HeurCommitException ) {
                    heurcommits = true;
                    heurmixed = (heurmixed || heurhazards);
                } else if ( err instanceof HeurHazardException ) {
                    heurhazards = true;
                    heurmixed = (heurmixed || heurcommits);
                    indoubts_.add ( reply.getParticipant ());
                    // REMEMBER: might be indoubt, so HAS to be notified
                    // during rollback!
                }

            }// if failed

            else {
                readonly = (reply.getResponse () == null);
                Boolean answer = new Boolean ( false );
                if ( !readonly ) {
                    answer = (Boolean) reply.getResponse ();
                }
                yes = (readonly || answer.booleanValue ());

                // if readonly: remember this fact for logging and second phase
                if ( readonly ) readonlytable_.add ( reply.getParticipant () );
                else indoubts_.add ( reply.getParticipant ());
            }

            allYes = (allYes && yes);
            allReadOnly = (allReadOnly && readonly);

        }

        if ( heurmixed )
            result_ = HEUR_MIXED;
        else if ( heurcommits )
            result_ = HEUR_COMMIT;
        else if ( heurhazards )
            result_ = HEUR_HAZARD;
        else if ( allReadOnly )
            result_ = ALL_READONLY;
        else if ( allYes )
            result_ = ALL_OK;

        analyzed_ = true;
    }

    /**
     * Test if all answers represent a yes vote. Blocks until all results
     * arrived.
     *
     * @return boolean True if all are yes, false if not.
     * @exception InterruptedException
     *                If interrupt on wait.
     */

    public boolean allYes () throws InterruptedException
    {
        calculateResultFromAllReplies ();
        return (result_ == ALL_OK || result_ == ALL_READONLY);

    }

    /**
     * Test if all answers were readonly votes. Blocks till all results known.
     *
     * @return boolean True if all readonly, false otherwise.
     * @exception InterruptedException
     *                If interrupted.
     */

    public boolean allReadOnly () throws InterruptedException
    {
        calculateResultFromAllReplies ();
        return (result_ == ALL_READONLY);
    }

    /**
     * Get a table of readonly voting participants.
     *
     * @return Set Contains readonly participant.
     * @exception InterruptedException
     *                If interrupted on wait.
     */

    public Set<Participant> getReadOnlyTable () throws InterruptedException
    {
        calculateResultFromAllReplies ();
        return readonlytable_;
    }

}
