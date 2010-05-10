package com.atomikos.jdbc.nonxa;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.atomikos.jdbc.ConnectionFactory;
import com.atomikos.jdbc.XPooledConnection;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class NonXAConnectionFactory implements ConnectionFactory
{

    private String userName;

    private String password;

    private DataSource dataSource;

    /**
     * Create a new instance.
     * 
     * @param dataSource
     *            The underlying (non-XA) datasource to use. For instance, a
     *            MySQL datasource.
     * @param userName
     *            The user name, or empty string if not applicable.
     * @param password
     *            The password for the given user, or empty if the user is
     *            empty.
     */

    public NonXAConnectionFactory ( DataSource dataSource , String userName ,
            String password )
    {
        this.userName = userName;
        this.password = password;
        this.dataSource = dataSource;
    }

    protected String getUserName ()
    {
        return userName;
    }

    protected String getPassword ()
    {
        return password;
    }

    protected DataSource getDataSource ()
    {
        return dataSource;
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getPooledConnection()
     */

    public XPooledConnection getPooledConnection () throws SQLException
    {
        NonXAPooledConnectionImp ret = null;
        Connection c = null;

        if ( getUserName () == null || getUserName ().equals ( "" ) )
            c = getDataSource ().getConnection ();
        else
            c = getDataSource ()
                    .getConnection ( getUserName (), getPassword () );
        ret = new NonXAPooledConnectionImp ( c );
        return ret;
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter () throws SQLException
    {
        return getDataSource ().getLogWriter ();
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        getDataSource ().setLogWriter ( pw );

    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getLoginTimeout()
     */
    public int getLoginTimeout () throws SQLException
    {
        return getDataSource ().getLoginTimeout ();
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#setLoginTimeout(int)
     */
    public void setLoginTimeout ( int secs ) throws SQLException
    {
        getDataSource ().setLoginTimeout ( secs );

    }

}
