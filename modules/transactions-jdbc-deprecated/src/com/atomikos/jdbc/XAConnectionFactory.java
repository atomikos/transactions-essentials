//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: XAConnectionFactory.java,v $
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
//Revision 1.9  2004/10/11 13:39:55  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.8  2004/10/08 07:11:43  guy
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Improved automatic registration for recovery.
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added methods to HeuristicDataSource.
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Improved user/paswwd handling in XAConnectionFactory.
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.7  2004/10/02 12:55:18  guy
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added user and passwd to JdbcTransactionalResource
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.6  2004/09/30 09:56:18  guy
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added support for external pools.
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Implemented support for late enlistment (start of tx after getConnection()).
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.5  2004/09/18 12:32:44  guy
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Moved to new package.
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.4  2003/03/11 06:42:19  guy
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: XAConnectionFactory.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.3.4.4  2003/02/26 21:46:31  guy
//Added Logging Features.
//
//Revision 1.3.4.3  2002/12/22 17:22:30  guy
//Set exclusive mode to false by default.
//
//Revision 1.3.4.2  2002/12/18 17:43:14  guy
//Added exclusive (non-shared) pooled connections as a generic way
//to tackle integration with SQLServer7.0 or Oracle.
//Previously, this was restricted to Oracle only.
//
//Revision 1.3.4.1  2002/09/10 17:23:51  guy
//Made XAConnectionFactory not abstract, and generic for all databases.
//
//Revision 1.3  2002/03/19 15:45:51  guy
//Added Heuristic support to JTA interfaces.
//Cleaned up ConnectionFactory.
//
//Revision 1.2  2002/03/19 14:08:00  guy
//Conceptual cleanup, and addition of HeuristicDataSource.
//
//Revision 1.1.1.1  2001/10/05 13:19:53  guy
//Jdbc module
//

package com.atomikos.jdbc;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;

/**
 * 
 * 
 * A default connection factory for XA connections that work with the Atomikos
 * connection pools.
 */

// @todo TEST NEW USER AND PASSWD SETS ON TXRESOURCE!!!
public class XAConnectionFactory implements ConnectionFactory
{
    private String userName_;

    private String passwd_;

    private String resourceName_;

    private XADataSource ds_;

    private JdbcTransactionalResource res_;

    private boolean exclusive_;

    // true iff XAResource is Oracle or SQLServer style
    // where 2PC must be on same instance as start&end

    /**
     * Constructor for subclasses with their own transactional resource.
     */

    protected XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource ,
            JdbcTransactionalResource res )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = res;

        // ONLY set the user and password if it is not
        // empty; in order to avoid that SimpleDataSourceBeans
        // will override user settings of XADataSource!!!
        // BUG IN INITIAL 2.0 BETA
        if ( userName != null && !userName.equals ( "" ) ) {
            res_.setUser ( userName );
            res_.setPassword ( passwd );
        }

        exclusive_ = false;
    }

    /**
     * Creates a new instance with a given resource name, user name and password
     * for the XADataSource.
     * 
     * @param resourceName
     *            The unique resource name for the corresponding
     *            JdbcTransactionalResource (which will be created
     *            automatically).
     * @param userName
     *            The user name to use for getting XA connections. Null if no
     *            authentication is supported by the supplied XADataSource.
     * @param passwd
     *            The password for the user, or null if no authentication is
     *            supported by the supplied XADataSource.
     * @param xaDataSource
     *            The XADataSource to use.
     */

    public XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = new JdbcTransactionalResource ( resourceName, ds_ );
        res_.setUser ( userName );
        res_.setPassword ( passwd );
        exclusive_ = false;
    }

    /**
     * Creates a new instance with a given resource name, user name and password
     * for the XADataSource, and a custom XidFactory.
     * 
     * @param resourceName
     *            The unique resource name for the corresponding
     *            JdbcTransactionalResource (which will be created
     *            automatically).
     * @param userName
     *            The user name to use for getting XA connections. Null if no
     *            authentication is supported by the supplied XADataSource.
     * @param passwd
     *            The password for the user, or null if no authentication is
     *            supported by the supplied XADataSource.
     * @param xaDataSource
     *            The XADataSource to use.
     * @param xidFactory
     *            The custom XidFactory.
     */

    public XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource , XidFactory xidFactory )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = new JdbcTransactionalResource ( resourceName, ds_, xidFactory );
        res_.setUser ( userName );
        res_.setPassword ( passwd );
        exclusive_ = false;
    }

    /**
     * Used by getPooledConnection() to get a connection. If the drivers do not
     * support authentication, then an empty string should be returned here.
     * 
     * @return String The user name, or null if not applicable.
     */

    protected String getUserName ()
    {
        return userName_;
    }

    /**
     * Used by getPooledConnection() to get a connection.
     * 
     * 
     * @return String The password, or null if not applicable.
     */

    protected String getPassword ()
    {
        return passwd_;
    }

    /**
     * This method gets the underlying data source. It is called by
     * getPooledConnection() to get connections.
     * 
     * @return XADataSource The data source to get XAConnections from.
     */

    public XADataSource getXADataSource ()
    {
        return ds_;
    }

    /**
     * Sets the connections generated to exclusive for 2PC. This mode is needed
     * for certain databases that do not conform to XA entirely.
     * 
     * @param exclusive
     *            If true, then connections will be kept until after
     *            commit/rollback, resulting in lower reuse.
     */

    public void setExclusive ( boolean exclusive )
    {
        exclusive_ = exclusive;
    }

    /**
     * Tests if the connections that are generated will be exclusive or not.
     * 
     * @return boolean True iff connections are exclusive.
     */

    public boolean isExclusive ()
    {
        return exclusive_;
    }

    /**
     * Get the transactional resource.
     * 
     * @return TransactionalResource The transactional resource.
     */

    public TransactionalResource getTransactionalResource ()
    {
        return res_;
    }

    /**
     * The main method: gets a new DTPPooledConnection instance.
     * 
     * @return XPooledConnection An instance of DTPPooledConnection for use with
     *         DTPConnectionPool.
     * 
     * @exception IllegalStateException
     *                If not initialized.
     * @exception SQLException
     *                On sql errors.
     */

    public XPooledConnection getPooledConnection ()
            throws IllegalStateException, SQLException
    {
        XAConnection conn = null;
        XPooledConnection ret = null;

        if ( getTransactionalResource () == null )
            throw new IllegalStateException (
                    "XAConnectionFactory: no tx resource" );
        if ( getUserName () == null || getUserName ().equals ( "" ) ) {
            conn = getXADataSource ().getXAConnection ();
        } else {
            conn = getXADataSource ().getXAConnection ( getUserName (),
                    getPassword () );
        }

        if ( !exclusive_ ) {
            ret = new ExternalXAPooledConnectionImp ( conn,
                    getTransactionalResource (), getLogWriter () );
        } else {
            ret = new ExclusiveExternalXAPooledConnectionImp ( conn,
                    getTransactionalResource (), getLogWriter () );
        }

        return ret;
    }

    public PrintWriter getLogWriter () throws SQLException
    {
        return getXADataSource ().getLogWriter ();
    }

    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        getXADataSource ().setLogWriter ( pw );
    }

    public int getLoginTimeout () throws SQLException
    {
        return getXADataSource ().getLoginTimeout ();
    }

    public void setLoginTimeout ( int secs ) throws SQLException
    {
        getXADataSource ().setLoginTimeout ( secs );
    }
}
