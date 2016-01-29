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

import java.util.Map;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A participant to add in case setRollbackOnly is called. This participant will
 * never allow commit.
 */

public class RollbackOnlyParticipant implements Participant
{

	private static final Logger LOG = LoggerFactory.createLogger(RollbackOnlyParticipant.class);

    

    RollbackOnlyParticipant() {}

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
        throw new RollbackException();
    }

    /**
     * @see Participant
     */

    public void commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if (onePhase) throw new RollbackException();
        else LOG.logWarning("Unexpected 2-phase commit: outcome should be rollback!");
    }

    /**
     * @see Participant
     */

    public void rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
    }

    /**
     * @see Participant
     */

    public void forget ()
    {      
    }


	@Override
	public String toString() {
		return "RollbackOnlyParticipant";
	}

	@Override
	public boolean isRecoverable() {
		return false;
	}

	@Override
	public String getResourceName() {
		return null;
	}

	@Override
	public void setCascadeList(Map<String, Integer> cascadeList)
			throws SysException {
		
	}
	
}
