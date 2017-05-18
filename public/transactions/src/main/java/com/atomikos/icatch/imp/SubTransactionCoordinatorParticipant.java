/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

/**
 * A participant for registering a subtx coordinator as a subordinate in 2PC of
 * the parent transaction coordinator.
 */

public class SubTransactionCoordinatorParticipant implements Participant
{

    private static final long serialVersionUID = -321213151844934630L;

    private transient Participant subordinateCoordinator;
    // the participant role of the subtx coordinator
    // NOT serializable -> recover by ID

    private String subordinateId;
    // the id to recover the participant of the subordinate


    private boolean prepareCalled;
    // if true: heuristics on failure of rollback

    public SubTransactionCoordinatorParticipant (
            CoordinatorImp subordinateCoordinator )
    {
        this.subordinateCoordinator = subordinateCoordinator;
        this.subordinateId = subordinateCoordinator.getCoordinatorId ();
        this.prepareCalled = false;
    }

    /**
     * @see com.atomikos.icatch.Participant#getURI()
     */
    public String getURI ()
    {
        return subordinateId;
    }

  
    public void setCascadeList ( Map<String,Integer> allParticipants )
            throws SysException
    {
        // delegate to subordinate, in order to propagate to remote
        // work (even though the subordinate itself is local, its
        // registered participants may be remote!)
        subordinateCoordinator.setCascadeList ( allParticipants );

    }

    /**
     * @see com.atomikos.icatch.Participant#setGlobalSiblingCount(int)
     */
    public void setGlobalSiblingCount ( int count )
    {
        // delegate to subordinate, in order to propagate
        // to remote work
        subordinateCoordinator.setGlobalSiblingCount ( count );

    }

    /**
     * @see com.atomikos.icatch.Participant#prepare()
     */
    public int prepare () throws RollbackException, HeurHazardException,
            HeurMixedException, SysException
    {
        prepareCalled = true;
        return subordinateCoordinator.prepare ();
    }

    /**
     * @see com.atomikos.icatch.Participant#commit(boolean)
     */
    public void commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        if ( subordinateCoordinator != null )
            subordinateCoordinator.commit ( onePhase );
        else if ( prepareCalled ) {
            throw new HeurHazardException ();
        } else {
            // prepare NOT called -> subordinate timed out
            // and must have rolled back
            throw new RollbackException ();
        }
    }

    /**
     * @see com.atomikos.icatch.Participant#rollback()
     */
    public void rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        if ( subordinateCoordinator != null ) {
            subordinateCoordinator.rollback ();
        } else if ( prepareCalled ) {
            // heuristic: coordinator not recovered?!
            throw new HeurHazardException();
        }
        // if prepare not called then the subordinate will
        // not be committed, so rollback is correct then
    }

    /**
     * @see com.atomikos.icatch.Participant#forget()
     */
    public void forget ()
    {
        if ( subordinateCoordinator != null ) subordinateCoordinator.forget ();
    }

	@Override
	public boolean isRecoverable() {
		return true;
	}

	@Override
	public String getResourceName() {
		return null;
	}

}
