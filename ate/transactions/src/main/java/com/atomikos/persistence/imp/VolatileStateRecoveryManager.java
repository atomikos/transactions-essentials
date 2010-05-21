package com.atomikos.persistence.imp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;

/**
 * 
 * 
 * 
 * A volatile recovery manager (one that doesn't support persistent logging and
 * hence doesn't allow recovery after a crash or restart).
 * 
 * 
 */

// @todo TEST if this works for the JTA release test!
public class VolatileStateRecoveryManager implements StateRecoveryManager,
        FSMPreEnterListener
{
    private Map idToElementMap;

    /**
     * Construct a new instance.
     * 
     */

    public VolatileStateRecoveryManager ()
    {
        idToElementMap = new HashMap ();
    }

    /**
     * @see StateRecoveryManager
     */

    public void init () throws LogException
    {

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

    public synchronized void preEnter ( FSMEnterEvent event )
            throws IllegalStateException
    {
        Object state = event.getState ();
        StateRecoverable source = (StateRecoverable) event.getSource ();
        ObjectImage img = source.getObjectImage ( state );
        //if img null: do nothing (BUG FIX 10041)
        if ( img != null ) {
	        	StateObjectImage simg = new StateObjectImage ( img );
	        Object[] finalstates = source.getFinalStates ();
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

    public StateRecoverable recover ( Object id ) throws LogException
    {
        StateRecoverable ret = null;

        StateObjectImage simg = (StateObjectImage) idToElementMap.get ( id );
        ret = (StateRecoverable) simg.restore ();

        return ret;
    }

    /**
     * @see StateRecoveryManager
     */

    public Vector recover () throws LogException
    {
        Vector ret = new Vector ();
        Iterator keys = idToElementMap.keySet ().iterator ();
        while ( keys.hasNext () ) {
            Object key = keys.next ();
            StateObjectImage simg = (StateObjectImage) idToElementMap
                    .get ( key );
            ret.add ( simg.restore () );
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

}
