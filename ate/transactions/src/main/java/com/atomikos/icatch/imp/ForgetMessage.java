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
