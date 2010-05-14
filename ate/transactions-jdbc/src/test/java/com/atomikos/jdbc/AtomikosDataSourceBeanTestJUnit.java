package com.atomikos.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NameNotFoundException;
import javax.naming.Reference;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.util.IntraVmObjectFactory;
import com.atomikos.util.IntraVmObjectRegistry;

public class AtomikosDataSourceBeanTestJUnit extends TestCase 
{

	private UserTransactionServiceImp uts;
	private AtomikosDataSourceBean adsb;
	
	protected void setUp() throws Exception {
		//start the transaction service
		uts = new UserTransactionServiceImp();
		uts.init(uts.createTSInitInfo());

		adsb = new AtomikosDataSourceBean();
		Properties p = new Properties();
		p.setProperty ( "lastUser" , "aLastUser" );
		p.setProperty( "lastPassword", "aLastPassword" );
		adsb.setXaProperties(p);
		adsb.setUniqueResourceName (getName() );
	}
	
	protected void tearDown() {
		TestXADataSource.simulateDbDown = false;
		adsb.close();
		//shutdown transaction service
		uts.shutdown ( true );
	}
	
	public void testCreateSuccess() throws Exception {
		
		
		adsb.setUniqueResourceName(getName());
		adsb.setXaDataSourceClassName(TestXADataSource.class.getName());
		
		adsb.init();
		
		try {
			TestXADataSource testXads = (TestXADataSource) adsb.getXaDataSource();
			
			assertEquals("aLastUser", testXads.getLastUser());
			assertEquals("aLastPassword", testXads.getLastPassword());
		} finally {
			adsb.close();
		}
	}
	
	public void testCreateWithNonExistentProperty() throws Exception {
		
		adsb.setUniqueResourceName(getName());
		adsb.setXaDataSourceClassName(TestXADataSource.class.getName());
		adsb.getXaProperties().setProperty("doesNotExist","someValue");
		
		try {
			adsb.init();
			fail("should have failed; doesNotExist property does not exist");
		} catch (SQLException ex) {
			assertTrue ( ex.getMessage().startsWith ( "Cannot initialize AtomikosDataSourceBean" ) );
			assertEquals("no writeable property 'doesNotExist' in class 'com.atomikos.jdbc.TestXADataSource'", ex.getCause().getMessage());
		} finally {
			adsb.close();
		}
	}
	
	public void testGetConnection() throws Exception {
		adsb.setUniqueResourceName(getName());
		adsb.setXaDataSourceClassName(TestXADataSource.class.getName());
		adsb.setMinPoolSize(1);
		adsb.init();
		
		Connection c = adsb.getConnection();
		c.close();
		
		adsb.close();
	}
	
	public void testPoolSizeWith2Pc() throws Exception {
		UserTransaction ut = new com.atomikos.icatch.jta.UserTransactionImp();
		
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(1);
		ds.init();
		
		ut.begin();
		
		assertEquals(1, ds.poolAvailableSize());
		Connection c1 = ds.getConnection();
		assertEquals(0, ds.poolAvailableSize());
		c1.createStatement(); // performs enlistment
		c1.close();
		assertEquals(0, ds.poolAvailableSize());
		ut.commit();
		assertEquals(1, ds.poolAvailableSize());
	
		ds.close();
	}
	
	public void testPoolSizeWithout2Pc() throws Exception {
		UserTransaction ut = new com.atomikos.icatch.jta.UserTransactionImp();
		
		AtomikosDataSourceBean ds = adsb;
		try {
			ds.setUniqueResourceName(getName());
			ds.setXaDataSourceClassName(TestXADataSource.class.getName());
			ds.setMinPoolSize(1);
			ds.setMaxPoolSize(1);
			ds.init();
			
			ut.begin();
			
			assertEquals(1, ds.poolAvailableSize());
			Connection c1 = ds.getConnection();
			assertEquals(0, ds.poolAvailableSize());
			// do not enlist
			c1.close();
			assertEquals(1, ds.poolAvailableSize());
			ut.commit();
			assertEquals(1, ds.poolAvailableSize());
		} finally {
			ds.close();
		}
	}
	
	public void testAutocommit() throws Exception {
		UserTransaction ut = new com.atomikos.icatch.jta.UserTransactionImp();
		
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(1);
		ds.init();
		
		Connection c1 = ds.getConnection();
		assertTrue(c1.getAutoCommit());
		ut.begin();
		assertTrue(c1.getAutoCommit());
		
		c1.createStatement();
		assertFalse(c1.getAutoCommit());
		
		try {
			c1.setAutoCommit(true);
			fail("should not be able to set autocommit during global transaction");
		} catch (SQLException ex) {
			//expected
		}
		assertFalse(c1.getAutoCommit());
		ut.commit();
		
		assertTrue(c1.getAutoCommit());
		c1.setAutoCommit(false);
		c1.close();
		
//		c1 = ds.getConnection();
//		assertTrue(c1.getAutoCommit());
//		c1.close();
	
		ds.close();
	}
	
