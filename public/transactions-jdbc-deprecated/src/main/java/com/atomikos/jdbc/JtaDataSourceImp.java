/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * A basic datasource implementation that works with Atomikos connection pools and
 * supports heuristic messages. Instances are referenceable in JNDI, but not Serializable.
 * <p>
 * <b>Note: instead of using this class directly, it is highly recommended that you use
 * one of the other Atomikos DataSourceBean implementations instead.</b>
 *
 * @deprecated As of release 3.3, the {@link AtomikosDataSourceBean} should be used instead.
 *
 */

public class JtaDataSourceImp implements HeuristicDataSource,
        ConnectionEventListener, Referenceable, ConnectionPoolDataSource
{

    private static Map nameToDataSource_ = new HashMap ();

    // maps names to DS instances for JNDI lookup helping

    /**
     * Create a JNDI reference for the given resource.
     *
     */

    static Reference createReference ( String uniqueName )
    {
        RefAddr ra = new StringRefAddr ( "ResourceName", uniqueName );
        Class clazz = JtaDataSourceImp.class;
        Reference ref = new Reference ( clazz.getName (), new StringRefAddr (
                "name", "JtaDataSourceImp" ), JtaDataSourceImpFactory.class
                .getName (), null );
        ref.add ( ra );
        return ref;
    }

    /**
     * Helper method for JNDI lookup; helps looking up a previously constructed
     * instance.
     *
     * @param name
     *            The name of the resource.
     * @return JtaDataSourceImp The data source, or null if not found.
     */

    static JtaDataSourceImp getInstance ( String name )
    {
        JtaDataSourceImp ret = (JtaDataSourceImp) nameToDataSource_.get ( name );
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
            JtaDataSourceImp instance ) throws SQLException
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
    		//test null: quick fix for 20718
    		if ( name!= null && nameToDataSource_ != null )
    			nameToDataSource_.remove ( name );

    }

    //
    // INSTANCE VARIABLES
    //

    private ConnectionPool pool_;

    private TransactionalResource resource_;

    private XAConnectionFactory fact_;

    private boolean autoRegister_;

    /**
     * Creates a new instance.
     *
     * @param fact
     *            The XAConnectionFactory to use.
     * @param poolSize
     *            The size of the pool.
     * @param connectionTimeout
     *            The timeout in seconds for each connection.
     * @param testQuery
     * 			 A test query to validate connection liveness.
     * @param testOnBorrow
     * 			 If true then connections will be tested when gotten from the pool.
     * @exception SQLException
     *                On error.
     */

    public JtaDataSourceImp ( XAConnectionFactory fact , int poolSize ,
            int connectionTimeout , String testQuery , boolean testOnBorrow ) throws SQLException
    {
        this ( fact , poolSize , connectionTimeout , false ,
                false, testQuery , testOnBorrow );
    }

    /**
     * Constructor for validation of DataSourceBean instances.
     *
     * @param fact
     * @param poolSize
     * @param connectionTimeout
     * @param allowDuplicateNames
     *            If true then the instance not added for JNDI retrieval.
     * @param registerWithTM
     *            If true, then the resource is added to the configuration for
     *            recovery.
     * @param testOnBorrow
     * 			 If true then connections will be tested when gotten from the pool.
     * @throws SQLException
     */

    JtaDataSourceImp ( XAConnectionFactory fact , int poolSize ,
            int connectionTimeout ,
            boolean allowDuplicateNames , boolean registerWithTM, String testQuery , boolean testOnBorrow )
            throws SQLException
    {
        fact_ = fact;
        pool_ = new ConnectionPool ( poolSize, fact, connectionTimeout,
                testQuery , testOnBorrow );
        // pool_.addConnectionPoolListener ( new JtaConnectionPoolListener() );
        resource_ = fact.getTransactionalResource ();
        autoRegister_ = registerWithTM;
        if ( !allowDuplicateNames )
            addToMap ( resource_.getName (), this );
        if ( registerWithTM ) {
            Configuration.addResource ( resource_ );
        }
    }

    /**
     * @see javax.sql.DataSource
     */

    public Connection getConnection () throws SQLException
    {
        HeuristicMessage msg = null;
        return getConnection ( msg );
    }

    /**
     * Not implemented in this release; throws SQLException.
     */

    public Connection getConnection ( String username , String passwd )
            throws SQLException
    {
        // @todo add support for explicit user name and password!!!
        throw new SQLException (
                "Not supported: getConnection ( user , passwd )" );
    }

    /**
     * Not implemented in this release; throws SQLException.
     */

    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException
    {
        throw new SQLException (
                "Not supported: getConnection ( user , passwd )" );
    }


    /**
     * @see javax.sql.DataSource
     */

    public PrintWriter getLogWriter () throws SQLException
    {
        return pool_.getLogWriter ();
    }

    /**
     * @see javax.sql.DataSource
     */

    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        pool_.setLogWriter ( pw );
    }

    /**
     * @see javax.sql.DataSource
     */

    public void setLoginTimeout ( int seconds ) throws SQLException
    {
        pool_.setLoginTimeout ( seconds );
    }

    /**
     * @see javax.sql.DataSource
     */

    public int getLoginTimeout () throws SQLException
    {
        return pool_.getLoginTimeout ();
    }

    /**
     * Cleans up the instance, and closes the pool.
     */

    public void close ()
    {
        pool_.cleanup ();
        removeFromMap ( resource_.getName () );
        if ( autoRegister_ )
            Configuration.removeResource ( resource_.getName () );
    }

    /**
     * Get the underlying transactional resource.
     *
     * @return TransactionalResource The resource.
     */
    public TransactionalResource getTransactionalResource ()
    {
        return resource_;
    }

    /**
     * @see Referenceable
     */

    public Reference getReference () throws NamingException
    {

        return createReference ( resource_.getName () );

    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(com.atomikos.icatch.HeuristicMessage)
     */
    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        DTPPooledConnection ret = (DTPPooledConnection) pool_
                .getPooledConnection ();
        ret.addConnectionEventListener ( this );
        return ret.getConnection ( msg );

    }

    /**
     * @see javax.sql.ConnectionEventListener#connectionClosed(javax.sql.ConnectionEvent)
     */
    public void connectionClosed ( ConnectionEvent arg )
    {
        DTPPooledConnection pc = (DTPPooledConnection) arg.getSource ();
        if ( pc.isDiscarded () ) {
            pc.removeConnectionEventListener ( this );
            pool_.putBack ( pc );
        }
    }

    /**
     * @see javax.sql.ConnectionEventListener#connectionErrorOccurred(javax.sql.ConnectionEvent)
     */
    public void connectionErrorOccurred ( ConnectionEvent arg )
    {
        DTPPooledConnection pc = (DTPPooledConnection) arg.getSource ();
        pc.removeConnectionEventListener ( this );
        pc.setInvalidated ();
    }

    /**
     * @see javax.sql.ConnectionPoolDataSource#getPooledConnection()
     */
    public PooledConnection getPooledConnection () throws SQLException
    {
        // This is meant for external third party pools, so
        // don't go through the pool: we don't want to use our
        // pooling and risk that there are two pools managing
        // the same connection
        return fact_.getPooledConnection ();
    }

    /**
     * @see javax.sql.ConnectionPoolDataSource#getPooledConnection(java.lang.String,
     *      java.lang.String)
     */
    public PooledConnection getPooledConnection ( String user , String pw )
            throws SQLException
    {
        throw new SQLException ( "Not supported." );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String)
     */
    public Connection getConnection ( String msg ) throws SQLException
    {
        HeuristicMessage hm = new StringHeuristicMessage ( msg );
        return getConnection ( hm );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        HeuristicMessage hm = new StringHeuristicMessage ( msg );
        return getConnection ( user, passwd, hm );
    }


}
