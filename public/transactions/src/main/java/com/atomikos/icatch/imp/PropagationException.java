/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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

	private boolean transient_ = false;

	private Exception detail_ = null;
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
