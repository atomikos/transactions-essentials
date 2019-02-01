/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A commit message implementation.
 */

class CommitMessage extends PropagationMessage
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CommitMessage.class);

    private boolean onephase_ = false;

    
    public CommitMessage ( Participant participant , Result result ,
            boolean onephase )
    {
        super ( participant , result );
        onephase_ = onephase;
    }

    /**
     * A commit message.
     * 
     * @return Boolean null.
     * @exception PropagationException
     *                If problems. If heuristics, this will be a heuristic
     *                exception; otherwise, commit has to be retried since
     *                participant can be indoubt. Hence, if not heuristic in
     *                nature, then the error is transient.
     */

    protected Boolean send () throws PropagationException
    {
        Participant part = getParticipant ();
        try {
            part.commit ( onephase_ );
            return null;
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
            LOGGER.logError ( msg, e );
            HeurHazardException heurh = new HeurHazardException();
            throw new PropagationException ( heurh, true );
        }
    }

    public String toString ()
    {
        return ("CommitMessage to " + getParticipant ());
    }

}
