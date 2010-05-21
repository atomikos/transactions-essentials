package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A commit message implemenation.
 */

class CommitMessage extends PropagationMessage
{
    protected boolean onephase_ = false;

    protected int retrycount_ = 0;

    // no of tries retry is done

    public CommitMessage ( Participant participant , Result result ,
            boolean onephase )
    {
        super ( participant , result );
        onephase_ = onephase;
    }

    /**
     * A commit message.
     * 
     * @return Object An array of heuristic messages.
     * @exception PropagationException
     *                If problems. If heuristics, this will be a heuristic
     *                exception; otherwise, commit has to be retried since
     *                participant can be indoubt. Hence, if not heuristic in
     *                nature, then the error is transient.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        HeuristicMessage[] msgs = null;
        try {
            msgs = part.commit ( onephase_ );
            return msgs;
        } catch ( RollbackException rb ) {
            throw new PropagationException ( rb, false );
        } catch ( HeurMixedException heurm ) {
            throw new PropagationException ( heurm, false );
        } catch ( HeurRollbackException heurr ) {
            throw new PropagationException ( heurr, false );
        } catch ( Exception e ) {
            // heuristic hazard or not, participant might be indoubt.
            // fill in exact heuristic messages by using buffer effect
            // of participant proxies.
            String msg = "Unexpected error in commit";
            Configuration.logWarning ( msg, e );
            HeurHazardException heurh = new HeurHazardException ( part
                    .getHeuristicMessages () );
            throw new PropagationException ( heurh, true );
        }
    }

    public String toString ()
    {
        return ("CommitMessage to " + getParticipant ());
    }

}
