package com.atomikos.jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

 /**
  *
  *
  *A release test for the JDBC framework.
  */

public class ReleaseTester
{
    private static final int POOLSIZE = 1;
    
    public static void test ( UserTransactionService uts ) 
    throws Exception
    {
        
        Connection c = null;
        LogControl logControl = null;
        AdminTransaction[] admintxs = null;
        Hashtable table = null;
        String[] tids = null;
        String tid = null;
        AdminTransaction admintx = null;
        Transaction tx = null;
        CompositeTransaction ct = null;
        HeuristicMessage msg = null;
        HeuristicMessage[] msgs = null;
        HeuristicParticipant hPart = null;
        TSInitInfo info = uts.createTSInitInfo();
        TestLogAdministrator admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
        
        XAConnectionFactory fact =
            new XAConnectionFactory ( "JDBCPoolingTest" , "" , "" , new TestXADataSource() );
        TransactionalResource tres =
              fact.getTransactionalResource();
        
        info.registerResource ( 
           tres );
        uts.init ( info );
        TransactionManager tm = uts.getTransactionManager();
        CompositeTransactionManager ctm =
            uts.getCompositeTransactionManager();
        
        TestXADataSource tds =
            ( TestXADataSource ) fact.getXADataSource();
        
        JtaDataSourceImp ds = new JtaDataSourceImp ( fact , POOLSIZE , 15  , null , false );
        
        //number of connection calls is one more than poolsize:
        //1 extra connection for datasource setup
        if ( tds.getConnectionCount() != POOLSIZE + 1 )
            throw new Exception ( "Too many getConnection() calls on source?" );
        
        //
        //CASE 1: assert that normal transaction works fine
        //
        
        tm.begin();
        msg = new StringHeuristicMessage ( "JDBC access" );
        c = ds.getConnection ( msg );
        
        c.close();
          
        tm.commit();
        
        //
        //CASE 2: assert that the heuristic message is really added.
        //
        
        tm.begin();
        
        //add heuristic participant to make sure tx is not forgotten
        ct = ctm.getCompositeTransaction();
        tid = ct.getTid();
        hPart = new HeuristicParticipant ( 
            new StringHeuristicMessage ( "heuristic participant" ) );
        hPart.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
        ct.addParticipant ( hPart );
        
        c = ds.getConnection ( msg );
        
        //call a method on the connection to trigger
        //enlistment
        c.getMetaData();
        
        c.close();
        
        try {
          tm.commit();
        }
        catch ( Exception e ) {
            //should happen 
        }
        //check if msg is there
        logControl = admin.getLogControl();
        tids = new String[1];
        tids[0] = tid;
        admintxs = logControl.getAdminTransactions ( tids );
        if ( admintxs == null || admintxs.length == 0 )
            throw new Exception ( "No admintx after heuristic termination?" );
            
        admintx = admintxs[0];
        msgs = admintx.getHeuristicMessages();
        table = new Hashtable();
        for ( int i = 0 ; i < msgs.length ; i++ ) {
        	//System.out.println ( "Found msg " + msgs[i] );
            table.put ( msgs[i].toString() , new Object() );
        }
        if ( ! table.containsKey ( msg.toString() ) )
            throw new Exception ( "Heuristic message not added through DataSource?" );
        
        //
        //CASE 3: assert that error connection is replaced in pool
        //
        
        tm.begin();
        c = ds.getConnection();
        try {
            c.createStatement();
            //this should trigger a SQLException and invalidate
            //the connection
        }
        catch (Exception e ) {
              //should happen
        }
        c.close();
        tm.rollback();
        
        //the number of connections gotten from source should be
        //the original size + 1
        if ( tds.getConnectionCount() == POOLSIZE )
            throw new Exception ( "Connection not replaced on error?" );
        
        //perform clean shutdown
        uts.shutdown ( true );
        ds.close();
        
        
    } 
    
     /**
      *Tests if exclusive mode works as it should.
      */
      
