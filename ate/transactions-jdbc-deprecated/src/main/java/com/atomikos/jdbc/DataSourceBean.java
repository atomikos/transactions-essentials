//$Id: DataSourceBean.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: DataSourceBean.java,v $
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
//Revision 1.10  2005/08/09 15:25:06  guy
//Updated javadoc.
//
//Revision 1.9  2004/10/25 08:46:21  guy
//Removed old todos
//
//Revision 1.8  2004/10/13 14:15:53  guy
//Updated javadocs.
//
//Revision 1.7  2004/10/11 15:44:13  guy
//Improved getReference.
//
//Revision 1.6  2004/10/11 13:39:55  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.5  2004/10/08 07:11:43  guy
//Improved automatic registration for recovery.
//Added methods to HeuristicDataSource.
//Improved user/paswwd handling in XAConnectionFactory.
//
//Revision 1.4  2004/09/28 11:27:40  guy
//Added classes for Websphere integration.
//
//Revision 1.3  2004/06/25 11:47:47  guy
//*** empty log message ***
//
//Revision 1.2  2004/03/22 15:39:16  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.12  2004/02/18 21:50:05  guy
//Added close() method.
//
//Revision 1.1.2.11  2004/02/17 15:55:49  guy
//Changed validate: should get connection from wrapped DataSource
//to avoid losing validation behaviour.
//
//Revision 1.1.2.10  2004/02/17 13:01:37  guy
//Added support for validation: in that case, no unique instance per name and per VM should be enforced.
//
//Revision 1.1.2.9  2004/02/16 09:37:16  guy
//Added a validating query facility.
//
//Revision 1.1.2.8  2004/02/13 17:56:21  guy
//Added some debugging code.
//
//Revision 1.1.2.7  2003/11/16 09:03:35  guy
//Updated name of XA DataSource property to follow JDBC conventions.
//
//Revision 1.1.2.6  2003/10/23 15:20:07  guy
//Added shutdown hook for closing the data source.
//Added bean properties for JNDI/XA name configuration.
//
//Revision 1.1.2.5  2003/08/21 20:31:51  guy
//*** empty log message ***
//
//Revision 1.1.2.4  2003/06/20 16:31:59  guy
//*** empty log message ***
//
//Revision 1.1.2.3  2003/05/30 15:19:27  guy
//Added getTransactionalResource method, needed during JNDI setup.
//
//Revision 1.1.2.2  2003/05/18 09:43:15  guy
//Made xid factory a list property, and added an editor for this.
//
//Revision 1.1.2.1  2003/05/15 15:26:46  guy
//Added JavaBean compliant data source for GUI setup.
//

package com.atomikos.jdbc;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.XADataSource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * @deprecated As of release 3.3, the {@link AtomikosDataSourceBean} should be used instead.
 * 
 */

