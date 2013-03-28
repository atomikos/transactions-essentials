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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.icatch.TxState;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.Ordered;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;

/**
 * A volatile recovery manager (one that doesn't support persistent logging and
 * hence doesn't allow recovery after a crash or restart).
 *
 */

public class VolatileStateRecoveryManager implements StateRecoveryManager,
        FSMPreEnterListener<TxState>
{

    private Map<Object,StateObjectImage> idToElementMap;


    public VolatileStateRecoveryManager ()
    {
        idToElementMap = new HashMap<Object,StateObjectImage> ();
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

    public synchronized void preEnter ( FSMEnterEvent<TxState> event )
            throws IllegalStateException
    {
    	TxState state = event.getState ();
        StateRecoverable<TxState> source = (StateRecoverable<TxState>) event.getSource ();
        ObjectImage img = source.getObjectImage ( state );
        //if img null: do nothing (BUG FIX 10041)
        if ( img != null ) {
	        	StateObjectImage simg = new StateObjectImage ( img );
	        	TxState[] finalstates = source.getFinalStates ();
	        boolean delete = false;

	        for ( int i = 0; i < finalstates.length; i++ ) {
	            if ( state.equals ( finalstates[i] ) )
	                delete = true;
	        }

	        if ( !delete )
	            idToElementMap.put ( simg.getId (), simg );
	        else
	            idToElementMap.remove ( simg.getId () );
        }

    }

    /**
     * @see StateRecoveryManager
     */

    public void close () throws LogException
    {

    }

    /**
     * @see StateRecoveryManager
     */

    public StateRecoverable<TxState> recover ( Object id ) throws LogException
    {
        StateRecoverable<TxState> ret = null;

        StateObjectImage simg = (StateObjectImage) idToElementMap.get ( id );
        if (simg != null) ret = (StateRecoverable<TxState>) simg.restore ();
        return ret;
    }

    /**
     * @see StateRecoveryManager
     */

    public Vector<StateRecoverable<TxState>> recover () throws LogException
    {
        Vector<StateRecoverable<TxState>> ret = new Vector<StateRecoverable<TxState>> ();
        Iterator<Object> keys = idToElementMap.keySet ().iterator ();
        while ( keys.hasNext () ) {
            Object key = keys.next ();
            StateObjectImage simg = (StateObjectImage) idToElementMap
                    .get ( key );
            ret.add ( (StateRecoverable<TxState>)simg.restore () );
        }
        return ret;
    }

    /**
     * @see StateRecoveryManager
     */

    public synchronized void delete ( Object id ) throws LogException
    {
        idToElementMap.remove ( id );
    }

	public void init(Properties properties) throws LogException {
		// nothing to do...		
	}


	public int getOrder() {
		
		return Ordered.LOWEST_PRECEDENCE;
	}

}