    public static void testExclusiveMode ( UserTransactionService uts )
    throws Exception
    {
        Connection c = null;

        CompositeTransaction ct = null , subtx = null;
        TSInitInfo info = uts.createTSInitInfo();
        TestSynchronization testsync = null;
        
        XAConnectionFactory fact =
            new XAConnectionFactory ( "JDBCPoolingTest" , "" , "" , new TestXADataSource() );
        fact.setExclusive ( true );
        TransactionalResource tres =
              fact.getTransactionalResource();
        
        info.registerResource ( 
           tres );
        uts.init ( info );
       
        CompositeTransactionManager ctm =
            uts.getCompositeTransactionManager();
        
        TestXADataSource tds =
            ( TestXADataSource ) fact.getXADataSource();
        
        JtaDataSourceImp ds = new JtaDataSourceImp ( fact , 1 , 15 , null , false );
        
        ct = ctm.createCompositeTransaction ( 1000 );
        c = ds.getConnection();
        //close connection to make sure that non-exclusive
        //use would put it in the pool
        c.close();
        
        subtx = ctm.createCompositeTransaction ( 1000 );
        testsync = new TestSynchronization ( ds , subtx );
        
        subtx.registerSynchronization ( testsync );
        subtx.getTransactionControl().getTerminator().commit();
        //this triggers beforecompletion, which triggers the 
        //getConnection in testsync
        
        
        
        ct.getTransactionControl().getTerminator().rollback();
        
        //assert that the two connections gotten are different
        if ( testsync.getConnection() == null )
            throw new Exception ( "TestSynchronization did not work?" );
            
        if ( c.equals ( testsync.getConnection() ) )
            throw new Exception ( "Exclusive connection mode fails?" );
        
        //perform clean shutdown
        uts.shutdown ( true );
        ds.close();
         
    }
    
      /**
      *Tests if non-exclusive mode works as it should.
      */
      
