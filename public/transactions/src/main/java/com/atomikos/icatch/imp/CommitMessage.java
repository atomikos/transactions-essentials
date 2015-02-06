/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A commit message implemenation.
 */

class CommitMessage extends PropagationMessage
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CommitMessage.class);

    protected boolean onephase_ = false;

    protected int retrycount_ = 0;
    
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
            LOGGER.logWarning ( msg, e );
            HeurHazardException heurh = new HeurHazardException ( part.getHeuristicMessages () );
            throw new PropagationException ( heurh, true );
        }
    }

    public String toString ()
    {
        return ("CommitMessage to " + getParticipant ());
    }

}
