//$Id: JtaDataSourceImp.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: JtaDataSourceImp.java,v $
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
//Revision 1.12  2005/08/09 15:25:06  guy
//Updated javadoc.
//
//Revision 1.11  2004/11/15 08:38:23  guy
//Updated to correct bug: aborted connections were not reused.
//
//Revision 1.10  2004/10/13 14:15:53  guy
//Updated javadocs.
//
//Revision 1.9  2004/10/11 15:44:13  guy
//Improved getReference.
//
//Revision 1.8  2004/10/11 13:39:55  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.7  2004/10/08 07:11:43  guy
//Improved automatic registration for recovery.
//Added methods to HeuristicDataSource.
//Improved user/paswwd handling in XAConnectionFactory.
//
//Revision 1.6  2004/09/30 09:56:18  guy
//Added support for external pools.
//Implemented support for late enlistment (start of tx after getConnection()).
//
//Revision 1.5  2004/09/28 11:36:13  guy
//Moved addResource to SimpleDataSourceBean to maintain backward compatibility.
//
//Revision 1.4  2004/09/28 11:27:40  guy
//Added classes for Websphere integration.
//
//Revision 1.3  2004/03/22 15:39:16  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.2.6  2004/02/17 13:01:37  guy
//Added support for validation: in that case, no unique instance per name and per VM should be enforced.
//
//Revision 1.2.2.5  2003/10/02 06:32:02  guy
//*** empty log message ***
//
//Revision 1.2.2.4  2003/08/24 07:04:12  guy
//*** empty log message ***
//
//Revision 1.2.2.3  2003/06/20 16:31:59  guy
//*** empty log message ***
//
//Revision 1.2.2.2  2003/05/30 15:19:27  guy
//Added getTransactionalResource method, needed during JNDI setup.
//
//Revision 1.2.2.1  2003/05/18 09:43:15  guy
//Made xid factory a list property, and added an editor for this.
//
//$Id: JtaDataSourceImp.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:42:19  guy
//$Id: JtaDataSourceImp.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: JtaDataSourceImp.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.4.4  2002/12/30 07:13:41  guy
//Added JNDI capabilities to JtaDataSourceImp.
//
//Revision 1.1.4.3  2002/12/26 16:25:38  guy
//Improved pooling; added support for exclusive  connection (no reuse before 2PC)
//
//Revision 1.1.4.2  2002/09/23 08:52:47  guy
//Added getConnection with user and password, for JDBC consistency.
//
//Revision 1.1.4.1  2002/09/10 17:23:51  guy
//Made XAConnectionFactory not abstract, and generic for all databases.
//
//Revision 1.1  2002/03/19 15:45:50  guy
//Added Heuristic support to JTA interfaces.
//Cleaned up ConnectionFactory.
//

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