    public static void testNonExclusiveMode ( UserTransactionService uts )
    throws Exception
    {
        Connection c = null;

        CompositeTransaction ct = null , subtx = null;
        TSInitInfo info = uts.createTSInitInfo();
        TestSynchronization testsync = null;
        
        XAConnectionFactory fact =
            new XAConnectionFactory ( "JDBCPoolingTest" , "" , "" , new TestXADataSource() );
        fact.setExclusive ( false );
        TransactionalResource tres =
              fact.getTransactionalResource();
        
        info.registerResource ( 
           tres );
        uts.init ( info );
       
        CompositeTransactionManager ctm =
            uts.getCompositeTransactionManager();
        
        TestXADataSource tds =
            ( TestXADataSource ) fact.getXADataSource();
        
        JtaDataSourceImp ds = new JtaDataSourceImp ( fact , 1 , 15 , null , false );
        
        ct = ctm.createCompositeTransaction ( 1000 );
        c = ds.getConnection();
        //close connection to make sure that non-exclusive
        //use would put it in the pool
        c.close();
        
        subtx = ctm.createCompositeTransaction ( 1000 );
        testsync = new TestSynchronization ( ds , subtx );
        
        subtx.registerSynchronization ( testsync );
        subtx.getTransactionControl().getTerminator().commit();
        //this triggers beforecompletion, which triggers the 
        //getConnection in testsync
        
        
        
        ct.getTransactionControl().getTerminator().rollback();
        
        //assert that the two connections gotten are different
        if ( testsync.getConnection() == null )
            throw new Exception ( "TestSynchronization did not work?" );

//REMOVED IN REDESING OF 2.0: ConnectionProxy instances are not equal            
//        if ( ! c.equals ( testsync.getConnection() ) )
//            throw new Exception ( "Nonexclusive connection not reused?" );
        
        //perform clean shutdown
        uts.shutdown ( true );
        ds.close();
         
    }
    
    
    public static void testSimpleDataSourceBean ( UserTransactionService uts )
    throws Exception
    {
    	TSInitInfo info = uts.createTSInitInfo();	
    	SimpleDataSourceBean bean = new SimpleDataSourceBean();	
    	bean.setUniqueResourceName ( "SimpleDataSource" );
    	if ( ! bean.getUniqueResourceName().equals ( "SimpleDataSource"))
    		throw new Exception ( "uniqueResourceName failure" );
    	bean.setXaDataSourceClassName ( "COM.FirstSQL.Dbcp.DbcpXADataSource" );
    	if ( ! bean.getXaDataSourceClassName().equals 
    		( "COM.FirstSQL.Dbcp.DbcpXADataSource"))
    		throw new Exception ( "xaDataSourceClassName failure");
    	bean.setXaDataSourceProperties ( "portNumber=8000;user=demo;" );
    	if ( ! bean.getXaDataSourceProperties().equals ("portNumber=8000;user=demo;" ))
    		throw new Exception ( "xaDataSourceProperties failure");
    	
    	bean.setValidatingQuery("select * from s");
    	if ( ! bean.getValidatingQuery().equals ( "select * from s" ))
    		 throw new Exception ( "validatingQuery failure");	 
    		 
    	try {
    		 bean.validate();
    	} catch ( Exception e )
    	{
    		System.err.println ( "WARNING: validating query not ok; FirstSQL not running?");
    		
    	}
    	
    	bean.setConnectionPoolSize(2);
    	if ( bean.getConnectionPoolSize()!=2)
    		throw new Exception ( "connectionPoolSize failure");
    	
    	bean.setConnectionTimeout(10);
    	if ( bean.getConnectionTimeout()!=10 ) 
    		throw new Exception ( "connectionTimeout failure");
    	
    	bean.setExclusiveConnectionMode(true);
    	if ( ! bean.getExclusiveConnectionMode())
    		throw new Exception ( "exclusiveConnectionMode failure");

		bean.setExclusiveConnectionMode(false);
		if ( bean.getExclusiveConnectionMode())
			throw new Exception ( "exclusiveConnectionMode failure");    	
    	
    	
    	//validation by itself should NOT have added the resource to the config
    	if ( Configuration.getResource ( "SimpleDataSource") != null )
    		throw new Exception ( "Validation leads to registration of datasource");
    	
    	//assert that JNDI binding works
    	Reference ref = bean.getReference();
    	String className = ref.getFactoryClassName();
    	Class clazz = Class.forName ( className );
    	ObjectFactory factory = ( ObjectFactory ) clazz.newInstance();
    	Object result = factory.getObjectInstance ( ref , null , null , null );
    	if ( ! ( result instanceof SimpleDataSourceBean ) ) 
    		throw new Exception ( "Wrong class on lookup: " + result.getClass().getName() );
    	bean = ( SimpleDataSourceBean ) result;
    	
    	//attribute values must have been conserved by binding
		if ( ! bean.getUniqueResourceName().equals ( "SimpleDataSource"))
			throw new Exception ( "uniqueResourceName JNDI failure" );
		if ( ! bean.getXaDataSourceClassName().equals 
					( "COM.FirstSQL.Dbcp.DbcpXADataSource"))
					throw new Exception ( "xaDataSourceClassName JNDI failure");	
		if ( ! bean.getXaDataSourceProperties().equals ("portNumber=8000;user=demo;" ))
					throw new Exception ( "xaDataSourceProperties JNDI failure");
		if ( ! bean.getValidatingQuery().equals ( "select * from s" ))
					 throw new Exception ( "validatingQuery JNDI failure");	 
		if ( bean.getConnectionPoolSize()!=2)
					throw new Exception ( "connectionPoolSize JNDI failure");
		if ( bean.getConnectionTimeout()!=10 ) 
					throw new Exception ( "connectionTimeout JNDI failure");
		if ( bean.getExclusiveConnectionMode())
					throw new Exception ( "exclusiveConnectionMode JNDI failure");    	
    	
					 			
    	uts.init ( info );
    	
    	try {
    		 Connection c = bean.getConnection();
    		 c.close();
			//now, the resource should have been added
			if ( Configuration.getResource ( "SimpleDataSource" ) ==  null )
				throw new Exception ( "Resource not added?");
    	
    	}
    	catch ( SQLException notRunning ) {
    		System.err.println ( "Warning: FirstSQL not running?");
    	}
    	
 
    	
    	uts.shutdown ( true );
    	
    	//assert external pooling functionality works
    	//even if there is no TM running
    	try {
			PooledConnection pc = bean.getPooledConnection();
			Connection c = pc.getConnection();
			c.close();
			
			pc = bean.getPooledConnection ( "demo" , "" );
			c = pc.getConnection();
			c.close();
    	}
    	catch ( SQLException notRunning ) {
    		System.err.println ( "Warning: FirstSQL not running?" );
    	}
    	

    	
    
    }
    
