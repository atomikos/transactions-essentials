/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

class ForgetResult extends Result
{

    public ForgetResult ( int numberOfRepliesToWaitFor )
    {
        super ( numberOfRepliesToWaitFor );

    }

    protected synchronized void calculateResultFromAllReplies() throws IllegalStateException,
            InterruptedException

    {
        // nothing to do here
    }

}
