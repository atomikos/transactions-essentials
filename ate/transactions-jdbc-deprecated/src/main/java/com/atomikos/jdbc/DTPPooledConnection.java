//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: DTPPooledConnection.java,v $
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
//Revision 1.7  2004/11/13 10:23:53  guy
//Debugged.
//
//Revision 1.6  2004/10/25 08:46:21  guy
//Removed old todos
//
//Revision 1.5  2004/10/12 13:04:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.4  2004/10/01 08:56:44  guy

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//DebuggedDebugged

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.3  2004/09/30 09:56:18  guy

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added support for external pools.

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Implemented support for late enlistment (start of tx after getConnection()).

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:42:18  guy

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.&

//$Id: DTPPooledConnection.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.1.1.4.1  2002/12/26 16:25:38  guy
//Improved pooling; added support for exclusive  connection (no reuse before 2PC)
//
//Revision 1.1.1.1  2001/10/05 13:19:53  guy
//Jdbc module
//

package com.atomikos.jdbc;

import java.sql.SQLException;

import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A pooled connection that is distributed transaction aware. Instances are
 * needed for DTPConnectionPool pools. Note: clients should be aware that some
 * instances of this interface generate TWO related close events: the first when
 * the application closes the SQL connection, and the second after two-phase
 * commit is done. In any case, the connection can NOT be reused <b>before</b>
 * isDiscarded returns true.
 */

public interface DTPPooledConnection extends XPooledConnection
{

    /**
     * Get a connection with a given heuristic message.
     * 
     * @param msg
     *            The message to include in the tx logs.
     * @return Connection The connection to use.
     * @throws SQLException
     */
    public java.sql.Connection getConnection ( HeuristicMessage msg )
            throws SQLException;

    /**
     * Get the transactional resource for this connection.
     * 
     * @return TransactionalResource The resource to which this connection
     *         belongs.
     */

    public TransactionalResource getTransactionalResource ();

    /**
     * Unsets the resource transaction property for this connection. Called to
     * dissociate the connection with a resourcetransaction.
     * 
     * @return ResourceTransaction The resource transaction associated with the
     *         connection, or null if none.
     */

    public ResourceTransaction unsetResourceTransaction ();

    /**
     * Associate a resource transaction with this connection. After returning,
     * the resource transaction should be ready for resume() calls.
     * 
     * @param restx
     *            The resource transaction to be associated with this
     *            connection.
     * @exception SQLException
     *                If a SQL error occurs.
     */

    public void setResourceTransaction ( ResourceTransaction restx )
            throws java.sql.SQLException;

    /**
     * Tests if the connection can be put back into the pool. Some
     * implementations of reusable connections may not be fully compliant, in
     * that an application-level close() does not automatically imply that the
     * connection can be reused. This function tests if the connection can be
     * reused.
     * 
     * @return boolean True iff the connection will NOT be used again by the
     *         current client. As a consequence, it can be put back into the
     *         pool or replaced, whichever the pool instance prefers.
     */

    public boolean isDiscarded ();

    /**
     * Test if the connection is currently associated with a resource
     * transaction.
     * 
     * @return boolean True iff in a transaction.
     */
    public boolean isInResourceTransaction ();
}
