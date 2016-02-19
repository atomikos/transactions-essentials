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
