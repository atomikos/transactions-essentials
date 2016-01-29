/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;

/**
 * A rollback message implementation.
 */

class RollbackMessage extends PropagationMessage
{

	private int retrycount_ = 0;
    // no of retries so far

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
