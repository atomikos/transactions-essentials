package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Reference;
import javax.transaction.TransactionManager;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.VoteNoParticipant;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.util.SerializableObjectFactory;

public class DeprecatedJdbcNonXaTestJUnit extends TransactionServiceTestCase 
{

	private UserTransactionService uts;
	private DriverManagerDataSource ds;
	private NonXADataSourceBean testDs;
	private TransactionManager tm;
	private Connection c;
	private int key;
	
	private static int lastKey = 0;
	
	private static synchronized int getNextKey()
	{
		return lastKey++;
	}
	
	public DeprecatedJdbcNonXaTestJUnit ( String name ) 
	{
		super ( name );
	}

	public void setUp()
	{
		super.setUp();
		uts =
            new UserTransactionServiceImp();
		//todo setup datasource
		ds = new DriverManagerDataSource();
		ds.setUser ( "sa");
		ds.setPassword ( "");
		String dir = getTemporaryOutputDir() + "/";
		String url = "jdbc:hsqldb:" + dir +  "NonXATestDB";
		ds.setUrl ( url );
		ds.setDriverClassName ( "org.hsqldb.jdbcDriver" );
		
		//printThreadInfo();
		
		testDs = new NonXADataSourceBean();

		testDs.setUniqueResourceName ( "testDS" );
		testDs.setUrl ( url );
		testDs.setDriverClassName ( "org.hsqldb.jdbcDriver" );
		testDs.setUser("sa");
		testDs.setPassword("");
		testDs.setPoolSize(1);
		testDs.setConnectionTimeout(15);
		//testOnBorrow: to detect bug 21359
		testDs.setTestOnBorrow ( true );
	
		//printThreadInfo();
		
		//testDs = new NonXADataSourceImp ( ds , "TestDS" , "sa" , "" , 1 , 15 );
		
		//todo create test table if necessary
		
		try {
			Connection c = ds.getConnection();
			Statement s = c.createStatement();
			try {
				
				s.executeUpdate ( "drop table NONXATABLE");
			}
			catch ( SQLException doesNotExist )
			{
				//ignore
			}
			
			s.executeUpdate ( "create table NONXATABLE ( key INTEGER )");
			s.close();
			c.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
		
	    TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "JdbcTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		uts.init ( info );
		tm = uts.getTransactionManager();
		
	}
	
	public void tearDown()
	{
		if ( uts != null ) uts.shutdown ( true );
		if ( testDs != null ) testDs.close();
		super.tearDown();
	}
	
	private boolean isInDatabase ( int key )
	throws Exception
	{
		boolean ret = false;
		
		Connection c = ds.getConnection();
		
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery ( "select * from NONXATABLE where key = " + key );
		if ( rs.next() ) ret = true;
		s.close();
		c.close();
		
		return ret;
	}
	
	private void assertPresent ( int key )
	throws Exception
	{
		if ( ! isInDatabase ( key ) ) failTest ( "Missing row with key: " + key );
	}
	
	private void assertNotPresent ( int key )
	throws Exception
	{
		if ( isInDatabase ( key ) ) failTest ( "Unexpected row with key: " + key );
	}
	
	private int insertNextKey ( Connection c , boolean simulateError )
	throws Exception
	{
		int ret = getNextKey();
		c = testDs.getConnection();
		PreparedStatement s = c.prepareStatement ( "insert into NONXATABLE values ( ? )" );
		if ( ! simulateError ) s.setInt ( 1 , ret );
		else s.setString ( 1 , "StringToCauseError"); 
		s.executeUpdate();
		s.close();
		
		return ret;
	}
	
	public void testBasicRollback() throws Exception
	{
		
		
		tm.begin();
		Connection c = testDs.getConnection();
		int key = insertNextKey ( c , false );
		c.close();
		
		tm.rollback();
		
		assertNotPresent ( key );
	}
	
	public void testBasicCommit() throws Exception
	{
		tm.begin();
		c = testDs.getConnection();
		key = insertNextKey ( c , false );
		c.close();
		
		tm.commit();
		assertPresent ( key );
	}
	
	public void testConnectionLevelRollbackNotAllowedInManagedTransaction()
	throws Exception
	{
		tm.begin();
		c = testDs.getConnection();
		try {
			c.rollback();
			throw new Exception ( "Connection.rollback works in a tx?");
		}
		catch ( SQLException normal ) {}
		tm.rollback();
		
	}
	
	public void testConnectionLevelCommitNotAllowedInManagedTransaction()
	throws Exception
	{
		tm.begin();
		c = testDs.getConnection();
		try {
			c.commit();
			throw new Exception ( "Connection.commit works in a tx?");
		}
		catch ( SQLException normal ) {}
		tm.rollback();
		
	}
	
	public void testLateRegistrationWithRollback()
	throws Exception
	{
		c = testDs.getConnection();
		tm.begin();
		key = insertNextKey ( c , false  );
		tm.rollback();
		c.close();
		assertNotPresent ( key );
	}
	
	public void testLateRegistrationWithCommit()
	throws Exception
	{
		c = testDs.getConnection();
		tm.begin();
		key = insertNextKey ( c , false  );
		tm.commit();
		c.close();
		assertPresent ( key );
	}
	
	public void testConnectionReuseWithManagedTransactions()
	throws Exception
	{
		c = testDs.getConnection();
		tm.begin();
		key = insertNextKey ( c , false );
		tm.rollback();
		assertNotPresent ( key );
		
		tm.begin();
		int secondKey = insertNextKey ( c , false);
		tm.commit();
		assertPresent ( secondKey );
		assertNotPresent ( key );
		
		tm.begin();
		int thirdKey = insertNextKey ( c , false );
		tm.rollback();
		assertNotPresent ( thirdKey );
		assertNotPresent ( key );
		assertPresent ( secondKey );
		
	}
	
	public void testConnectionReuseWithoutManagedTransactions()
	throws Exception
	{
		Connection c = null;
		key = 0;
		
		c = testDs.getConnection();
		c.setAutoCommit ( false );
		
		key = insertNextKey ( c , false );
		c.rollback();
		assertNotPresent ( key );
		
		int secondKey = insertNextKey ( c , false );
		c.commit();
		assertPresent ( secondKey );
		assertNotPresent ( key );
		
		c.close();
	}
	
	public void testConnectionsNotReusedBySubTransactions()
	throws Exception
	{
		c = testDs.getConnection();
		
		
		tm.begin();
		int key1 = insertNextKey ( c , false );
		int key2 = 0;
		tm.begin();
		try {
			key2 = insertNextKey ( c , false );
			failTest ( "Connection can be shared with subtx?");
		}
		catch ( SQLException ok ) {}
		tm.rollback();
		int key3 = insertNextKey ( c , false );
		tm.commit();
		assertPresent ( key1  );
		assertNotPresent ( key2 );
		assertPresent ( key3 );
		
		c.close();
	}
	
	public void testMultipleGetsAndClose() throws Exception
	{

		Connection c1 = null , c2 = null;
		int key1 = 0 , key2 = 0;
		TransactionManager tm = uts.getTransactionManager();
		
		c1 = testDs.getConnection();
		tm.begin();
		
		key1 = insertNextKey ( c1 , false );
		
		c2 = testDs.getConnection();
		
		
		
		key2 = insertNextKey ( c2 , false );
		
		c1.close();
		c2.close();
		tm.commit();
		assertPresent ( key1 );
		assertPresent ( key2 );

		c1 = testDs.getConnection();
		tm.begin();
		
		key1 = insertNextKey ( c1 ,  false );
		
		c2 = testDs.getConnection();
		
		
		
		key2 = insertNextKey ( c2 , false );
		
		c1.close();
		c2.close();
		tm.rollback();
		assertNotPresent ( key1 );
		assertNotPresent ( key2 );		
	}
	
	public void testNonXADataSourceBean()
	throws Exception
	{
		Connection c = null;
		NonXADataSourceBean bean = null;
		
		//Test if setters and getters work
		bean = new NonXADataSourceBean();
		bean.setConnectionTimeout(12);
		if ( bean.getConnectionTimeout() !=  12) throw new Exception ( "Bean: connection property");
		
		bean.setUniqueResourceName("testName");
		if ( ! "testName".equals ( bean.getUniqueResourceName())) throw new Exception ( "Bean: jndiName property");
		
		String dir = getTemporaryOutputDir() + "/";
		String url = "jdbc:hsqldb:" + dir +  "NonXATestDB";
		bean.setUrl ( url );
		if ( ! url.equals ( bean.getUrl())) throw new Exception ( "Bean: url property");
		
		bean.setUser ( "sa" );
		if ( ! "sa".equals ( bean.getUser())) throw new Exception  ("Bean: user property");
		
		bean.setPoolSize ( 3);
		if (  bean.getPoolSize()!= 3 ) throw new Exception ( "Bean: poolSize property");
		
		bean.setDriverClassName("org.hsqldb.jdbcDriver" );
		if ( !"org.hsqldb.jdbcDriver".equals (bean.getDriverClassName()))	throw new Exception ( "Bean: driver class name");	
		
		bean.setValidatingQuery( "select * from NONXATABLE");
		if ( ! "select * from NONXATABLE".equals ( bean.getValidatingQuery())) throw new Exception( "Bean: query property");
		
	
		assertFalse ( bean.getTestOnBorrow() );
		bean.setTestOnBorrow ( true );
		assertTrue ( bean.getTestOnBorrow() );
		bean.setTestOnBorrow ( false );
		assertFalse ( bean.getTestOnBorrow() );
		

		
		//Test validation
		
		bean.validate();
		
		bean.close();
					
	}
	
	public void testRollbackAfterTimeout()
	throws Exception
	{
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		Connection c = null;
		CompositeTransaction ct = null;
		int key = 0;
		
		ct = ctm.createCompositeTransaction ( 1000 );
        ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
        
		c = testDs.getConnection();
		
		key = insertNextKey ( c ,false );
		
		//sleep till after timeout
		sleep();
		
		//here we are AFTER timeout and rollback, but the
		//THREAD is still mapped to the CT (no subtx commit/rb called)
		
		try {
			//errors due to timeout will only be detected here
			ct.commit();
			failTest ( "commit after timeout");
		}
		catch ( RollbackException normal ){
			//System.out.println ( "OK: Failed to commit after timeout...");
		} 
	
		//force the connection to rollback
		c.getAutoCommit();
		
		assertNotPresent ( key );
	}
	
	public void testCommitAfterPrepare() throws Exception
	{
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		Connection c = null;
		CompositeTransaction ct = null;
		int key = 0;
		Participant p = null;
		
		ct = ctm.createCompositeTransaction ( 1000 );
		//add another participant to force 2PC
		p = new TestParticipant();
		ct.addParticipant (p );
		
		c = testDs.getConnection();
		
		key = insertNextKey ( c ,false );
		
		ct.commit();
		
		assertPresent ( key );	
		
		
	}
	
	public void testRollbackAfterPrepare() throws Exception
	{
		
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		Connection c = null;
		CompositeTransaction ct = null;
		int key = 0;
		Participant p = null;
		
		ct = ctm.createCompositeTransaction ( 1000 );
         ct.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
        
		p = new VoteNoParticipant();
		ct.addParticipant ( p );
		
		key = insertNextKey ( c ,false );
		
		try {
			ct.commit();
		}
		catch ( Exception normal ) {}
		
		assertNotPresent ( key );
	}
	
	public void testReferenceable() throws Exception
	{
		Reference ref = testDs.getReference();
		SerializableObjectFactory jndi = new SerializableObjectFactory();
		NonXADataSourceBean jndiBean = ( NonXADataSourceBean ) 
			jndi.getObjectInstance ( ref , null , null , null );
		assertNotNull ( jndiBean );
		assertEquals ( testDs.getUniqueResourceName() , jndiBean.getUniqueResourceName() );
	}

    /**
     * See bug #20140 
     */
	public void testNonTransactionalMethods() throws Exception
	{
		tm.begin();
		c = testDs.getConnection();
		c.close();
		tm.commit();
		c.hashCode();
	}
	
}
