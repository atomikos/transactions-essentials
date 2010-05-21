package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;

/**
 * 
 * 
 * A prepare message implemenation.
 */

class PrepareMessage extends PropagationMessage
{

    public PrepareMessage ( Participant participant , Result result )
    {
        super ( participant , result );
    }

    /**
     * A prepare message.
     * 
     * @return Object Boolean True if YES vote, False if NO vote, null if
     *         readonly vote.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        int ret = 0;
        Boolean result = null;
        try {
            ret = part.prepare ();
            if ( ret == Participant.READ_ONLY )
                result = null;
            else
                result = new Boolean ( true );
        } catch ( HeurHazardException heurh ) {
            throw new PropagationException ( heurh, false );
        } catch ( RollbackException jtr ) {
            // NO vote.
            result = new Boolean ( false );
        } catch ( Exception e ) {
            // here, participant might be indoubt!
            StringHeuristicMessage shm = new StringHeuristicMessage (
                    "Possibly heuristic participant: " + part.toString () );
            HeuristicMessage[] msgs = new HeuristicMessage[1];
            msgs[0] = shm;
            HeurHazardException heurh = new HeurHazardException ( msgs );
            throw new PropagationException ( heurh, false );

        }
        return result;
    }

    public String toString ()
    {
        return ("PrepareMessage to " + getParticipant ());
    }

}
