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


/**
 *
 *
 * An error of propagation messages. Some errors are transient, leading to a
 * retry of the message send. Others are fatal, and reported back to the sender.
 */

class PropagationException extends java.io.IOException
{

    protected boolean transient_ = false;
    // default is fatal

    protected Exception detail_ = null;

    // wrapped exception

    /**
     * Constructor.
     *
     * @param detail
     *            The wrapped exception.
     * @param trans
     *            If true, then the failure will NOT be reported to the Sender,
     *            but rather be dealt with in the Propagator by retrying it.
     */

    public PropagationException ( Exception detail , boolean trans )
    {
        super ();
        transient_ = trans;
        detail_ = detail;
    }

    /**
     * Get transient flag.
     *
     * @return boolean True if the error is transient and its message can be
     *         retried.
     */

    public boolean isTransient ()
    {
        return transient_;
    }

    /**
     * Get detail.
     *
     * @return Exception The underlying error cause.
     */

    public Exception getDetail ()
    {
        return detail_;
    }

    public void printStackTrace ()
    {
        super.printStackTrace ();
        if ( detail_ != null )
            detail_.printStackTrace ();
    }

}
