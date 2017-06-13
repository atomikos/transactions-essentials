/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * A result for prepare messages.
 */

class PrepareResult extends Result
{
    // for read only voters
    private Set<Participant> readonlytable_ = new HashSet<>();

    // for indoubt participants
    // should be rolled back in case of failure!
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<Participant> indoubts_ = new HashSet<>();

    private boolean analyzed_ = false;

    private boolean heurmixed = false;
    private boolean heurhazards = false;
    private boolean heurcommits = false;

    private boolean readonly = false;
    private boolean yes = false;

    /**
     * Constructor.
     *
     * @param count
     *            The number of replies to deal with.
     */

    @SuppressWarnings("WeakerAccess")
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
        heurmixed = false;
        heurhazards = false;
        heurcommits = false;

        Deque<Reply> replies = getReplies ();

        for (Reply reply : replies) {
          yes = false;
          readonly = false;

          // Reply reply = (Reply) enumm.nextElement ();

          if ( reply.hasFailed () ) {
            yes = false;
            readonly = false;

            Exception err = reply.getException ();

            processFailedReply(reply, err);

          }// if failed

          else {
            processSuccessfulReply(reply);
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

  private void processSuccessfulReply(Reply reply)
  {
    readonly = (reply.getResponse () == null);
    Boolean answer = Boolean.FALSE;
    if ( !readonly ) {
      answer = (Boolean) reply.getResponse ();
    }
    yes = (readonly || answer);

    // if readonly: remember this fact for logging and second phase
    if ( readonly ) readonlytable_.add ( reply.getParticipant () );
    else indoubts_.add ( reply.getParticipant ());
  }

  private void processFailedReply(Reply reply, Exception err)
  {
    if ( err instanceof HeurMixedException) {
      heurmixed = true;
    } else if ( err instanceof HeurCommitException) {
      heurcommits = true;
      heurmixed = (heurmixed || heurhazards);
    } else if ( err instanceof HeurHazardException) {
      heurhazards = true;
      heurmixed = (heurmixed || heurcommits);

      // REMEMBER: might be indoubt, so HAS to be notified
      // during rollback!
      indoubts_.add ( reply.getParticipant ());
    }
  }

  /**
     * Test if all answers represent a yes vote. Blocks until all results
     * arrived.
     *
     * @return boolean True if all are yes, false if not.
     * @exception InterruptedException
     *                If interrupt on wait.
     */

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("WeakerAccess")
    public Set<Participant> getReadOnlyTable () throws InterruptedException
    {
        calculateResultFromAllReplies ();
        return readonlytable_;
    }
}
