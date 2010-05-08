//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: JdbcTransactionalResource.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/15 10:31:32  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:23:06  guy
//Cleaned up code.
//
//Revision 1.1.1.1  2006/03/09 14:59:07  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2004/10/25 08:46:35  guy
//Removed old todos
//
//Revision 1.4  2004/10/12 13:04:56  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/10/11 13:40:14  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2004/10/02 12:55:27  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added user and passwd
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.1  2004/09/18 12:33:02  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Moved JdbcTransactionalResource to this package.
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.4  2004/08/30 07:21:43  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//*** empty log message ***
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.3  2004/03/22 15:39:16  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2.2.2  2004/03/16 14:28:07  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added tolerance for DB unavailability in recovery.
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2.2.1  2003/10/23 15:20:07  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added shutdown hook for closing the data source.
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added bean properties for JNDI/XA name configuration.
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2003/03/11 06:42:19  guy
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: JdbcTransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//Revision 1.1.2.1  2002/08/29 07:23:57  guy
//Added JdbcTransactionalResource for better XAResource setup and support.
//

package com.atomikos.datasource.xa.jdbc;

import java.sql.SQLException;
import java.util.Stack;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XidFactory;

/**
 * 
 * 
 * A default XATransactionalResource implementation for JDBC.
 */

public class JdbcTransactionalResource extends XATransactionalResource
{
    private XADataSource xads_;

    // where to get new connections

    private XAConnection conn_;

    // the last connection used.

    private String user_;

    // the user; empty string if not set

    private String password_;

    // the password, empty string if not set
    /**
     * Constructs a new instance with a given name and XADataSource.
     * 
     * @param serverName
     *            The unique name.
     * @param xads
     *            The data source.
     */

    public JdbcTransactionalResource ( String serverName , XADataSource xads )
    {
        super ( serverName );
        xads_ = xads;
        if ( xads_ == null )
            throw new RuntimeException ( "Null XADataSource argument" );
        conn_ = null;
    }

    /**
     * Constructs a new instance with a given name and XADataSource, and an Xid
     * factory to use. The custom Xid factory is needed for data servers that do
     * not accept arbitrary Xid formats.
     * 
     * @param serverName
     *            The unique name.
     * @param xads
     *            The data source.
     * @param factory
     *            The custom Xid factory.
     */

    public JdbcTransactionalResource ( String serverName , XADataSource xads ,
            XidFactory factory )
    {
        super ( serverName , factory );
        xads_ = xads;
        conn_ = null;
    }

    /**
     * Get the user
     * 
     * @return String The user, or empty string.
     */
    private String getUser ()
    {
        String ret = "";
        if ( user_ != null )
            ret = user_;

        return ret;
    }

    /**
     * Get the passwd
     * 
     * @return String the password, or empty string
     */
    private String getPassword ()
    {
        String ret = "";
        if ( password_ != null )
            ret = password_;
        return ret;
    }

    /**
     * Implements the functionality to get an XAResource handle.
     * 
     * @return XAResource The XAResource instance.
     */

    protected synchronized XAResource refreshXAConnection ()
            throws ResourceException
    {
        XAResource res = null;

        if ( conn_ != null ) {
            try {
                conn_.close ();
            } catch ( Exception err ) {
                // happens if connection has timed out
                // which is probably normal, otherwise
                // refresh would not be called in the first place
            }
        }

        try {
            conn_ = createXAConnection();
            if ( conn_ != null )
                res = conn_.getXAResource ();
            // null if db down during recovery
        } catch ( SQLException sql ) {
            Stack errors = new Stack ();
            errors.push ( sql );
            throw new ResourceException ( "Error in getting XA resource",
                    errors );
        }

        return res;

    }

    /**
     * Optionally set the user name with which to get connections for recovery.
     * 
     * If not set, then the right user name should be configured on the
     * XADataSource directly.
     * 
     * @param user
     *            The user name.
     */

    public void setUser ( String user )
    {
        user_ = user;
    }

    /**
     * Optionally set the password with which to get connections for recovery.
     * 
     * If not set, then the right password should be configured on the
     * XADataSource directly.
     * 
     * @param password
     *            The password.
     */

    public void setPassword ( String password )
    {
        password_ = password;
    }

    /**
     * Overrides default close to include closing any open connections to the
     * XADataSource.
     */

    public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( conn_ != null )
                conn_.close ();
        } catch ( SQLException err ) {
            // throw new ResourceException ( err.getMessage() );
            // exception REMOVED because it close clashes
            // with shutdown hooks (in which the order of TS and
            // DS shutdown is unpredictable)
        }
    }
    
    private XAConnection createXAConnection()
    {
    		XAConnection conn = null;
    		try {
            if ( "".equals ( getUser () ) )
                conn = xads_.getXAConnection ();
            else
                conn = xads_.getXAConnection ( getUser (), getPassword () );
        } catch ( SQLException noConnection ) {
            // ignore and return null: happens if
            // db is down at this time (during
            // recovery for instance)
            conn = null;
        }
        return conn;
    }



}