public class DataSourceBean implements HeuristicDataSource, Serializable,
        Referenceable
{

    private transient JtaDataSourceImp ds_;
    // the data source to delegate to

    private XADataSource xads_;
    // the hook to the JDBC database, needed to
    // reconstruct the transient datasource

    private String xadsJndiName_;
    // optionally allow the xads to be specified through the
    // JNDI name on which it is intalled

    private String resourceName_;
    // the unique name for the resource associated with us

    private String xidFactory_;
    // the name of the XID factory, needed to reconstruct ds_

    private int poolSize_;
    // the pool size for the ds_

    private int connectionTimeout_;
    // needed for the pool

    private boolean exclusive_;
    // true if not reusable before 2PC

    private String validatingQuery_;
    // an optional query for validating all settings
    
    private boolean testOnBorrow_;
    //should connections be tested when gotten?

    public DataSourceBean ()
    {
        ds_ = null;
        xads_ = null;
        resourceName_ = "someUniqueName";
        xidFactory_ = "Default";
        poolSize_ = 2;
        connectionTimeout_ = 30;
        exclusive_ = true;
        xadsJndiName_ = "";
        validatingQuery_ = "";
        testOnBorrow_ = false;
    }

    private synchronized void checkSetup ( boolean validation )
            throws SQLException
    {
        // try if there has been a previous bean setup; return the DS if so
        ds_ = JtaDataSourceImp.getInstance ( getUniqueResourceName () );
        // return if found, but ONLY if validation is NOT true
        // since validation requires asserting XA settings and creation
        if ( ds_ != null && !validation )
            return;

        if ( xads_ == null && getDataSourceName ().equals ( "" ) )
            throw new SQLException ( "DataSourceBean: xaDataSource not set." );
        if ( xidFactory_ == null )
            throw new SQLException ( "DataSourceBean: xidFormat not set." );

        if ( !validation && !getDataSourceName ().equals ( "" ) ) {
            // XADataSource is specified through its JNDI name -> look it up
            try {
                Context ctx = new InitialContext ();
                Context envCtx = (Context) ctx.lookup ( "java:comp/env" );
                xads_ = (XADataSource) envCtx.lookup ( getDataSourceName () );
            } catch ( Exception e ) {
                // e.printStackTrace();
                throw new SQLException ( "DataSourceBean: setup error: "
                        + e.getClass ().getName () + " " + e.getMessage () );
            }
        }

        XidFactory xidFactory = null;

        xidFactory = new DefaultXidFactory ();

        XAConnectionFactory factory = new XAConnectionFactory ( resourceName_,
                "", "", xads_, xidFactory );
        factory.setExclusive ( exclusive_ );
        ds_ = new JtaDataSourceImp ( factory, poolSize_, connectionTimeout_,
                validation, !validation, validatingQuery_ , testOnBorrow_ );

        // the application does not know the Jta datasource and hence can not
        // shut it down. therefore, add a shutdown hook to do this job
        DataSourceShutdownHook hook = new DataSourceShutdownHook ( ds_ );
        Configuration.addShutdownHook ( hook );
    }

    private synchronized void checkSetup () throws SQLException
    {
        checkSetup ( false );
    }
    
    public void setTestOnBorrow ( String value )
    {
    		testOnBorrow_ = "true".equals ( value );
    }
    
    public String getTestOnBorrow()
    {
    		return testOnBorrow_ + "";
    }

    public void setDataSourceName ( String name )
    {
        xadsJndiName_ = name;
    }

    public String getDataSourceName ()
    {
        return xadsJndiName_;
    }

    public void setXaDataSource ( XADataSource xads )
    {
        xads_ = xads;
    }

    public XADataSource getXaDataSource ()
    {
        return xads_;
    }

    public void setUniqueResourceName ( String resourceName )
    {
        resourceName_ = resourceName;
    }

    public String getUniqueResourceName ()
    {
        return resourceName_;
    }

    public void setXidFormat ( String factory )
    {
        xidFactory_ = factory;
    }

    public String getXidFormat ()
    {
        return xidFactory_;
    }

    public void setConnectionPoolSize ( String poolSize )
    {
        poolSize_ = Integer.parseInt ( poolSize );
    }

    public String getConnectionPoolSize ()
    {
        return "" + poolSize_;
    }

    public void setConnectionTimeout ( String timeout )
    {
        connectionTimeout_ = Integer.parseInt ( timeout );
    }

    public String getConnectionTimeout ()
    {
        return "" + connectionTimeout_;
    }

    public void setExclusiveConnectionMode ( String mode )
    {
        exclusive_ = "true".equals ( mode );
    }

    public String isExclusiveConnectionMode ()
    {
        return "" + exclusive_;
    }

    public TransactionalResource getTransactionalResource ()
    {
        try {
            checkSetup ();
        } catch ( SQLException err ) {
            err.printStackTrace ();
            throw new RuntimeException ( err.getMessage () );
        }
        return ds_.getTransactionalResource ();
    }

    public void setValidatingQuery ( String query )
    {
        validatingQuery_ = query;
    }

    public String getValidatingQuery ()
    {
        return validatingQuery_;
    }

    /**
     * Perform validation based on the validating query. This method does
     * nothing if no query was specified.
     * 
     * @throws SQLException
     *             If validation fails.
     */
    public void validate () throws SQLException
    {
        checkSetup ( true );
        String query = getValidatingQuery ();
        if ( query == null || query.equals ( "" ) )
            return;

        Connection c = null;
        Statement s = null;
        try {
            // don't use our own getConnection since it will
            // call checkSetup without validation flag!
            c = ds_.getConnection ();

            try {
                s = c.createStatement ();
                ResultSet rs = s.executeQuery ( query );
                s.close ();
            } finally {
                if ( s != null )
                    s.close ();
                // this will also close the resultsets if any
            }
        }

        finally {
            if ( c != null )
                c.close ();
        }
    }

    //
    //
    // IMPLEMENTATION OF HEURISTICDATASOURCE
    //
    //

    public Connection getConnection () throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ();
    }

    public Connection getConnection ( String user , String pw )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw );
    }

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    public Connection getConnection ( String msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, passwd, msg );
    }

    public Connection getConnection ( String user , String pw ,
            HeuristicMessage msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw, msg );
    }

    public int getLoginTimeout () throws SQLException
    {
        checkSetup ();
        return ds_.getLoginTimeout ();
    }

    public PrintWriter getLogWriter () throws SQLException
    {
        checkSetup ();
        return ds_.getLogWriter ();
    }

    public void setLoginTimeout ( int seconds ) throws SQLException
    {
        checkSetup ();
        ds_.setLoginTimeout ( seconds );
    }

    public void setLogWriter ( PrintWriter out ) throws SQLException
    {
        checkSetup ();
        ds_.setLogWriter ( out );
    }

    // close the datasources
    public void close () throws SQLException
    {
    	    //see case 21666: don't call setup
        //checkSetup ();
        if ( ds_ != null ) ds_.close ();
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    public Reference getReference () throws NamingException
    {
        return JtaDataSourceImp.createReference ( getUniqueResourceName () );
    }

	
}