	public void testClose() throws Exception {
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(1);
		ds.init();
		
		assertEquals(1, ds.poolAvailableSize());
		Connection c1 = ds.getConnection();
		assertEquals(0, ds.poolAvailableSize());
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		
		try {
			c1.createStatement();
			fail("createStatement() should throw an exception on a closed connection");
		} catch (SQLException ex) {
			// expected
		}
		XATransactionalResource res = (XATransactionalResource) Configuration.getResource ( ds.getUniqueResourceName() );
		assertNotNull ( res );
		assertFalse ( res.isClosed() );
		ds.close();
		//test for case 26005
		assertTrue ( res.isClosed() );
	}
	
	public void testGrow() throws Exception {
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.init();
		
		assertEquals(0, ds.poolAvailableSize());
		
		Connection c1 = ds.getConnection();
		assertEquals(0, ds.poolAvailableSize());
		
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		
		ds.close();
	}
	
	public void testShrink() throws Exception {
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.init();
		
		assertEquals(0, ds.poolAvailableSize());
		
		Connection c1 = ds.getConnection();
		assertEquals(0, ds.poolAvailableSize());
		
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		
		Thread.sleep(2000);
		assertEquals(0, ds.poolAvailableSize());
		
		ds.close();
	}
	
	
	public void testReap() throws Exception {
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(1);
		ds.setReapTimeout(1);
		ds.setMaintenanceInterval(1);
		ds.init();
		
		assertEquals(1, ds.poolAvailableSize());
		
		Connection c1 = ds.getConnection();
		assertEquals(0, ds.poolAvailableSize());
		
		Thread.sleep(3000); // reap
		
		//assertTrue(c1.isClosed());
		
		assertEquals(1, ds.poolAvailableSize());
		
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		
		ds.close();
	}
	
	public void testResourceNameUniqueness() throws Exception {
		AtomikosDataSourceBean ds1 = adsb;
		ds1.setUniqueResourceName(getName());
		ds1.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds1.init();

		try {
			AtomikosDataSourceBean ds2 = new AtomikosDataSourceBean();
			ds2.setUniqueResourceName(getName());
			ds2.setXaDataSourceClassName(TestXADataSource.class.getName());
			Properties p = new Properties();
			p.setProperty ( "lastUser" , "aLastUser" );
			p.setProperty( "lastPassword", "aLastPassword" );
			ds2.setXaProperties(p);
			ds2.init();
			fail("test should have failed as duplicate resource names were used");
		} catch (Exception ex) {
			assertEquals("Another resource already exists with name "+ getName() + " - pick a different name", ex.getCause().getMessage());
		} finally {
			ds1.close();
		}
	}
	

	
	public void testGrowShrinkGrowShrink() throws Exception {
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName(getName());
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.init();
		
		assertEquals(0, ds.poolAvailableSize());
		Connection c1 = ds.getConnection(); // grow
		assertEquals(0, ds.poolAvailableSize());
		
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		
		Thread.sleep(3000); // shrink
		assertEquals(0, ds.poolAvailableSize());
		
		c1 = ds.getConnection(); // grow
		assertEquals(0, ds.poolAvailableSize());
		
		c1.close();
		assertEquals(1, ds.poolAvailableSize());
		Thread.sleep(2000); // shrink
		assertEquals(0, ds.poolAvailableSize());
		
		ds.close();
	}

	public void testSerializable() throws Exception
	{
		final int MAX_SIZE = 5;

		adsb.setMaxPoolSize ( MAX_SIZE );
		Properties props = new Properties();
		adsb.setXaProperties ( props );
		ByteArrayOutputStream bout = new ByteArrayOutputStream ();
		ObjectOutputStream out = new ObjectOutputStream ( bout );
		out.writeObject ( adsb );
		out.close();
		
		ObjectInputStream in = new ObjectInputStream ( new ByteArrayInputStream ( bout.toByteArray() ));
		AtomikosDataSourceBean bean = ( AtomikosDataSourceBean ) in.readObject();
		in.close();
		
		assertEquals ( MAX_SIZE , bean.getMaxPoolSize());
		assertNotNull ( bean.getXaProperties() );
	}
	
	public void testDataSourceSetters() throws Exception
	{
		adsb.setLoginTimeout(3);
		assertEquals ( 3, adsb.getLoginTimeout() );
		Writer w = new FileWriter ( "test" );
		PrintWriter pw = new PrintWriter(  w ); 
		adsb.setLogWriter(pw );
		assertEquals ( pw , adsb.getLogWriter() );
	}
	
