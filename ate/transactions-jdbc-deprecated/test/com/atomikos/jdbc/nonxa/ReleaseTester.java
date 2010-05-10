package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.transaction.TransactionManager;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.VoteNoParticipant;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */

public class ReleaseTester
{
	private static DriverManagerDataSource ds = null;
	
	private static NonXADataSourceBean testDs = null;

	private static UserTransactionService  uts = null;

	private static int lastKey = 0;
	
	private static int getNextKey()
	{
		return lastKey++;
	}
	
	private static int insertNextKey ( Connection c , boolean simulateError )
	throws Exception
	{
		int ret = getNextKey();
		PreparedStatement s = c.prepareStatement ( "insert into NONXATABLE values ( ? )" );
		if ( ! simulateError ) s.setInt ( 1 , ret );
		else s.setString ( 1 , "StringToCauseError"); 
		s.executeUpdate();
		s.close();
		
		return ret;
	}

	private static void setup() throws Exception
	{
		//todo setup datasource
		ds = new DriverManagerDataSource();
		ds.setUser ( "sa");
		ds.setPassword ( "");
		ds.setUrl ( "jdbc:HypersonicSQL:NonXATestDB" );
		ds.setDriverClassName ( "org.hsql.jdbcDriver" );
		
		//printThreadInfo();
		
		testDs = new NonXADataSourceBean();

		testDs.setUniqueResourceName ( "testDS" );
		testDs.setUrl ( "jdbc:HypersonicSQL:NonXATestDB" );
		testDs.setDriverClassName ( "org.hsql.jdbcDriver" );
		testDs.setUser("sa");
		testDs.setPassword("");
		testDs.setPoolSize(1);
		testDs.setConnectionTimeout(15);
	
		//printThreadInfo();
		
		//testDs = new NonXADataSourceImp ( ds , "TestDS" , "sa" , "" , 1 , 15 );
		
		//todo create test table if necessary
		
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
		
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		uts.init ( info );
		
		//printThreadInfo();
		
	}
	
	private static void shutdown() throws Exception
	{
		//todo delete all from test table
		if ( uts != null ) uts.shutdown ( true );
		if ( testDs != null ) testDs.close();
		
	}
	
	private static boolean isInDatabase ( int key )
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
	
	private static void assertPresent ( int key )
	throws Exception
	{
		if ( ! isInDatabase ( key ) ) throw new Exception ( "Missing row with key: " + key );
	}
	
	private static void assertNotPresent ( int key )
	throws Exception
	{
		if ( isInDatabase ( key ) ) throw new Exception ( "Unexpected row with key: " + key );
	}
	
	private static void testBasic() throws Exception
	{
		
		//
		//BASIC-1: assert that rollback works
		//
		
		TransactionManager tm = uts.getTransactionManager();
		
		
		tm.begin();
		Connection c = testDs.getConnection();
		int key = insertNextKey ( c , false );
		c.close();
		
		tm.rollback();
		
		assertNotPresent ( key );
		
		//
		//BASIC-2: assert that commit works
		//
		tm.begin();
		c = testDs.getConnection();
		key = insertNextKey ( c , false );
		c.close();
		
		tm.commit();
		assertPresent ( key );
		
		//
		//BASIC-3: assert that connection txs not allowed in managed use
		//
		
		tm.begin();
		c = testDs.getConnection();
		try {
			c.rollback();
			throw new Exception ( "Connection.rollback works in a tx?");
		}
		catch ( SQLException normal ) {}
		tm.rollback();
		
	}

	private static void testLateRegistration() throws Exception
	{
		//
		//LATE-1: rollback
		//
		
		Connection c = null;
		Statement s = null;
		int key = 0;
		TransactionManager tm = uts.getTransactionManager();
		
		c = testDs.getConnection();
		tm.begin();
		key = insertNextKey ( c , false  );
		tm.rollback();
		c.close();
		assertNotPresent ( key );
		
		//
		//LATE-2: commit
		//
		c = testDs.getConnection();
		tm.begin();
		key = insertNextKey ( c , false );
		tm.commit();
		c.close();
		assertPresent ( key );		
		
	}
	
	//Test reuse of connection for different transactions of same thread
	private static void testConnectionReuse() throws Exception
	{
		TransactionManager tm = uts.getTransactionManager();	
		Connection c = null;
		int key = 0;
		
		c = testDs.getConnection();
		
		tm.begin();
		key = insertNextKey ( c , false );
		tm.rollback();
		assertNotPresent ( key );
		
		tm.begin();
		key = insertNextKey ( c , false);
		tm.commit();
		assertPresent ( key );
		
		tm.begin();
		key = insertNextKey ( c , false );
		tm.rollback();
		assertNotPresent ( key );
		
		c.close();
		
		
			
	}
	
	//test connection use without transactions
	private static void testNoTransaction() 
	throws Exception
	{
		Connection c = null;
		int key = 0;
		
		c = testDs.getConnection();
		c.setAutoCommit ( false );
		
		key = insertNextKey ( c , false );
		c.rollback();
		assertNotPresent ( key );
		
		key = insertNextKey ( c , false );
		c.commit();
		assertPresent ( key );
		
		c.close();
		
	}
	
