package com.atomikos.jdbc.nonxa;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.jdbc.ConnectionPool;
import com.atomikos.jdbc.HeuristicDataSource;
import com.atomikos.jdbc.XPooledConnection;

/**
 * 
 * 
 * 
 * A DataSource implementation that is capable of handling 2-phase commit and
 * transactions without requiring XA support from the JDBC drivers of the
 * database. Instances can be used to involve non-XA databases in a transaction,
 * at the risk of not being able to recover prepared transactions (recovery of
 * prepared transactions will result in heuristics).
 * 
 *  @deprecated As of release 3.3, the {@link AtomikosNonXADataSourceBean} should be used instead.
 * 
 */

public class NonXADataSourceImp implements HeuristicDataSource,
        ConnectionEventListener, Referenceable
{

    private Map previousConnections;
    // for each thread, the previously used connection if any

    private ConnectionPool pool;

    private String jndiName;

    private static Map nameToDataSource_ = new HashMap ();

    // maps names to DS instances for JNDI lookup helping

    /**
     * Helper method for JNDI lookup; helps looking up a previously constructed
     * instance.
     * 
     * @param name
     *            The name of the resource.
     * @return NonXADataSourceImp The data source, or null if not found.
     */

    static NonXADataSourceImp getInstance ( String name )
    {
        NonXADataSourceImp ret = (NonXADataSourceImp) nameToDataSource_
                .get ( name );
        return ret;
    }

    /**
     * Add an instance to the map, so that it can be found by name.
     * 
     * @param name
     *            The name to map on.
     * @param instance
     *            The data source.
     * @exception SQLException
     *                If the name is already in use.
     */

    private synchronized static void addToMap ( String name ,
            NonXADataSourceImp instance ) throws SQLException
    {
        if ( nameToDataSource_.get ( name ) != null ) {
            throw new SQLException ( "DataSource for resource " + name
                    + " already exists!" );
        }

        nameToDataSource_.put ( name, instance );

    }

    /**
     * Remove a map entry for the given name.
     * 
     * @param name
     *            The name to unmap.
     */

    private synchronized static void removeFromMap ( String name )
    {
    		//test null: quick fix for 20718 (Tomcat classloader problem)
    		if ( name != null && nameToDataSource_ != null ) 
    			nameToDataSource_.remove ( name );
    }

    /**
     * Create a new instance.
     * 
     * @param driver
     *            The original datasource driver to use. This can be a MySQL,
     *            for instance. Note: don't use a pooling driver!
     * @param jndiName
     *            The unique global JNDI name to bind this instance on.
     * @param user
     *            The username to get connections for. Empty string or null if
     *            no user authentication should be used.
     * @param password
     *            The password, null or empty if no user authentication should
     *            be used.
     * @param poolSize
     *            The size of the connection pool.
     * @param connectionTimeout
     *            The number of seconds after which the pool's connections are
     *            checked periodically.
     * @param testQuery 
     *        	 A SQL test query to validate connection liveness.
     * @param testOnBorrow
     * 			Should connections be tested when gotten?
     * 
     * @throws SQLException
     */

    public NonXADataSourceImp ( DataSource driver , String jndiName ,
            String user , String password , int poolSize , int connectionTimeout, String testQuery , boolean testOnBorrow )
            throws SQLException
    {
        this ( driver , jndiName , user , password , poolSize ,
                connectionTimeout , false, testQuery , testOnBorrow );
    }

    /**
     * Create a new instance.
     * 
     * @param driver
     *            The original datasource driver to use. This can be a MySQL,
     *            for instance. Note: don't use a pooling driver!
     * @param jndiName
     *            The unique global JNDI name to bind this instance on.
     * @param user
     *            The username to get connections for. Empty string or null if
     *            no user authentication should be used.
     * @param password
     *            The password, null or empty if no user authentication should
     *            be used.
     * @param poolSize
     *            The size of the connection pool.
     * @param connectionTimeout
     *            The number of seconds after which the pool's connections are
     *            checked periodically.
     * @param validation
     *            True if the instance will be used for validation only. No
     *            binding is done in that case.
     * @param testQuery 
     * 			 A query to validate connection liveness.
     * @param testOnBorrow
     * 			 Should connections be tested when gotten?
     * 
     * @throws SQLException
     */

    public NonXADataSourceImp ( DataSource driver , String jndiName ,
            String user , String password , int poolSize ,
            int connectionTimeout , boolean validation, String testQuery , boolean testOnBorrow ) throws SQLException
    {
        NonXAConnectionFactory factory = new NonXAConnectionFactory ( driver,
                user, password );
        pool = new ConnectionPool ( poolSize, factory, connectionTimeout, testQuery , testOnBorrow );
        previousConnections = new HashMap ();
        this.jndiName = jndiName;
        if ( !validation )
            addToMap ( jndiName, this );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String)
     */
    public Connection getConnection ( String msg ) throws SQLException
    {
        StringHeuristicMessage hm = new StringHeuristicMessage ( msg );
        return getConnection ( hm );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        StringHeuristicMessage hm = new StringHeuristicMessage ( msg );
        return getConnection ( user, passwd, hm );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(com.atomikos.icatch.HeuristicMessage)
     */

    public synchronized Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {

        // ThreadLocalConnection ret = ( ThreadLocalConnection )
        // previousConnections.get();
        Connection proxy = (Connection) previousConnections.get ( Thread
                .currentThread () );
        if ( proxy == null ) {
            // no previous connection -> get one from pool
            NonXAPooledConnectionImp pc = (NonXAPooledConnectionImp) pool
                    .getPooledConnection ();
            pc.addConnectionEventListener ( this );
            proxy = (Connection) ThreadLocalConnection.newInstance ( pc );
            previousConnections.put ( Thread.currentThread (), proxy );
        }

        // here we are certain that proxy is not null -> increase the use count
        ThreadLocalConnection previous = (ThreadLocalConnection) Proxy
                .getInvocationHandler ( proxy );
        // if ( previous.isStale() ) System.out.println ( "WARNING: STALE
        // CONNECTION STILL MAPPED TO THREAD");
        previous.incUseCount ();
        previous.addHeuristicMessage ( msg );

        return proxy;
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, com.atomikos.icatch.HeuristicMessage)
     */

    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException
    {
        throw new SQLException (
                "Not supported: getConnection with authentication." );
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */

    public Connection getConnection () throws SQLException
    {
        HeuristicMessage m = null;
        return getConnection ( m );
    }

    public Connection getConnection ( String user , String pw )
            throws SQLException
    {
        HeuristicMessage m = null;
        return getConnection ( user, pw, m );
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter () throws SQLException
    {
        return pool.getLogWriter ();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        pool.setLogWriter ( pw );

    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout ( int val ) throws SQLException
    {
        pool.setLoginTimeout ( val );

    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout () throws SQLException
    {
        return pool.getLoginTimeout ();
    }

    /**
     * @see javax.sql.ConnectionEventListener#connectionClosed(javax.sql.ConnectionEvent)
     */
    public synchronized void connectionClosed ( ConnectionEvent event )
    {
        // called when the Pooled Connection is resubmitted to the pool
        // remove ThreadLocal mapping
        XPooledConnection source = (XPooledConnection) event.getSource ();
        Set values = previousConnections.entrySet ();
        Iterator it = values.iterator ();
        while ( it.hasNext () ) {
            Map.Entry entry = (Map.Entry) it.next ();
            Connection c = (Connection) entry.getValue ();
            ThreadLocalConnection tlc = (ThreadLocalConnection) Proxy
                    .getInvocationHandler ( c );
            if ( tlc.usesConnection ( source ) ) {
                it.remove ();

            }
        }
        source.removeConnectionEventListener ( this );
        pool.putBack ( source );
    }

    /**
     * @see javax.sql.ConnectionEventListener#connectionErrorOccurred(javax.sql.ConnectionEvent)
     */
    public void connectionErrorOccurred ( ConnectionEvent arg0 )
    {
        // ignore

    }

    /**
     * Closes the datasource (and shuts down the pool).
     * 
     */

    public void close ()
    {
        pool.cleanup ();
        removeFromMap ( jndiName );
    }

    /**
     * @see Referenceable
     */

    public Reference getReference () throws NamingException
    {
        RefAddr ra = new StringRefAddr ( "ResourceName", jndiName );
        Reference ref = new Reference ( getClass ().getName (),
                new StringRefAddr ( "name", "NonXADataSourceImp" ),
                NonXADataSourceImpFactory.class.getName (), null );
        ref.add ( ra );
        return ref;

    }

}
