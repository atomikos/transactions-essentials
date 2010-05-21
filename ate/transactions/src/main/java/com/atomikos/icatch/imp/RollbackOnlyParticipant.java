package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;

/**
 * 
 * 
 * A participant to add in case setRollbackOnly is called. This participant will
 * never allow commit.
 */

public class RollbackOnlyParticipant implements Participant
{
    private StringHeuristicMessage msg_;

    // the message to return in exception

    public RollbackOnlyParticipant ( StringHeuristicMessage msg )
    {
        msg_ = msg;
    }

    /**
     * @see Participant
     */

    public boolean recover () throws SysException
    {
        // by default: does nothing
        return true;
    }

    /**
     * @see Participant
     */

    public void setCascadeList ( java.util.Dictionary allParticipants )
            throws SysException
    {
        // nothing by default
    }

    /**
     * @see Participant
     */

    public void setGlobalSiblingCount ( int count )
    {
        // default does nothing
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
        // if onePhase then this method will be called;
        // make sure rollback is indicated.
        throw new RollbackException ( msg_.toString () );
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
        // nothing to do
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

    /**
     * @see com.atomikos.icatch.Participant#getURI()
     */
    public String getURI ()
    {

        return null;
    }

}
