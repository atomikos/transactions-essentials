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

import com.atomikos.icatch.Participant;

/**
 *
 *
 * A forget message implemenation.
 */

class ForgetMessage extends PropagationMessage
{

    ForgetMessage ( Participant participant )
    {
        super ( participant , null );
    }

    ForgetMessage ( Participant p , ForgetResult result )
    {
        super ( p , result );
    }

    /**
     * A forget message.
     *
     * @return Object The participant to whom this was sent.
     * @exception PropagationException
     *                Never returned; we don't care now.
     */

    protected Object send () throws PropagationException
    {
        try {
            Participant part = getParticipant ();
            part.forget ();

        } catch ( Exception e ) {
        }

        return getParticipant ();
    }

    public String toString ()
    {
        return ("ForgetMessage to " + getParticipant ());
    }

}