	public void testReferenceable() throws Exception
	{
		adsb.setUniqueResourceName( getName() );
		adsb.setXaDataSourceClassName(TestXADataSource.class.getName());
		adsb.setMinPoolSize(0);
		adsb.setMaxPoolSize(1);
		adsb.setMaxIdleTime(1);
		adsb.setMaintenanceInterval(1);
		
		Reference ref = adsb.getReference();
		assertNotNull ( ref );
		IntraVmObjectFactory f = new IntraVmObjectFactory();
		Object res = null;
		
		res = f.getObjectInstance ( ref , null , null , null );
		//assert that init has been called by JNDI factory
		assertNotNull ( IntraVmObjectRegistry.getResource( adsb.getUniqueResourceName() ) );
		res = f.getObjectInstance ( ref , null , null , null );
		assertNotNull ( res );
		AbstractDataSourceBean resBean = ( AbstractDataSourceBean ) res;
		assertEquals ( adsb , resBean );
	}
	
	public void testResourceAddedForRecovery() throws Exception
	{
		//see case 22672 in FB
		String name = getName();
		assertNull ( Configuration.getResource ( name ) );
		adsb.setUniqueResourceName ( name );
		adsb.setXaDataSourceClassName(TestXADataSource.class.getName());
		adsb.init();
		assertNotNull ( "Resource not added for recovery during init of datasource" , Configuration.getResource ( name ) );
		adsb.close();
		assertNull ( "Resource not removed on close of datasource" , Configuration.getResource ( name ) );
	}
	
	public void testConnectionProxyPreservesDriverSQLException() throws Exception
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.init();
		
		Connection c = ds.getConnection();
		try {
			//calling native SQL will generate an exception in the mock classes
			//and this exception is similar to what a driver would do if local txs fail
			c.nativeSQL ( "some SQL that generates an exception" );
			fail ( "no exception where one was expected?" );
		} catch ( SQLException ok ) {
			String expectedMsg = TestConnection.SQL_ERROR_MESSAGE;
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		c.close();
		ds.close();
	}
	
	public void testUsingClosedConnectionNotAllowed() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.init();
		