    public static void testLateTransactionStart ( UserTransactionService uts,
    	boolean exclusiveMode )
    throws Exception
	{
		Connection c = null;

		CompositeTransaction ct = null;
		TSInitInfo info = uts.createTSInitInfo();
		TestSynchronization testsync = null;
        
		XAConnectionFactory fact =
			new XAConnectionFactory ( "LateTransactionStartTest" , "" , "" , new TestXADataSource() );
		fact.setExclusive ( exclusiveMode );
		TransactionalResource tres =
			  fact.getTransactionalResource();
        
		info.registerResource ( 
		   tres );
		uts.init ( info );
       
		CompositeTransactionManager ctm =
			uts.getCompositeTransactionManager();
        
		TestXADataSource tds =
			( TestXADataSource ) fact.getXADataSource();
        
		JtaDataSourceImp ds = new JtaDataSourceImp ( fact , 1 , 15 , null , false );
        

		c = ds.getConnection();
		//first get connection
		
		ct = ctm.createCompositeTransaction ( 1000 );
		
		c.close();
        

        
		ct.getTransactionControl().getTerminator().rollback();

        
		//perform clean shutdown
		uts.shutdown ( true );
		ds.close();
    	
    }
    
    //test if our pooled connections can be used
    //externally by thirdparty pools
    public static void testExternalPooledConnection (
    	UserTransactionService uts , boolean exclusive )
    throws Exception
    {
    	
		Connection c = null;
		ExternalXAPooledConnectionImp pc = null;
		CompositeTransaction ct = null;
		TSInitInfo info = uts.createTSInitInfo();
		TestSynchronization testsync = null;
		TestConnectionEventListener l = new TestConnectionEventListener();
		TestConnectionEventListener l2 = new TestConnectionEventListener();
        
		XAConnectionFactory fact =
			new XAConnectionFactory ( "ExternalPooledConnectionTest" , "" , "" , new TestXADataSource() );
		fact.setExclusive ( exclusive );
		TransactionalResource tres =
			  fact.getTransactionalResource();
        
		info.registerResource ( 
		   tres );
		uts.init ( info );
       
		CompositeTransactionManager ctm =
			uts.getCompositeTransactionManager();
        
		TestXADataSource tds =
			( TestXADataSource ) fact.getXADataSource();
        
		JtaDataSourceImp ds = new JtaDataSourceImp (  fact , 1 , 15 , null , false );
        
		
		if ( l.isNotified() ) 
			throw new Exception ( "Error in TestConnectionEventListener" );

		pc = ( ExternalXAPooledConnectionImp ) ds.getPooledConnection();
		
		pc.addConnectionEventListener(l);
		pc.addConnectionEventListener ( l2 );
		
		if ( pc.unsetResourceTransaction() != null )
			throw new Exception ( "Resourcetransaction before tx starts?");
		
		

		
		c = pc.getConnection();
		
		if (  c != pc.getConnection() ) 
			throw new Exception ( 
			"Different connection returned " + 
			"on second getConnection() before close()");
		
		//first get connection
		c.getMetaData();
		
		if ( pc.unsetResourceTransaction() != null )
			throw new Exception ( "Resourcetransaction before tx starts?");
		
		ct = ctm.createCompositeTransaction ( 1000 );
		
		c.getMetaData();
		
		
		ResourceTransaction restx = pc.unsetResourceTransaction();
		if ( restx == null ) 
			throw new Exception ( "No resourcetransaction for pooled connection in tx");
		pc.setResourceTransaction ( restx );	
		
		
		pc.removeConnectionEventListener ( l2 );
		
		if ( l.isNotified() )
			throw new Exception ( "Listener notified before close ");
		
		c.close();
		
		if ( l2.isNotified() ) 
			throw new Exception ( "Remove of listener not OK" );
		
		if ( ! exclusive && ! l.isNotified() )
			throw new Exception ( "Listener not notified after close");
		
		if ( ! c.isClosed() )
			throw new Exception ( "Connection not closed after close()");
		
		System.out.flush();
		
		if ( ! exclusive && ! pc.isDiscarded() ) 
			throw new Exception ( "Close not propagated to PooledConnection " +
			pc + " ; vital for pooling!" );
		
		if ( exclusive && pc.isDiscarded() )
			throw new Exception ( "Exclusive mode failure: recycled before 2PC");
		
		if ( ! exclusive && pc.unsetResourceTransaction() != null )
				throw new Exception ( "Resourcetransaction after close");

        
		ct.getTransactionControl().getTerminator().rollback();
		
		if ( exclusive && !pc.isDiscarded() )
			throw new Exception ( "Exclusive connection not discarded after rollback");
		
		if ( exclusive && pc.unsetResourceTransaction() != null )
			throw new Exception ( "Exclusive connection has restx after rollback");

        
        Connection c2 = pc.getConnection();
        if ( c2 == c ) 
        	throw new Exception ( "Same connection gotten after close()");
        c2.close();
        
        
		//perform clean shutdown
		uts.shutdown ( true );
		ds.close();    	
    	    
    }
    
