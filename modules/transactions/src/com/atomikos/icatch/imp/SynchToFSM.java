package com.atomikos.icatch.imp;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A class for wrapping Synchronization to FSMPreEnterListener.
 */

class SynchToFSM implements FSMEnterListener
{
    private Synchronization synch_;

    private boolean aftercompletion_;

    private boolean termination_;

    // to avoid that listeners (e.g. pooled connections)
    // add themselves to the pool twice. This would be
    // very dangerous!

    SynchToFSM ( Synchronization s )
    {
        super ();
        synch_ = s;
        aftercompletion_ = false;
        termination_ = false;
    }
    
    private void doAfterCompletion ( TxState state ) 
    {
    	try {
			synch_.afterCompletion ( state );
		} catch ( RuntimeException e ) {
			//see case 24246: ignore but log
			Configuration.logWarning ( "Error during afterCompletion" , e );
		}
    }

    public void entered ( FSMEnterEvent e )
    {
    	
        if ( e != null ) {
            if ( e.getState ().equals ( TxState.COMMITTING )
                    && (!aftercompletion_) ) {
                doAfterCompletion ( TxState.COMMITTING );
                aftercompletion_ = true;
            } else if ( e.getState ().equals ( TxState.ABORTING )
                    && (!aftercompletion_) ) {
            	doAfterCompletion ( TxState.ABORTING );
                aftercompletion_ = true;
            } else if ( e.getState ().equals ( TxState.TERMINATED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.TERMINATED );
                aftercompletion_ = true;
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_MIXED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_MIXED );
                termination_ = true;

            } else if ( e.getState ().equals ( TxState.HEUR_ABORTED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_ABORTED );
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_HAZARD )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_HAZARD );
                termination_ = true;
            } else if ( e.getState ().equals ( TxState.HEUR_COMMITTED )
                    && !termination_ ) {
            	doAfterCompletion ( TxState.HEUR_COMMITTED );
                termination_ = true;
            }
        }// if
    }
}
