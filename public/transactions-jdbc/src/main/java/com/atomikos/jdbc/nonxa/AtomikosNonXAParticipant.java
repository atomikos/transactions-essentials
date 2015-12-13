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

package com.atomikos.jdbc.nonxa;


import java.io.Serializable;
import java.util.Dictionary;

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
 *
 *
 *
 * A participant for non-XA interactions. Instances are NOT recoverable in the
 * sense that commit/rollback will fail after prepare. This is an implicit
 * limitation of non-XA transactions and we want this to be made explicit in the
 * transaction logs.
 *
 *
 *
 *
 */

public class AtomikosNonXAParticipant implements Participant, Serializable
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAParticipant.class);



	private static final long serialVersionUID = -771461092384746954L;

	private boolean readOnly;

	private String name;

    public AtomikosNonXAParticipant() {
	}
    private transient JtaAwareNonXaConnection connection;

    // not null iff not recovered

    public AtomikosNonXAParticipant ( JtaAwareNonXaConnection connection , String name )
    {
        this.connection = connection;
        this.name = name;
    }

    /**
     * @see com.atomikos.icatch.Participant#recover()
     */
    public boolean recover () throws SysException
    {
        // return true at this stage: there is a problem only when commit is
        // requested
        // and not for rollback requests!
        return true;
    }

    /**
     * @see com.atomikos.icatch.Participant#setCascadeList(java.util.Dictionary)
     */
    public void setCascadeList ( Dictionary allParticipants )
            throws SysException
    {
        // ignore

    }

    /**
     * @see com.atomikos.icatch.Participant#setGlobalSiblingCount(int)
     */
    public void setGlobalSiblingCount ( int count )
    {
        // ignore

    }

    /*
     *
     *
     * @see com.atomikos.icatch.Participant#prepare()
     */
    public int prepare () throws RollbackException, HeurHazardException,
            HeurMixedException, SysException
    {
        // DON'T return readOnly: we need the terminated event to reuse the connection in the pool!
        int ret = Participant.READ_ONLY + 1;
    	return ret;
    }

    /**
     * @see com.atomikos.icatch.Participant#commit(boolean)
     */

    public void commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if ( isRecovered() ) {
            if ( ! readOnly ) throw new HeurRollbackException();
            // do nothing if readOnly: no commit needed, so don't raise unnecessary heuristic either
        } else {
        	 // not recovered -> connection not null -> commit
        	 try {
                 connection.transactionTerminated ( true );
             } catch ( Exception e ) {
                 LOGGER.logWarning ( "Error in non-XA commit", e );
                 //see case 30752: don't throw HAZARD because retries are probably useless
                 //and the connection won't be reused by the pool but destroyed instead
                 throw new HeurMixedException();
             }
        }
    }

    private boolean isRecovered()
    {
		return connection == null;
	}

	/**
     * @see com.atomikos.icatch.Participant#rollback()
     */
    public void rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        // if recovered: do nothing special since rolled back by default

        if ( !isRecovered() ) {
            try {
                connection.transactionTerminated ( false );
            } catch ( Exception e ) {
                LOGGER.logWarning ( "Error in non-XA rollback", e );
                //see case 30752: don't throw HAZARD because retries are probably useless
                //and the connection won't be reused by the pool but destroyed instead
                throw new HeurMixedException();
            }
        }
    }

    /**
     * @see com.atomikos.icatch.Participant#forget()
     */
    public void forget ()
    {
        // do nothing special, since DB doesn't know about 2PC or heuristics

    }


    public String getURI ()
    {
        return null;
    }

	public void setReadOnly ( boolean readOnly )
	{
		this.readOnly = readOnly;
	}


	@Override
	public String toString() {
		return "Non-XA resource '" + name +
        "': warning: this resource does not support two-phase commit";
	}
}
