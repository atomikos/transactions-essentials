//$Id: Sync2Sync.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: Sync2Sync.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/08/05 15:03:40  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.5  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/09/17 16:41:53  guy
//Improved log methods in Configuration.
//
//Revision 1.3  2004/09/03 10:00:22  guy
//*** empty log message ***
//
//Revision 1.2  2004/09/02 08:21:04  guy
//Corrected to avoid double calls in afterCompletion.
//Added tolerance for non-delisting appservers.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.jta;

import javax.transaction.Status;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An adaptor for the native icatch Synchronization towards JTA synch.
 */

class Sync2Sync implements com.atomikos.icatch.Synchronization
{
    protected javax.transaction.Synchronization sync_;

    private Boolean committed_;

    // null for readonly, True for commit, False for abort

    Sync2Sync ( javax.transaction.Synchronization sync )
    {
        sync_ = sync;

    }

    public void beforeCompletion ()
    {
        sync_.beforeCompletion ();
        // reset flag to allow reuse of sync objects!
        committed_ = null;
    }

    public void afterCompletion ( Object state )
    {
        if ( state.equals ( TxState.TERMINATED ) ) {
            if ( committed_ == null ) {

                // happens on readonly vote -> outcome not known!
                sync_.afterCompletion ( Status.STATUS_UNKNOWN );
                Configuration
                        .logInfo ( "afterCompletion ( STATUS_UNKNOWN ) called "
                                + " on Synchronization: " + sync_.toString () );
            } else {
                boolean commit = committed_.booleanValue ();
                if ( commit ) {
                    sync_.afterCompletion ( Status.STATUS_COMMITTED );
                    Configuration
                            .logInfo ( "afterCompletion ( STATUS_COMMITTED ) called "
                                    + " on Synchronization: "
                                    + sync_.toString () );
                } else {
                    sync_.afterCompletion ( Status.STATUS_ROLLEDBACK );
                    Configuration
                            .logInfo ( "afterCompletion ( STATUS_ROLLEDBACK ) called "
                                    + " on Synchronization: "
                                    + sync_.toString () );
                }
            }
        } else if ( state.equals ( TxState.COMMITTING ) )
            committed_ = new Boolean ( true );
        else if ( state.equals ( TxState.ABORTING ) )
            committed_ = new Boolean ( false );

    }
}
