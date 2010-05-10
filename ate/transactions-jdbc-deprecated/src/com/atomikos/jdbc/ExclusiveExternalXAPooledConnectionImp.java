//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//$Log: ExclusiveExternalXAPooledConnectionImp.java,v $
//Revision 1.3  2006/12/21 07:30:38  guy
//Merged in changes of 3.1.3 release
//
//Revision 1.2.2.1  2006/12/18 13:21:17  guy
//FIXED 10102
//
//Revision 1.2  2006/09/19 08:03:55  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:01  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:14  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/11/15 08:38:22  guy
//Updated to correct bug: aborted connections were not reused.
//
//Revision 1.6  2004/11/13 16:47:46  guy
//Debugged.
//
//Revision 1.5  2004/11/13 10:23:53  guy
//Debugged.
//
//Revision 1.4  2004/10/12 13:04:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Revision 1.3  2004/10/01 12:54:25  guy
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Debugged and tested..
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Revision 1.2  2004/10/01 08:56:44  guy
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//DebuggedDebugged
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Revision 1.1  2004/09/30 09:56:18  guy
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Added support for external pools.
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Implemented support for late enlistment (start of tx after getConnection()).
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Revision 1.2  2003/03/11 06:42:18  guy
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: ExclusiveExternalXAPooledConnectionImp.java,v 1.3 2006/12/21 07:30:38 guy Exp $
//
//Revision 1.1.2.3  2003/02/26 21:46:31  guy
//Added Logging Features.
//
//Revision 1.1.2.2  2002/12/26 16:25:38  guy
//Improved pooling; added support for exclusive  connection (no reuse before 2PC)
//
//Revision 1.1.2.1  2002/12/18 17:43:13  guy
//Added exclusive (non-shared) pooled connections as a generic way
//to tackle integration with SQLServer7.0 or Oracle.
//Previously, this was restricted to Oracle only.
//

package com.atomikos.jdbc;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;

import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A non-shared implementation of a pooled connection. Some DBs such as Oracle
 * or SQLServer deviate from the standard in the moment where an XAResource can
 * be reused. This has to be AFTER 2PC (contrary to standard). Instances of this
 * class will generate TWO close events, and only the second will have
 * isDiscarded set to true.
 */

public class ExclusiveExternalXAPooledConnectionImp extends
        ExternalXAPooledConnectionImp implements Synchronization
{

    private boolean afterCompletionDone_ = false;

    public ExclusiveExternalXAPooledConnectionImp ( XAConnection c ,
            TransactionalResource res )
    {
        super ( c , res );
    }

    public ExclusiveExternalXAPooledConnectionImp ( XAConnection c ,
            TransactionalResource res , PrintWriter logWriter )
    {
        super ( c , res , logWriter );
    }

    public synchronized void setResourceTransaction ( ResourceTransaction restx )
            throws SQLException
    {
        // System.out.println ( "setResourceTransaction");
        super.setResourceTransaction ( restx );
        try {
            // add synchronization to current CT.
            // NOTE: since we are only reusable AFTER termination
            // of the CT, we can not add ourselves as synchronization
            // more than once -> GOOD, otherwise we would have
            // dangerous behaviour (multiple terminated notifications
            // would mess up reused connections!)
            // System.out.println ( "registering synchronization" );
            CompositeTransaction ct = Configuration
                    .getCompositeTransactionManager ()
                    .getCompositeTransaction ();
            ct.registerSynchronization ( this );
            afterCompletionDone_ = false;
            // added: needed for reuse of aborts (beforecompletion not called!)

        } catch ( Exception e ) {
        	AtomikosSQLException.throwAtomikosSQLException ( e.getMessage() , e );
        }
    }

    public void beforeCompletion ()
    {

        // reset from previous tx; for afterCompletion processing
        afterCompletionDone_ = false;
    }

    public void afterCompletion ( Object state )
    {
        // this method is called once for every
        // SQL method done in this transaction
        // so don't repeat this every time
        if ( afterCompletionDone_ )
            return;

        if ( state.equals ( TxState.TERMINATED )
                || state.equals ( TxState.HEUR_MIXED )
                || state.equals ( TxState.HEUR_HAZARD )
                || state.equals ( TxState.HEUR_ABORTED )
                || state.equals ( TxState.HEUR_COMMITTED ) ) {

            // connection is reusable!

            setDiscarded ();

            // next, notify listeners of REAL close event.
            ConnectionEvent e2 = new ConnectionEvent ( this );
            Enumeration enumm = listeners_.elements ();
            while ( enumm.hasMoreElements () ) {
                ConnectionEventListener l = (ConnectionEventListener) enumm
                        .nextElement ();
                l.connectionClosed ( e2 );
            }
            afterCompletionDone_ = true;
            

        }

        // System.out.println ( "afterCompletion ( " + state + " )" );

    }

    public void connectionClosed ( ConnectionEvent e )
    {

        // overridden from base class, to NOT set discarded

        ResourceTransaction restx = unsetResourceTransaction ();
        if ( restx != null )
        		suspendResourceTransaction ( restx );
        else {
            // if not in tx, then don't wait until tx finishes to reuse!
            setDiscarded ();
            ConnectionEvent e2 = new ConnectionEvent ( this );
            Enumeration enumm = listeners_.elements ();
            while ( enumm.hasMoreElements () ) {
                ConnectionEventListener l = (ConnectionEventListener) enumm
                        .nextElement ();
                l.connectionClosed ( e2 );
            }
        }
    }

}
