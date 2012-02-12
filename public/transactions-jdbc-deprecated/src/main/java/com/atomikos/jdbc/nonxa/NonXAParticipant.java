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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

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

class NonXAParticipant implements Participant, Serializable
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(NonXAParticipant.class);

    private boolean recovered;
    // true iff recovered

    private ArrayList heuristicMessages;

    private transient ThreadLocalConnection connection;

    // not null iff not recovered

    public NonXAParticipant ( ThreadLocalConnection connection )
    {
        recovered = false;
        heuristicMessages = new ArrayList ();
        this.connection = connection;
        heuristicMessages.add ( new StringHeuristicMessage (
                "JDBC driver does not support XA!" ) );
    }

    /**
     * @see com.atomikos.icatch.Participant#recover()
     */
    public boolean recover () throws SysException
    {
        recovered = true;
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
     * $
     *
     * @see com.atomikos.icatch.Participant#prepare()
     */
    public int prepare () throws RollbackException, HeurHazardException,
            HeurMixedException, SysException
    {
        // we don't know if readonly or not -> assume the worst
        return Participant.READ_ONLY + 1;
    }

    /**
     * @see com.atomikos.icatch.Participant#commit(boolean)
     */

    public HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if ( recovered )
            throw new HeurRollbackException ( getHeuristicMessages () );

        try {
            connection.transactionTerminated ( true );
        } catch ( Exception e ) {
            LOGGER.logWarning ( "Error in non-XA commit", e );
            throw new HeurHazardException ( getHeuristicMessages () );
        }

        return getHeuristicMessages ();
    }

    /**
     * @see com.atomikos.icatch.Participant#rollback()
     */
    public HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        // if recovered: do nothing special since rolled back by default

        if ( !recovered ) {
            try {
                connection.transactionTerminated ( false );
            } catch ( Exception e ) {
                LOGGER.logWarning ( "Error in non-XA rollback", e );
                throw new HeurHazardException ( getHeuristicMessages () );
            }
        }
        return getHeuristicMessages ();
    }

    /**
     * @see com.atomikos.icatch.Participant#forget()
     */
    public void forget ()
    {
        // do nothing special, since DB doesn't know about 2PC or heuristics

    }

    /**
     * @see com.atomikos.icatch.Participant#getHeuristicMessages()
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {
        HeuristicMessage[] ret = new HeuristicMessage[0];

        ret = (HeuristicMessage[]) heuristicMessages.toArray ( ret );

        return ret;
    }

    public String getURI ()
    {
        return null;
    }

    void addHeuristicMessage ( HeuristicMessage msg )
    {
        heuristicMessages.add ( msg );
    }

}
