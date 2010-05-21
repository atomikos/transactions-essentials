package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * A rollback message implemenation.
 */

class RollbackMessage extends PropagationMessage
{
    protected int retrycount_ = 0;
    // no of retries so far

    protected boolean indoubt_ = false;

    // true if participant can be indoubt.

    public RollbackMessage ( Participant participant , Result result ,
            boolean indoubt )
    {
        super ( participant , result );
        indoubt_ = indoubt;
    }

    /**
     * A rollback message.
     * 
     * @return Object An array of heuristic messages.
     * @exception PropagationException
     *                If problems. If heuristics, this will be a fatal
     *                exception; otherwise, rollback has to be retried since
     *                participant can be indoubt. In that case, the error is
     *                transient in nature.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        HeuristicMessage[] msgs = null;
        try {
            msgs = part.rollback ();

        } catch ( HeurCommitException heurc ) {
            // System.err.println ( "RollbackMessage: heur commit detected" );
            throw new PropagationException ( heurc, false );
        } catch ( HeurMixedException heurm ) {
            throw new PropagationException ( heurm, false );
        }

        catch ( Exception e ) {
            // only retry if might be indoubt. Otherwise ignore.
            if ( indoubt_ ) {
                // here, participant might be indoubt!
                // fill in exact heuristic msgs by using buffered effect
                // of proxies
                HeurHazardException heurh = new HeurHazardException ( part
                        .getHeuristicMessages () );
                throw new PropagationException ( heurh, true );
            }
        }
        return msgs;
    }

    public String toString ()
    {
        return ("RollbackMessage to " + getParticipant ());
    }

}
