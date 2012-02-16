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
