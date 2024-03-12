/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.config.Configuration;

/**
 * A PropagationMessage is used for propagation of 2pc communication.
 */

abstract class PropagationMessage
{

    /**
     * How many times is message re-sent if comm. failure?
     */

	private static final int MAX_RETRIES_ON_COMM_FAILURE = Configuration.getConfigProperties().getOltpMaxRetries();

    private Participant participant_;
    private int retrycount_ = 0;
    private Result result_ = null;

    public PropagationMessage ( Participant participant , Result result )
    {
        participant_ = participant;
        result_ = result;
    }

    public Participant getParticipant ()
    {
        return participant_;
    }

    /**
     * @exception PropagationException
     *                If any. If the exception is transient, then this instance
     *                will retry calling send() until it succeeds. Otherwise, a
     *                failure is reported to the Result object.
     */

    protected abstract Object send() throws PropagationException;

    /**
     * Called by system to process message. This will call the send() method and
     * return a reply to the result object.
     *
     * @return boolean If true, then it should be tried again on failure.
     */

    protected boolean submit ()
    {
        boolean failed = false;
        boolean transienterr = false;
        Exception exception = null;
        Object result = null;
        boolean retried = false;

        try {
            result = send ();
        } catch ( PropagationException e ) {
            failed = true;
            transienterr = e.isTransient ();
            exception = e.getDetail ();
        } finally {
            if ( failed && transienterr && retrycount_ < MAX_RETRIES_ON_COMM_FAILURE ) {
                retried = true;
                retrycount_++;
            }
            if ( result_ != null ) {
                result_.addReply ( new Reply ( result, exception,
                        getParticipant (), retried ) );
            }
        }
        return retried;
    }

}
