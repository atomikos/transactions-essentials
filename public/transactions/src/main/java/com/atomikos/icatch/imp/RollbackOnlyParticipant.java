/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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

class RollbackOnlyParticipant implements Participant
{

	private static final Logger LOG = LoggerFactory.createLogger(RollbackOnlyParticipant.class);

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
        else LOG.logError("Unexpected 2-phase commit: outcome should be rollback!");
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
	public String getResourceName() {
		return null;
	}

	@Override
	public void setCascadeList(Map<String, Integer> cascadeList)
			throws SysException {
		
	}
	
}
