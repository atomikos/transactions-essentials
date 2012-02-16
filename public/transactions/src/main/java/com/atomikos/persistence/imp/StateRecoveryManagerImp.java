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

package com.atomikos.persistence.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
 * Default implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp implements StateRecoveryManager,
        FSMPreEnterListener
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(StateRecoveryManagerImp.class);

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
