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
