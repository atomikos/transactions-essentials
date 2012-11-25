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

package com.atomikos.icatch.jta;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A participant to add in case setRollbackOnly is called. This participant will
 * never allow commit.
 */

class RollbackOnlyParticipant implements Participant
{
	
	private static final Logger LOG = LoggerFactory.createLogger(RollbackOnlyParticipant.class);

    private StringHeuristicMessage msg_;
    // the message to return in exception

    RollbackOnlyParticipant ( StringHeuristicMessage msg )
    {
        msg_ = msg;
    }

    /**
     * @see Participant
     */

    public boolean recover () throws SysException
    {
        return false;
    }

    /**
     * @see Participant
     */

    public void setCascadeList ( java.util.Dictionary allParticipants )
            throws SysException
    {
        
    }

    /**
     * @see Participant
     */

    public void setGlobalSiblingCount ( int count )
    {
        
    }

    /**
     * @see Participant
     */

    public String getURI ()
    {
        return null;
    }

    /**
     * @see Participant
     */

    public int prepare () throws RollbackException, HeurHazardException,
            HeurMixedException, SysException
    {
        // prepare MUST fail: rollback only!
        throw new RollbackException ( msg_.toString () );
    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if (onePhase) throw new RollbackException(msg_.toString());
        else LOG.logWarning("Unexpected 2-phase commit: outcome should be rollback!");
        return getHeuristicMessages();
    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        return getHeuristicMessages ();
    }

    /**
     * @see Participant
     */

    public void forget ()
    {      
    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {
        HeuristicMessage[] ret = new HeuristicMessage[1];
        ret[0] = msg_;
        return ret;
    }

}
