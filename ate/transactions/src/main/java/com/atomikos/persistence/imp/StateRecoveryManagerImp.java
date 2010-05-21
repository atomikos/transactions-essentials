package com.atomikos.persistence.imp;

import java.util.Enumeration;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;

/**
 * 
 * 
 * A standard implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp implements StateRecoveryManager,
        FSMPreEnterListener
{

    protected ObjectLog objectlog_;

    // for delegation of storage tasks

    /**
     * Construct a new instance that uses an underlying log.
     * 
     * @param objectlog
     *            The log to delegate to.
     */

    public StateRecoveryManagerImp ( ObjectLog objectlog )
    {
        objectlog_ = objectlog;
    }

    /**
     * @see StateRecoveryManager
     */

    public void init () throws LogException
    {
        objectlog_.init ();
    }

    /**
     * @see StateRecoveryManager
     */

    public void register ( StateRecoverable staterecoverable )
    {
        if ( staterecoverable == null )
            throw new IllegalArgumentException ( "null in register arg" );
        Object[] states = staterecoverable.getRecoverableStates ();
        if ( states != null ) {
            for ( int i = 0; i < states.length; i++ ) {
                staterecoverable.addFSMPreEnterListener ( this, states[i] );
            }
            states = staterecoverable.getFinalStates ();
            for ( int i = 0; i < states.length; i++ ) {
                staterecoverable.addFSMPreEnterListener ( this, states[i] );
            }
        }
    }

    /**
     * @see FSMPreEnterListener
     */

    public void preEnter ( FSMEnterEvent event ) throws IllegalStateException
    {
        Object state = event.getState ();
        StateRecoverable source = (StateRecoverable) event.getSource ();
        ObjectImage img = source.getObjectImage ( state );
        if ( img != null ) {
            //null images are not logged as per the Recoverable contract
            StateObjectImage simg = new StateObjectImage ( img );
            Object[] finalstates = source.getFinalStates ();
            boolean delete = false;

            for ( int i = 0; i < finalstates.length; i++ ) {
                if ( state.equals ( finalstates[i] ) )
                    delete = true;
            }

            try {
                if ( !delete )
                    objectlog_.flush ( simg );
                else
                    objectlog_.delete ( simg.getId () );
            } catch ( LogException le ) {
                throw new IllegalStateException (
                        "could not flush state image " + le.getMessage () + " "
                                + le.getClass ().getName () );
            }
        }

    }

    /**
     * @see StateRecoveryManager
     */

    public void close () throws LogException
    {
        objectlog_.close ();
    }

    /**
     * @see StateRecoveryManager
     */

    public StateRecoverable recover ( Object id ) throws LogException
    {
        StateRecoverable srec = (StateRecoverable) objectlog_.recover ( id );
        if ( srec != null ) // null if not found!
            register ( srec );
        return srec;
    }

    /**
     * @see StateRecoveryManager
     */

    public Vector recover () throws LogException
    {
        Vector ret = objectlog_.recover ();
        Enumeration enumm = ret.elements ();
        while ( enumm.hasMoreElements () ) {
            StateRecoverable srec = (StateRecoverable) enumm.nextElement ();
            register ( srec );
        }
        return ret;
    }

    /**
     * @see StateRecoveryManager
     */

    public void delete ( Object id ) throws LogException
    {
        objectlog_.delete ( id );
    }

}
