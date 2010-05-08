//$Id: VolatileStateRecoveryManager.java,v 1.2 2006/09/15 08:39:24 guy Exp $
//$Log: VolatileStateRecoveryManager.java,v $
//Revision 1.2  2006/09/15 08:39:24  guy
//Merged-in changes from 3.0.1 release.
//
//Revision 1.1.1.1.2.1  2006/09/12 07:31:52  guy
//FIXED 10041
//
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:07  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2004/10/18 08:48:55  guy
//Added a VolatileStateRecoveryManager to support disabled recovery.
//
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
