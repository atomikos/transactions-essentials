/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;

/**
 * A rollback message implementation.
 */

class RollbackMessage extends PropagationMessage
{
	private boolean indoubt_ = false;
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
     * @return Boolean null
     * @exception PropagationException
     *                If problems. If heuristics, this will be a fatal
     *                exception; otherwise, rollback has to be retried since
     *                participant can be indoubt. In that case, the error is
     *                transient in nature.
     */

    protected Boolean send () throws PropagationException
    {
        Participant part = getParticipant ();
        try {
             part.rollback ();

        } catch ( HeurCommitException heurc ) {
            throw new PropagationException ( heurc, false );
        } catch ( HeurMixedException heurm ) {
            throw new PropagationException ( heurm, false );
        }

        catch ( Exception e ) {
            // only retry if might be indoubt. Otherwise ignore.
            if ( indoubt_ ) {
                // here, participant might be indoubt!
                // fill in exact heuristic msgs by using buffered effect of proxies
                HeurHazardException heurh = new HeurHazardException();
                throw new PropagationException ( heurh, true );
            }
        }
        return null;
    }

    public String toString ()
    {
        return ("RollbackMessage to " + getParticipant ());
    }

}
