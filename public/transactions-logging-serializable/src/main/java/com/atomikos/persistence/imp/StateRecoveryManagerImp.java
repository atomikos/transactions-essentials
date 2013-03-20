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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.Utils;

/**
 * Default implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp implements StateRecoveryManager,
        FSMPreEnterListener<TxState>
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

    public StateRecoveryManagerImp() {

	}

    /**
     * @see StateRecoveryManager
     */

    public void register ( StateRecoverable<TxState> staterecoverable )
    {
        if ( staterecoverable == null )
            throw new IllegalArgumentException ( "null in register arg" );
        TxState[] states = staterecoverable.getRecoverableStates ();
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

    public void preEnter ( FSMEnterEvent<TxState> event ) throws IllegalStateException
    {
    	TxState state = event.getState ();
        StateRecoverable<TxState> source = (StateRecoverable<TxState>) event.getSource ();
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

    public StateRecoverable<TxState> recover ( Object id ) throws LogException
    {
        StateRecoverable<TxState> srec = (StateRecoverable<TxState>) objectlog_.recover ( id );
        if ( srec != null ) // null if not found!
            register ( srec );
        return srec;
    }

    /**
     * @see StateRecoveryManager
     */

    public Vector<StateRecoverable<TxState>> recover () throws LogException
    {
    	Vector<StateRecoverable<TxState>> ret = objectlog_.recover ();
        Enumeration<StateRecoverable<TxState>> enumm = ret.elements ();
        while ( enumm.hasMoreElements () ) {
            StateRecoverable<TxState> srec =  enumm.nextElement ();
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

	public void init(Properties p) throws LogException {
		long chckpt = Long.valueOf( Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, p ) );

        String logdir = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, p );
        String logname = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, p );
        logdir = Utils.findOrCreateFolder ( logdir );
		LogStream logstream;
		try {
			logstream = new FileLogStream ( logdir, logname );
			objectlog_ = new StreamObjectLog ( logstream, chckpt );
			objectlog_.init();
		} catch (IOException e) {
			throw new LogException(e.getMessage(), e);
		}
		
	}
	

}