	//assert that connections can not be shared with subtxs
	private static void testNestedTransactions() throws Exception
	{
		Connection c = null;
		int key1 = 0, key2 = 0, key3 = 0;
		TransactionManager tm = uts.getTransactionManager();
		
		c = testDs.getConnection();
		
		//NESTED-1: assert that rolled back subtx work does not appear in committed parent work
		
		tm.begin();
		key1 = insertNextKey ( c , false );
		tm.begin();
		try {
			key2 = insertNextKey ( c , false );
			throw new Exception ( "Connection can be shared with subtx?");
		}
		catch ( SQLException ok ) {}
		tm.rollback();
		key3 = insertNextKey ( c , false );
		tm.commit();
		assertPresent ( key1  );
		assertNotPresent ( key2 );
		assertPresent ( key3 );
		
		c.close();
	}
	
	//assert that multiple gets and close calls work
	private static void testMultipleGets() throws Exception
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
	
	private static void testNonXADataSourceBean()
	throws Exception
	{
		int key = 0;
		Connection c = null;
		NonXADataSourceBean bean = null;
		
		//Test if setters and getters work
		bean = new NonXADataSourceBean();
		bean.setConnectionTimeout(12);
		if ( bean.getConnectionTimeout() !=  12) throw new Exception ( "Bean: connection property");
		
		bean.setUniqueResourceName("testName");
		if ( ! "testName".equals ( bean.getUniqueResourceName())) throw new Exception ( "Bean: jndiName property");
		
		bean.setUrl ( "jdbc:HypersonicSQL:NonXATestDB");
		if ( ! "jdbc:HypersonicSQL:NonXATestDB".equals ( bean.getUrl())) throw new Exception ( "Bean: url property");
		
		bean.setUser ( "sa" );
		if ( ! "sa".equals ( bean.getUser())) throw new Exception  ("Bean: user property");
		
		bean.setPoolSize ( 3);
		if (  bean.getPoolSize()!= 3 ) throw new Exception ( "Bean: poolSize property");
		
		bean.setDriverClassName("org.hsql.jdbcDriver" );
		if ( !"org.hsql.jdbcDriver".equals (bean.getDriverClassName()))	throw new Exception ( "Bean: driver class name");	
		
		bean.setValidatingQuery( "select * from NONXATABLE");
		if ( ! "select * from NONXATABLE".equals ( bean.getValidatingQuery())) throw new Exception( "Bean: query property");
		
		
		//Test validation
		
		bean.validate();
		
		bean.close();
				
		
	}
	
	//test the effect of transaction rollback due to timeout
	private static void testTimeout()
	throws Exception
	{
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		Connection c = null;
		CompositeTransaction ct = null;
		int key = 0;
		
		ct = ctm.createCompositeTransaction ( 1000 );
		
		c = testDs.getConnection();
		
		key = insertNextKey ( c ,false );
		
		//sleep till after timeout
		Thread.sleep ( 2000 );
		
		//here we are AFTER timeout and rollback, but the
		//THREAD is still mapped to the CT (no subtx commit/rb called)
		
		try {
			//errors due to timeout will only be detected here
			ct.getTransactionControl().getTerminator().commit();
			throw new Exception ( "commit after timeout");
		}
		catch ( IllegalStateException normal ){
			//System.out.println ( "OK: Failed to commit after timeout...");
		} 
	
		//force the connection to rollback
		c.getAutoCommit();
		
		assertNotPresent ( key );
		
		
		
	}
	
	//test whether full 2PC works
	private static void test2PC()
	throws Exception
	{
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		Connection c = null;
		CompositeTransaction ct = null;
		int key = 0;
		Participant p = null;
		
		//2PC-1: test normal commit
		
		ct = ctm.createCompositeTransaction ( 1000 );
		//add another participant to force 2PC
		p = new TestParticipant();
		ct.addParticipant (p );
		
		c = testDs.getConnection();
		
		key = insertNextKey ( c ,false );
		
		ct.commit();
		
		assertPresent ( key );	
		
		//2PC - 2: test rollback after vote-no
		ct = ctm.createCompositeTransaction ( 1000 );
		p = new VoteNoParticipant();
		ct.addParticipant ( p );
		
		key = insertNextKey ( c ,false );
		
		try {
			ct.commit();
		}
		catch ( Exception normal ) {}
		
		assertNotPresent ( key );
		
	}

	private static void printThreadInfo()
	throws Exception
	{
		
		Thread[] threads = new Thread[100];
		int num = Thread.enumerate(threads);
		for ( int i = 0 ; i < num ; i++ ) {
			Thread t = threads[i];
			System.out.println ( "Thread: " + t.getName() );
			
		}
		System.out.println ( "-------------------------");
		System.out.println();
	}


	private static void test()
	throws Exception
	{
		
		try {
	
			//printThreadInfo();
			setup();
			//printThreadInfo();
			testNonXADataSourceBean();
			//printThreadInfo();
			testBasic();
			//printThreadInfo();
			testLateRegistration();
			testConnectionReuse();
			testNoTransaction();
			testNestedTransactions();
			testMultipleGets();
			testTimeout();
			test2PC();
			
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		finally {
			shutdown();
			//printThreadInfo();
			System.out.println ( "Done.");
		}
		System.exit ( 0 );
		
	}
	
	

    public static void main(String[] args) throws Exception
    {
    	test();
    }
}
