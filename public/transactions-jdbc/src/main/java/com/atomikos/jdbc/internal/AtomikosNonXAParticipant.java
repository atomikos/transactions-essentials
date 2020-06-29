/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;


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
 *
 *
 *
 * A participant for non-XA interactions. Instances are NOT recoverable in the
 * sense that commit/rollback can fail after prepare. This is an implicit
 * limitation of non-XA transactions.
 *
 *
 *
 *
 */

class AtomikosNonXAParticipant implements Participant
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAParticipant.class);

	private boolean readOnly;

	private String name;

	private NonXaConnectionProxy connection;
    
    AtomikosNonXAParticipant ( NonXaConnectionProxy connection , String name )
    {
        this.connection = connection;
        this.name = name;
    }

    /**
     * @see Participant
     */

    public void setCascadeList ( Map<String, Integer> allParticipants )
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
        if (!readOnly) {
            //see https://github.com/atomikos/transactions-essentials/issues/83
            String msg = this.toString();
            LOGGER.logWarning(msg);
            throw new RollbackException(msg);
        }
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
        try {
            connection.transactionTerminated ( true );
        } catch ( Exception e ) {
            LOGGER.logError ( "Error in non-XA commit", e );
            //see case 30752: don't throw HAZARD because retries are probably useless
            //and the connection won't be reused by the pool but destroyed instead
            throw new HeurMixedException();
        }   
    }


	/**
     * @see com.atomikos.icatch.Participant#rollback()
     */
    public void rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
       
        try {
            connection.transactionTerminated ( false );
        } catch ( Exception e ) {
            LOGGER.logError ( "Error in non-XA rollback", e );
            //see case 30752: don't throw HAZARD because retries are probably useless
            //and the connection won't be reused by the pool but destroyed instead
            throw new HeurMixedException();
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
		return com.atomikos.jdbc.AtomikosNonXADataSourceBean.class.getName() + " '" + name +
        "' [NB: this resource does not support two-phase commit unless configured as readOnly]";
	}

	@Override
	public String getResourceName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
	    boolean ret = false;
	    if (o instanceof AtomikosNonXAParticipant) {
	        AtomikosNonXAParticipant other = (AtomikosNonXAParticipant) o;
	        ret = connection == other.connection;
	    }
	    return ret;
	}
	
	@Override
	public int hashCode() {
	    return connection.hashCode();
	}
}