		Connection c = ds.getConnection();
		assertFalse ( c.isClosed() ); 
		c.close();
		assertTrue ( c.isClosed() );
		try {
			c.createStatement();
			fail ( "createStatement works on closed connection?" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "Connection was already closed - calling createStatement is no longer allowed!";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		
		//see JDBC spec: closing again should be allowed
		c.close();
		
		ds.close();
	}
	
	public void testInitWithNullXaProperties() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.setXaProperties ( null );
		try {
			ds.init();
			fail ( "init works if no xaProperties set" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "Property 'xaProperties' cannot be null";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithNullXaDataSourceClassName() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.setXaDataSourceClassName ( null );
		try {
			ds.init();
			fail ( "init works if no xaDataSourceClassName set" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "Property 'xaDataSourceClassName' cannot be null";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithNullUniqueResourceName() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		ds.setUniqueResourceName ( null );
		try {
			ds.init();
			fail ( "init works if no uniqueResourceName set" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "Property 'uniqueResourceName' cannot be null";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithXaDataSourceClassNotFound() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName( "com.example.NonExistingClass" );
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		try {
			ds.init();
			fail ( "init works if no xa driver class found" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "The class 'com.example.NonExistingClass' specified by property 'xaDataSourceClassName' could not be found in the classpath. " +
					"Please make sure the spelling is correct, and that the required jar(s) are in the classpath.";
			 
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithInvalidXaDataSource() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName( "java.lang.String" );
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setMaintenanceInterval(1);
		try {
			ds.init();
			fail ( "init works for invalid XADataSource class" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "The class 'java.lang.String' specified by property 'xaDataSourceClassName' does not implement the required interface javax.jdbc.XADataSource. Please make sure the spelling is correct, and check your JDBC driver vendor's documentation." ;
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testXaProperties() 
	{
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		assertTrue ( ds.getXaProperties().isEmpty() );
		Properties props = new Properties();
		ds.setXaProperties ( props );
		assertSame ( props , ds.getXaProperties() );
	}
	
	public void testXaDataSourceClassName() 
	{
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		assertNull ( ds.getXaDataSourceClassName() );
		String name = "something";
		ds.setXaDataSourceClassName ( name );
		assertEquals ( name , ds.getXaDataSourceClassName() );
	}
	
	public void testLocalTransactionMode() 
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertTrue ( ds.getLocalTransactionMode() );
	}
	
	public void testMinPoolSize()
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 1 , ds.getMinPoolSize() );
		ds.setMinPoolSize( 2 );
		assertEquals ( 2 , ds.getMinPoolSize() );
	}
	
	public void testMaxPoolSize()
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setMaxPoolSize( 2 );
		assertEquals ( 2 , ds.getMaxPoolSize() );
	}
	
	public void testPoolSize()
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 1 , ds.getMinPoolSize() );
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setPoolSize( 2 );
		assertEquals ( 2 , ds.getMinPoolSize() );
		assertEquals ( 2 , ds.getMaxPoolSize() );
	}
	
	public void testBorrowConnectionTimeout() 
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 30 , ds.getBorrowConnectionTimeout() );
		int timeout = 45;
		ds.setBorrowConnectionTimeout( timeout );
		assertEquals ( timeout , ds.getBorrowConnectionTimeout() );
	}
	
	public void testReapTimeout()
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 0 , ds.getReapTimeout() );
		ds.setReapTimeout( 100 );
		assertEquals ( 100 , ds.getReapTimeout() );
	}
	
	public void testMaintenanceInterval() 
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 60 , ds.getMaintenanceInterval() );
		ds.setMaintenanceInterval( 12 );
		assertEquals ( 12 , ds.getMaintenanceInterval() );
	}
	
	public void testMaxIdleTime() 
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 60 , ds.getMaintenanceInterval() );
		ds.setMaxIdleTime ( 12 );
		assertEquals ( 12 , ds.getMaxIdleTime() );
	}
	
	public void testTestQuery ()
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertNull ( ds.getTestQuery() );
		String q = "test";
		ds.setTestQuery ( q );
		assertEquals ( q , ds.getTestQuery() );
	}
	
	public void testLoginTimeout() throws Exception
	{
		AbstractDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( 0 , ds.getLoginTimeout() );
		ds.setLoginTimeout ( 11 );
		assertEquals ( 11 , ds.getLoginTimeout() );
	}
	
	public void testMeaningfulExceptionIfMaxPoolSizeReached() throws Exception 
	{
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		ds.init();
		
		assertTrue ( ds.getMaxPoolSize() == 1 );
		Connection c = ds.getConnection();
		//get second connection and block
		try {
			ds.getConnection();
			fail ( "gotten connection when max poolsize reached?" );
		} catch ( AtomikosSQLException ok ) {
			String expectedMsg = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		
		ds.close();
	}
	
	public void testInitWorksIfDatabaseDown() throws Exception
	{
		//test for case 26380
		AtomikosDataSourceBean ds = adsb;
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(1);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		TestXADataSource.simulateDbDown = true;
		
		//init should not fail!
		ds.init();
		
		//try get a connection
		try {
			ds.getConnection();
			fail ( "got connection if DB down?" );
		} catch ( SQLException ok ) {
			assertEquals ( "Failed to grow the connection pool" , ok.getMessage() );
		}
		
		TestXADataSource.simulateDbDown = false;
		ds.getConnection();
		
	}
	
	public void testDefaultIsolationLevel() throws Exception 
	{
		//test for case 32251
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( -1 , ds.getDefaultIsolationLevel() );
		ds.setDefaultIsolationLevel ( 0 );
		assertEquals ( 0 , ds.getDefaultIsolationLevel() );
		
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		ds.init();
		
		Connection c = ds.getConnection();
		assertEquals ( 0 , c.getTransactionIsolation() );
		ds.close();
	}
	
	public void testUnsupportedDefaultIsolationLevelThrowsException() throws Exception 
	{
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setDefaultIsolationLevel ( TestConnection.UNSUPPORTED_ISOLATION_LEVEL );
		assertEquals ( TestConnection.UNSUPPORTED_ISOLATION_LEVEL , ds.getDefaultIsolationLevel() );
		
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		ds.init();
		
		try {
			Connection c = ds.getConnection();
			fail ( "getConnection works with unsupported isolation level?" );
		} catch ( SQLException ok ) {
			String expectedMsg = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.";
			assertEquals ( expectedMsg , ok.getMessage() );
			ok.printStackTrace();
		}
		ds.close();
	}
	
	public void testWithDefaultIsolationLevelUnset() throws Exception 
	{
		//test for case 32251
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		assertEquals ( -1 , ds.getDefaultIsolationLevel() );
		
		ds.setUniqueResourceName("mockResourceName");
		ds.setXaDataSourceClassName(TestXADataSource.class.getName());
		ds.setMinPoolSize(0);
		ds.setMaxPoolSize(1);
		ds.setMaxIdleTime(1);
		ds.setBorrowConnectionTimeout(1);
		ds.setMaintenanceInterval( ds.getBorrowConnectionTimeout() * 10 );
		ds.init();
		
		Connection c = ds.getConnection();
		assertEquals ( TestConnection.DEFAULT_ISOLATION_LEVEL , c.getTransactionIsolation() );
		ds.close();
	}
	
}
