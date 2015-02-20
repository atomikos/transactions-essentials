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


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import com.atomikos.icatch.DataSerializable;
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

public class AtomikosNonXAParticipant implements Participant, Serializable,DataSerializable
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAParticipant.class);



	private static final long serialVersionUID = -771461092384746954L;

	private boolean readOnly;

    private List<HeuristicMessage> heuristicMessages;

    public AtomikosNonXAParticipant() {
	}
    private transient JtaAwareNonXaConnection connection;

    // not null iff not recovered

    public AtomikosNonXAParticipant ( JtaAwareNonXaConnection connection , String name )
    {
        heuristicMessages = new ArrayList<HeuristicMessage> ();
        this.connection = connection;
        heuristicMessages.add ( new StringHeuristicMessage ( "Non-XA resource '" + name +
                "': warning: this resource does not support two-phase commit" ) );
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

    public HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if ( isRecovered() ) {
            if ( ! readOnly ) throw new HeurRollbackException ( getHeuristicMessages () );
            // do nothing if readOnly: no commit needed, so don't raise unnecessary heuristic either
        } else {
        	 // not recovered -> connection not null -> commit
        	 try {
                 connection.transactionTerminated ( true );
             } catch ( Exception e ) {
                 LOGGER.logWarning ( "Error in non-XA commit", e );
                 //see case 30752: don't throw HAZARD because retries are probably useless
                 //and the connection won't be reused by the pool but destroyed instead
                 throw new HeurMixedException ( getHeuristicMessages () );
             }
        }



        return getHeuristicMessages ();
    }

    private boolean isRecovered()
    {
		return connection == null;
	}

	/**
     * @see com.atomikos.icatch.Participant#rollback()
     */
    public HeuristicMessage[] rollback () throws HeurCommitException,
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
                throw new HeurMixedException ( getHeuristicMessages () );
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
    	if(msg!=null){
    		heuristicMessages.add ( msg );
    	}

    }

	public void setReadOnly ( boolean readOnly )
	{
		this.readOnly = readOnly;
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeBoolean(readOnly);
		out.writeInt(heuristicMessages.size());
		for (Iterator<HeuristicMessage> iterator = heuristicMessages.iterator(); iterator.hasNext();) {
			HeuristicMessage heuristicMessage =  iterator.next();
			out.writeUTF(heuristicMessage.toString());
		}

	}

	public void readData(DataInput in) throws IOException {
		readOnly=in.readBoolean();
		int nbMessages=in.readInt();
		heuristicMessages=new ArrayList<HeuristicMessage>(nbMessages);
		for (int i = 0; i < nbMessages; i++) {
			heuristicMessages.add(new StringHeuristicMessage(in.readUTF()));
		}

	}

}