    public static void testSimpleDataSourceBeanRecovery (
    	UserTransactionService uts )
    	throws Exception
    {
    	Connection c = null;
    	String name = "TestSimpleDataSource";
    	UserTransaction utx = null;
		
		SimpleDataSourceBean bean = new SimpleDataSourceBean();	
		bean.setUniqueResourceName ( name );
		bean.setXaDataSourceClassName ( "com.atomikos.jdbc.test.TestXADataSource" );
		
		//trigger initialization
		c = bean.getConnection();
		c.close();
		
		//assert that the resource is added for recovery
		if ( Configuration.getResource ( name ) == null )
			throw new Exception ( "SimpleDataSourceBean does not register for recovery");
			 
		
		//make sure that a second instance can be constructed
		//without problems - needed for certain JNDI object
		//factory mechanisms (like Tomcat)
		bean = new SimpleDataSourceBean();	
		bean.setUniqueResourceName ( name );
		bean.setXaDataSourceClassName ( "com.atomikos.jdbc.test.TestXADataSource" );
		
		//trigger initialization 
		c = bean.getConnection();
		c.close();
		
		
    }
    
    //test the basic interface of the pooled connection imp
    public static void testExternalPooledConnectionBasics ( boolean exclusive )
    throws Exception
    {
    	XADataSource xads = new TestXADataSource();
		XAConnectionFactory fact =
			new XAConnectionFactory ( "ExternalPooledConnectionBasicsTest" , "" , "" , xads );
		fact.setExclusive ( exclusive );
		TransactionalResource tres =
			  fact.getTransactionalResource();
			  
    	DTPPooledConnection pc = null;
    	XAConnection xac = xads.getXAConnection();
    	if ( ! exclusive ) pc =	new ExternalXAPooledConnectionImp ( xac , tres );
    	else pc = new ExclusiveExternalXAPooledConnectionImp ( xac , tres );
    	Date now = new Date();
    	pc.setLastUse ( now );
    	if ( pc.getLastUse() != now )
    		throw new Exception ( "lastUse fails" );
    	
    	pc.getConnection().close();
    	if ( ! pc.isDiscarded() && ! exclusive )
    		throw new Exception ( "isDiscarded fails");
    		
    	pc.setInvalidated();
    	if ( !pc.getInvalidated() )
    		throw new Exception ("invalidated fails");
    	
    	
    	pc.close();
    	tres.close();
    	
    }
    
