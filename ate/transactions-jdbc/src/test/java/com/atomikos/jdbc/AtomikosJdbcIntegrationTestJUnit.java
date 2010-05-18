package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.transaction.RollbackException;
import javax.transaction.Status;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;

public class AtomikosJdbcIntegrationTestJUnit extends
		TransactionServiceTestCase {

	private UserTransactionServiceImp uts;
	private TSInitInfo info;
	private  AtomikosDataSourceBean ds;
	private TransactionManagerImp tm = null;


	public AtomikosJdbcIntegrationTestJUnit (String name) {
		super(name);
	}
	
	

	protected void setUp() {
		super.setUp();
		
		uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        Properties properties = info.getProperties();    
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "AtomikosJdbcIntegrationTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath());
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.init ( info );
        
        ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setReapTimeout(10);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		ds.setXaProperties( new Properties() );
		tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager();
		
	}
	
	protected void tearDown() {
		uts.shutdown(true);
		ds.close();
		super.tearDown();
		
	}
	

	public void testConnectionRecyclingReturnsSamePooledConnectionAfterClose() throws Exception
	{

		assertEquals ( "max pool size must be 1 for this test to work" , 1 , ds.getMaxPoolSize() );
		ds.init();
		
		tm.setTransactionTimeout( 5 );
		tm.begin();
		Connection c = ds.getConnection();
		assertEquals ( "pool size should be 0 after getting the only connection available" , 0 , ds.poolAvailableSize() );
		
		//important: create statement to trigger enlistment!
		c.createStatement();
		
		//important: close handle to enable recycling!
		c.close();
		assertEquals ( "pool size should be 0: the closed connection is not pooled but waiting for 2PC" , 0 , ds.poolAvailableSize() );
		//now, we should get the same connection again, without blocking on the pool of size now 0!
		c = ds.getConnection();		
		tm.commit();
		
		try {
			ds.getConnection();
			fail ( "getting a connection should not work: the only pooled connection available is not yet closed!" );
		} catch ( AtomikosSQLException ok ) {
			
		}
		
		c.close();
		
		ds.close();
	}
	
	public void testConnectionProxyReuseAfterTransactionTimeoutNotAllowed() throws Exception 
	{
		//test for bug: case 27857
		
		ds.init();
		tm.setTransactionTimeout ( 1 );
		tm.begin();
		Connection c = ds.getConnection();
		c.createStatement();
		//wait for tx timeout
		Thread.currentThread().sleep ( 2000 );
		assertNotNull ( tm.getTransaction() );
		assertEquals ( tm.getTransaction().getStatus() , Status.STATUS_MARKED_ROLLBACK );
		
		try {
			c.createStatement();
			//reuse of connection NOT allowed since application did not detect timeout
			fail ( "Illegal reuse of connection proxy after transaction timeout" );
		} catch ( AtomikosSQLException ok ) {
			String msg = ok.getMessage();
			String expected = "Transaction is marked for rollback only or has timed out";
			assertEquals ( expected , msg );
		}
		
	}
	
	public void testConnectionProxyReuseAfterTransactionRollbackAllowed() throws Exception 
	{
		//test to distinguish normal use from bug: case 27857
		ds.init();
		tm.setTransactionTimeout ( 1 );
		tm.begin();
		Connection c = ds.getConnection();
		c.createStatement();
	
		tm.rollback();
		assertNull ( tm.getTransaction() );
		//reuse of connection should be allowed since the application knows about rollback
		c.createStatement();
			
		
	}
	
	public void testStatementClosedAfterTransactionTimeout() throws Exception 
	{
		//test for bug 29708: avoid that statement resorts to autoCommit mode 
		ds.init();
		tm.setTransactionTimeout ( 1 );
		tm.begin();
		Connection c = ds.getConnection();
		Statement s = c.createStatement();
		//wait for tx timeout
		Thread.sleep ( 3000 );
		try {
			s.execute ( "" );
			//tx has timed out -> SQL should fail!
			fail ( "statement not closed after tx timeout" );
		} catch ( SQLException ok ) {}
		
		try {
			tm.commit();
		} catch ( RollbackException ok ) {
			assertEquals ( "Transaction set to rollback only" , ok.getMessage() );
		}
		
	}
	
}
