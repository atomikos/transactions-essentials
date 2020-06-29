/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Participant;

/**
 * A reply for propagation messages.
 */

class Reply
{

    private Exception exception_ = null;

    private Object response_ = null;

    private Participant participant_ = null;

    private boolean retried_ = false;

    /**
     * Constructor.
     *
     * @param response
     *            The response value.
     * @param exception
     *            The exception, or null if none.
     * @param Participant
     *            The participant whose reply this is.
     * @param retried
     *            If true, then the original messages has failed but is
     *            reschuled for retry.
     */

    public Reply ( Object response , Exception exception ,
            Participant participant , boolean retried )
    {
        response_ = response;
        exception_ = exception;
        participant_ = participant;
        retried_ = retried;
    }

    /**
     * Check if response ok.
     *
     * @return boolean True if response is invalid. In that case, getException()
     *         returns the error.
     */

    public boolean hasFailed ()
    {
        return exception_ != null;
    }

    /**
     * To check if retried for failure case.
     *
     * @return boolean True if the original message will be retried.
     */

    public boolean isRetried ()
    {
        return hasFailed () && retried_;
    }

    /**
     * Get any errors. Not null if hasFailed() is true.
     *
     * @return Exception The exception.
     */

    public Exception getException ()
    {
        return exception_;
    }

    /**
     * Get the response. OK if hasFailed() returns false.
     *
     * @return Object Application specific; can be null.
     */

    public Object getResponse ()
    {

        return response_;
    }

    /**
     * Get the participant who replied this.
     *
     * @return Participant.
     */

    public Participant getParticipant ()
    {
        return participant_;
    }
}
