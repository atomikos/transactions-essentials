package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
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
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

public class JdbcTestJUnit extends TransactionServiceTestCase 
{

	private static final int POOLSIZE = 1;
	
	private UserTransactionService uts;
	private CompositeTransaction ct;
	private TransactionManager tm;
	private Connection c;
	private JtaDataSourceImp ds;
	private TestXADataSource tds;
	private TSInitInfo info;
	private CompositeTransactionManager ctm;
	private StringHeuristicMessage  msg;
	private TestLogAdministrator admin;
	private HeuristicMessage[] msgs;
	private XAConnectionFactory fact;
	
	public JdbcTestJUnit ( String name ) 
	{
		super ( name );
	}
	
	public void setUp()
	{
		super.setUp();
		uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "JdbcTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        	fact =
                new XAConnectionFactory ( "JDBCTest" , "" , "" , new TestXADataSource() );
        	fact.setExclusive ( true );
         TransactionalResource tres =
                  fact.getTransactionalResource();
         
         uts.registerResource ( 
               tres );
         admin = new TestLogAdministrator();
         uts.registerLogAdministrator ( admin );
         tds =
             ( TestXADataSource ) fact.getXADataSource();
         
         try {
			ds = new JtaDataSourceImp ( fact , POOLSIZE , 15 , null , false );
		} catch (SQLException e) {
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
		msg = new StringHeuristicMessage ( "JDBC access" );
	}

	
	public void tearDown()
	{
		//ensure proper shutdown
		uts.shutdown ( true );
		ds.close();
		super.tearDown();
	}
	
	public void testConnectionCountAfterStartup()
	throws Exception
	{
		uts.init ( info );
		//no of connections equals pool size plus one for recovery
		if ( tds.getConnectionCount() != POOLSIZE + 1 )
            failTest ( "Too many getConnection() calls on source?" );
	}

	public void testNormalProcessing()
	throws Exception
	{
		uts.init ( info );
		tm = uts.getTransactionManager();
		tm.begin();
        
        c = ds.getConnection ( msg );
        c.getMetaData();
        c.close();
          
        tm.commit();
	}
	
	public void testCloseConnectionAfterCommit()
	throws Exception
	{
		uts.init ( info );
		tm = uts.getTransactionManager();
		tm.begin();
        
        c = ds.getConnection ( msg );
        c.getMetaData();
       
          
        tm.commit();
        c.close();
	}
	
	public void testAddHeuristicMessage() throws Exception
	{
		   uts.init ( info );
		   tm = uts.getTransactionManager();
	       tm.begin();
	       ctm = uts.getCompositeTransactionManager();
	        //add heuristic participant to make sure tx is not forgotten
	        ct = ctm.getCompositeTransaction();
	        String tid = ct.getTid();
	        HeuristicParticipant hPart = new HeuristicParticipant ( 
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
	        LogControl logControl = admin.getLogControl();
	        String[] tids = new String[1];
	        tids[0] = tid;
	        AdminTransaction[] admintxs = logControl.getAdminTransactions ( tids );
	        if ( admintxs == null || admintxs.length == 0 )
	            failTest ( "No admintx after heuristic termination?" );
	            
	        AdminTransaction admintx = admintxs[0];
	        msgs = admintx.getHeuristicMessages();
	        Hashtable table = new Hashtable();
	        for ( int i = 0 ; i < msgs.length ; i++ ) {
	        	//System.out.println ( "Found msg " + msgs[i] );
	            table.put ( msgs[i].toString() , new Object() );
	        }
	        if ( ! table.containsKey ( msg.toString() ) )
	            failTest ( "Heuristic message not added through DataSource?" );
	}
	
	public void testAreErrorConnectionsReplaced()
	throws Exception
	{
		uts.init ( info );
		tm = uts.getTransactionManager();
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
            failTest ( "Connection not replaced on error?" );
	}
	
	public void testExclusiveMode() throws Exception
	{
		uts.init ( info );
		ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction ( 1000 );
        c = ds.getConnection();
        //close connection to make sure that non-exclusive
        //use would put it in the pool
        c.close();
        
        CompositeTransaction subtx = ctm.createCompositeTransaction ( 1000 );
        TestSynchronization testsync = new TestSynchronization ( ds , subtx );
        
        subtx.registerSynchronization ( testsync );
        subtx.getTransactionControl().getTerminator().commit();
        //this triggers beforecompletion, which triggers the 
        //getConnection in testsync
        
        
        
        ct.getTransactionControl().getTerminator().rollback();
        
        //assert that the two connections gotten are different
        if ( testsync.getConnection() == null )
            failTest ( "TestSynchronization did not work?" );
            
        if ( c.equals ( testsync.getConnection() ) )
            failTest ( "Exclusive connection mode fails?" );
	}
	
	public void testNonExclusiveMode() throws Exception
	{
	    uts.init ( info );
	    ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction ( 1000 );
        c = ds.getConnection();
        //close connection to make sure that non-exclusive
        //use would put it in the pool
        c.close();
        
        CompositeTransaction subtx = ctm.createCompositeTransaction ( 1000 );
        TestSynchronization testsync = new TestSynchronization ( ds , subtx );
        
        subtx.registerSynchronization ( testsync );
        subtx.getTransactionControl().getTerminator().commit();
        //this triggers beforecompletion, which triggers the 
        //getConnection in testsync
        
        
        
        ct.getTransactionControl().getTerminator().rollback();
        
        //assert that the two connections gotten are different
        if ( testsync.getConnection() == null )
            failTest ( "TestSynchronization did not work?" );

//REMOVED IN REDESING OF 2.0: ConnectionProxy instances are not equal            
//        if ( ! c.equals ( testsync.getConnection() ) )
//            failTest ( "Nonexclusive connection not reused?" );
        
	}
	
	public void testSimpleDataSourceBean() throws Exception
	{
	    	TSInitInfo info = uts.createTSInitInfo();	
	    	SimpleDataSourceBean bean = new SimpleDataSourceBean();	
	    	bean.setUniqueResourceName ( "SimpleDataSource" );
	    	if ( ! bean.getUniqueResourceName().equals ( "SimpleDataSource"))
	    		failTest ( "uniqueResourceName failure" );
	    	bean.setXaDataSourceClassName ( "COM.FirstSQL.Dbcp.DbcpXADataSource" );
	    	if ( ! bean.getXaDataSourceClassName().equals 
	    		( "COM.FirstSQL.Dbcp.DbcpXADataSource"))
	    		failTest ( "xaDataSourceClassName failure");
	    	bean.setXaDataSourceProperties ( "portNumber=8000;user=demo;" );
	    	if ( ! bean.getXaDataSourceProperties().equals ("portNumber=8000;user=demo;" ))
	    		failTest ( "xaDataSourceProperties failure");
	    	
	    	bean.setValidatingQuery("select * from s");
	    	if ( ! bean.getValidatingQuery().equals ( "select * from s" ))
	    		 failTest ( "validatingQuery failure");	 
	    		 
	    	try {
	    		 bean.validate();
	    	} catch ( Exception e )
	    	{
	    		System.err.println ( "WARNING: validating query not ok; FirstSQL not running?");
	    		
	    	}
	    	
	    	bean.setConnectionPoolSize(2);
	    	if ( bean.getConnectionPoolSize()!=2)
	    		failTest ( "connectionPoolSize failure");
	    	
	    	bean.setConnectionTimeout(10);
	    	if ( bean.getConnectionTimeout()!=10 ) 
	    		failTest ( "connectionTimeout failure");
	    	
	    	bean.setExclusiveConnectionMode(true);
	    	if ( ! bean.getExclusiveConnectionMode())
	    		failTest ( "exclusiveConnectionMode failure");
	
			bean.setExclusiveConnectionMode(false);
			if ( bean.getExclusiveConnectionMode())
				failTest ( "exclusiveConnectionMode failure");    	
	    	
	    	
	    	//validation by itself should NOT have added the resource to the config
	    	if ( Configuration.getResource ( "SimpleDataSource") != null )
	    		failTest ( "Validation leads to registration of datasource");
	    	
	    	//assert that JNDI binding works
	    	Reference ref = bean.getReference();
	    	String className = ref.getFactoryClassName();
	    	Class clazz = Class.forName ( className );
	    	ObjectFactory factory = ( ObjectFactory ) clazz.newInstance();
	    	Object result = factory.getObjectInstance ( ref , null , null , null );
	    	if ( ! ( result instanceof SimpleDataSourceBean ) ) 
	    		failTest ( "Wrong class on lookup: " + result.getClass().getName() );
	    	bean = ( SimpleDataSourceBean ) result;
	    	
	    	//attribute values must have been conserved by binding
			if ( ! bean.getUniqueResourceName().equals ( "SimpleDataSource"))
				failTest ( "uniqueResourceName JNDI failure" );
			if ( ! bean.getXaDataSourceClassName().equals 
						( "COM.FirstSQL.Dbcp.DbcpXADataSource"))
						failTest ( "xaDataSourceClassName JNDI failure");	
			if ( ! bean.getXaDataSourceProperties().equals ("portNumber=8000;user=demo;" ))
						failTest ( "xaDataSourceProperties JNDI failure");
			if ( ! bean.getValidatingQuery().equals ( "select * from s" ))
						 failTest ( "validatingQuery JNDI failure");	 
			if ( bean.getConnectionPoolSize()!=2)
						failTest ( "connectionPoolSize JNDI failure");
			if ( bean.getConnectionTimeout()!=10 ) 
						failTest ( "connectionTimeout JNDI failure");
			if ( bean.getExclusiveConnectionMode())
						failTest ( "exclusiveConnectionMode JNDI failure");    	
	    	
						 			
	    	uts.init ( info );
	    	
	    	try {
	    		 Connection c = bean.getConnection();
	    		 c.close();
				//now, the resource should have been added
				if ( Configuration.getResource ( "SimpleDataSource" ) ==  null )
					failTest ( "Resource not added?");
	    	
	    	}
	    	catch ( SQLException notRunning ) {
	    		System.err.println ( "Warning: FirstSQL not running?");
	    	}
	}
	
	public void testTransactionStartAfterConnectionGotten() throws Exception
	{
		uts.init ( info );
	    ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction ( 1000 );
		c = ds.getConnection();
		//first get connection
		
		ct = ctm.createCompositeTransaction ( 1000 );
		
		c.close();
        

        
		ct.getTransactionControl().getTerminator().rollback();
	}
	
	public void testPooledConnectionUsableByThirdPartyPoolsInExclusiveMode()
	throws Exception
	{
		testIfConnectionsAreUsableByThirdPartyPools ( true );
	}
	
	public void testPooledConnectionUsableByThirdPartyPoolsInNonExclusiveMode()
	throws Exception
	{
		testIfConnectionsAreUsableByThirdPartyPools ( false );
	}
	
	private void testIfConnectionsAreUsableByThirdPartyPools ( boolean exclusive )
	throws Exception
	{
		fact.setExclusive ( exclusive );
		uts.init ( info );
	    ctm = uts.getCompositeTransactionManager();
        
        TestConnectionEventListener l = new TestConnectionEventListener();
		TestConnectionEventListener l2 = new TestConnectionEventListener();
		if ( l.isNotified() ) 
			failTest ( "Error in TestConnectionEventListener" );

		DTPPooledConnection pc = ( DTPPooledConnection) ds.getPooledConnection();
		
		pc.addConnectionEventListener(l);
		pc.addConnectionEventListener ( l2 );
		
		if ( pc.unsetResourceTransaction() != null )
			failTest ( "Resourcetransaction before tx starts?");
		
		

		
		c = pc.getConnection();
		
		if (  c != pc.getConnection() ) 
			failTest ( 
			"Different connection returned " + 
			"on second getConnection() before close()");
		
		//first get connection
		c.getMetaData();
		
		if ( pc.unsetResourceTransaction() != null )
			failTest ( "Resourcetransaction before tx starts?");
		
		ct = ctm.createCompositeTransaction ( 1000 );
        ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
		
		c.getMetaData();
		
		
		ResourceTransaction restx = pc.unsetResourceTransaction();
		if ( restx == null ) 
			failTest ( "No resourcetransaction for pooled connection in tx");
		pc.setResourceTransaction ( restx );	
		
		
		pc.removeConnectionEventListener ( l2 );
		
		if ( l.isNotified() )
			failTest ( "Listener notified before close ");
		
		c.close();
		
		if ( l2.isNotified() ) 
			failTest ( "Remove of listener not OK" );
		
		if ( ! exclusive && ! l.isNotified() )
			failTest ( "Listener not notified after close");
		
		if ( ! c.isClosed() )
			failTest ( "Connection not closed after close()");
	
		
		if ( ! exclusive && ! pc.isDiscarded() ) 
			failTest ( "Close not propagated to PooledConnection " +
			pc + " ; vital for pooling!" );
		
		if ( exclusive && pc.isDiscarded() )
			failTest ( "Exclusive mode failure: recycled before 2PC");
		
		if ( ! exclusive && pc.unsetResourceTransaction() != null )
				failTest ( "Resourcetransaction after close");

        
		ct.rollback();
		
		if ( exclusive && !pc.isDiscarded() )
			failTest ( "Exclusive connection not discarded after rollback");
		
		if ( exclusive && pc.unsetResourceTransaction() != null )
			failTest ( "Exclusive connection has restx after rollback");

        
        Connection c2 = pc.getConnection();
        if ( c2 == c ) 
        	failTest ( "Same connection gotten after close()");
        c2.close();
	}
	
	public void testSimpleDataSourceBeanRecovery()
	throws Exception
	{
		uts.init ( info );
    	String name = "TestSimpleDataSource";
    	UserTransaction utx = null;
		
		SimpleDataSourceBean bean = new SimpleDataSourceBean();	
		bean.setUniqueResourceName ( name );
		bean.setXaDataSourceClassName ( "com.atomikos.jdbc.TestXADataSource" );
		
		//trigger initialization
		c = bean.getConnection();
		c.close();
		
		//assert that the resource is added for recovery
		if ( Configuration.getResource ( name ) == null )
			failTest ( "SimpleDataSourceBean does not register for recovery");
			 
		
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
	
    //
    //Assert that user and password are 
    //propagated to the XA datasource
    //or recovery may fail
    public void testUserPasswordPropagation()
    throws Exception
    {
    	uts.init ( info );
    	String resName = "resource";
    	String user = "john";
    	String password = "secret";
    	
    	TestXADataSource xads = new TestXADataSource();
   
    	XAConnectionFactory fact = 
    		new XAConnectionFactory ( resName , user , 
    								  password , xads );
    	fact.getPooledConnection().getConnection();
    	if ( ! user.equals ( xads.getLastUser()))
    		failTest ( "User not propagated");
    	if ( ! password.equals ( xads.getLastPassword()))
    		failTest ( "Password not propagated" );
    	
    }
    
    public void testTransactionEnlistmentInExclusiveMode()
    throws Exception
    {
    		testTransactionEnlistment ( true );
    }
    
    public void testTransactionEnlistmentInNonExclusiveMode()
    throws Exception
    {
    		testTransactionEnlistment ( false );
    }
    
    private void testTransactionEnlistment ( boolean exclusive )
    throws Exception
    {
     	UserTransaction utx = null;
		DTPPooledConnection pc = null;
		TestXAResource xares = new TestXAResource();
    	Connection c = null;
		XADataSource xads = new TestXADataSource ( xares );
		String resName = "transactionEnlistmentTest";
		XAConnectionFactory fact =
			new XAConnectionFactory ( resName , "" , "" , xads );
		fact.setExclusive ( exclusive );
		TransactionalResource tres =
			  fact.getTransactionalResource();
			  
		pc = null;
		XAConnection xac = xads.getXAConnection();
		if ( ! exclusive ) pc =	new ExternalXAPooledConnectionImp ( xac , tres );
		else pc = new ExclusiveExternalXAPooledConnectionImp ( xac , tres );	
		xac = xads.getXAConnection();
		if ( ! exclusive ) pc =	new ExternalXAPooledConnectionImp ( xac , tres );
		else pc = new ExclusiveExternalXAPooledConnectionImp ( xac , tres );	
    
    	//assert that no xid is enlisted if no tm is there
    	c = pc.getConnection();
    	c.getMetaData();
    	c.close();
    	if ( xares.getLastStarted() != null )
    		failTest ( "Enlist without TM?" );
    	
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
			failTest ( "Enlist without tx?" );
    	  
    	
    	//assert that xid is enlisted if tx started
    	utx = uts.getUserTransaction();
    	utx.begin();
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		Xid xid = xares.getLastStarted();
		if ( xid == null )
			failTest ( "No enlist with tx?" );    	
    	
    	if ( !xid.toString().startsWith ( tmName ) )
    		failTest ( "Xid format not recoverable");
    		
    	if ( exclusive && pc.isDiscarded() )
    		failTest ( "Exclusive mode failure");
    		
    	if ( ! exclusive && !pc.isDiscarded() )
    		failTest ( "Nonexclusive mode failure");
    	
    	utx.commit();  
    	//assert that the resource is included in 2PC
    	if ( ! xid.equals ( xares.getLastCommitted() ) )
    		failTest ( "Not committed");
    	
    	if ( exclusive && ! pc.isDiscarded() )
    		failTest ( "Exclusive pc not discarded after commit");
    	
    	//clear xaresource
    	xares.reset();
    	//test again, without tx, to test if pc is enlisted or not
		//assert that no xid is enlisted if TM up,but no tx is there
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		if ( xares.getLastStarted() != null )
			failTest ( "Enlist without tx?" );    	
    	
    	uts.shutdown ( true );	
    	
    	//test again, without TM
		c = pc.getConnection();
		c.getMetaData();
		c.close();
		if ( xares.getLastStarted() != null )
			failTest ( "Enlist without TM?" );   
    }
    
    /**
     * See bug #20140 
     */
	public void testNonTransactionalMethods()
	throws Exception
	{
		uts.init ( info );
		tm = uts.getTransactionManager();
        tm.begin();
        c = ds.getConnection();
        c.close();
        tm.rollback();
        c.hashCode();
	}

}
