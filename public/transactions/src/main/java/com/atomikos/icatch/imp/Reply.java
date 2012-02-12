/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * A reply for propagationmessages.
 */

class Reply
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(Reply.class);

    protected Exception exception_ = null;

    protected Object response_ = null;

    protected Participant participant_ = null;

    protected boolean retried_ = false;

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