    //assert that pooled connections work in the 
    //scope of a tx if needed
    public static void testTransactionEnlistment (
    	UserTransactionService uts , boolean exclusive )
    	throws Exception
    {
    	System.out.println ( "testTransactionEnlistment with exclusive=" + 
    	exclusive );
    	System.out.flush();
    	UserTransaction utx = null;
    	Xid xid = null;
    	TestXAResource xares = new TestXAResource();
    	Connection c = null;
		XADataSource xads = new TestXADataSource ( xares );
		String resName = "transactionEnlistmentTest";
		XAConnectionFactory fact =
			new XAConnectionFactory ( resName , "" , "" , xads );
		fact.setExclusive ( exclusive );
		TransactionalResource tres =
			  fact.getTransactionalResource();
			  
		DTPPooledConnection pc = null;
		XAConnection xac = xads.getXAConnection();
		if ( ! exclusive ) pc =	new ExternalXAPooledConnectionImp ( xac , tres );
		else pc = new ExclusiveExternalXAPooledConnectionImp ( xac , tres );	
    
    	//assert that no xid is enlisted if no tm is there
    	c = pc.getConnection();
    	c.getMetaData();
    	c.close();
    	if ( xares.getLastStarted() != null )
    		throw new Exception ( "Enlist without TM?" );
    	
    	//startup TM
    	TSInitInfo info = uts.createTSInitInfo();
    	uts.init ( info );
    	String tmName = info.getProperties().getProperty (
    		AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME);
    	
		//assert that no xid is enlisted if TM up,but no tx is there
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		if ( xares.getLastStarted() != null )
			throw new Exception ( "Enlist without tx?" );
    	  
    	
    	//assert that xid is enlisted if tx started
    	utx = uts.getUserTransaction();
    	utx.begin();
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		xid = xares.getLastStarted();
		if ( xid == null )
			throw new Exception ( "No enlist with tx?" );    	
    	
    	if ( !xid.toString().startsWith ( tmName ) )
    		throw new Exception ( "Xid format not recoverable");
    		
    	if ( exclusive && pc.isDiscarded() )
    		throw new Exception ( "Exclusive mode failure");
    		
    	if ( ! exclusive && !pc.isDiscarded() )
    		throw new Exception ( "Nonexclusive mode failure");
    	
    	utx.commit();  
    	//assert that the resource is included in 2PC
    	if ( ! xid.equals ( xares.getLastCommitted() ) )
    		throw new Exception ( "Not committed");
    	
    	if ( exclusive && ! pc.isDiscarded() )
    		throw new Exception ( "Exclusive pc not discarded after commit");
    	
    	//clear xaresource
    	xares.reset();
    	//test again, without tx, to test if pc is enlisted or not
		//assert that no xid is enlisted if TM up,but no tx is there
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		if ( xares.getLastStarted() != null )
			throw new Exception ( "Enlist without tx?" );    	
    	
    	uts.shutdown ( true );	
    	
    	//test again, without TM
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		if ( xares.getLastStarted() != null )
			throw new Exception ( "Enlist without TM?" );    	
    		
    
    }
    //
    //Assert that user and password are 
    //propagated to the XA datasource
    //or recovery may fail
    public static void testUserPasswordPropagation()
    throws Exception
    {
    	String resName = "resource";
    	String user = "john";
    	String password = "secret";
    	TestXADataSource xads = new TestXADataSource();
    	
    	XAConnectionFactory fact = 
    		new XAConnectionFactory ( resName , user , 
    								  password , xads );
    	fact.getPooledConnection().getConnection();
    	if ( ! user.equals ( xads.getLastUser()))
    		throw new Exception ( "User not propagated");
    	if ( ! password.equals ( xads.getLastPassword()))
    		throw new Exception ( "Password not propagated" );
    	
    }
    
    public static void main ( String[] args )
    {
        try {
            System.err.println ( "Starting JDBC test..." );
            
            UserTransactionService uts =
                new com.atomikos.icatch.standalone.UserTransactionServiceFactory().
                    getUserTransactionService ( new Properties() );
            
			testTransactionEnlistment ( uts , false );
			testTransactionEnlistment ( uts , true );
            testExternalPooledConnectionBasics ( false );
			testExternalPooledConnectionBasics ( true );
			testExternalPooledConnection ( uts , false );
			testExternalPooledConnection ( uts , true );
            test ( uts );
            testExclusiveMode ( uts );
            testNonExclusiveMode ( uts );
            testSimpleDataSourceBean ( uts );
			testLateTransactionStart ( uts , false );
			testLateTransactionStart ( uts , true );
			testSimpleDataSourceBeanRecovery ( uts );
			testUserPasswordPropagation();
		
			
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
        finally {
            System.err.println ( "Done!" ); 
        }
    }
}
 
