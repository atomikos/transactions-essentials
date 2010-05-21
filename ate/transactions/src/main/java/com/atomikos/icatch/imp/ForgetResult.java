package com.atomikos.icatch.imp;

/**
 * 
 * 
 * A result object for forget messages.
 */

class ForgetResult extends Result
{

    protected boolean analyzed_;

    // true if all answers processed

    /**
     * Constructor.
     * 
     * @param count
     *            The number of messages to process.
     */

    public ForgetResult ( int count )
    {
        super ( count );
        analyzed_ = false;

    }

    protected synchronized void analyze () throws IllegalStateException,
            InterruptedException

    {
        // nothing to do here
    }

}
