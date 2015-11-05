package com.atomikos.jdbc.nonxa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.jdbc.AtomikosSQLException;

public class AtomikosNonXADataSourceBeanTestJUnit
{
	private AtomikosNonXADataSourceBean ds;
	
	@Before
	public void setUp()
	{
		ds = new AtomikosNonXADataSourceBean();
	}
	
	@After
	public void tearDown()
	{
		if ( ds != null ) ds.close();
	}
	
	@Test
	public void testDriverClassName() 
	{
		assertNull ( ds.getDriverClassName() );
		String name = "driver";
		ds.setDriverClassName ( name );
		assertEquals ( name , ds.getDriverClassName() );
	}
	
	@Test
	public void testPassword()
	{
		assertNull ( ds.getPassword() );
		String pw = "secret";
		ds.setPassword( pw );
		assertEquals ( pw , ds.getPassword() );
	}
	
	@Test
	public void testUrl()
	{
		assertNull ( ds.getUrl() );
		String url = "url";
		ds.setUrl(url);
		assertEquals ( url , ds.getUrl() );
	}
	
	@Test
	public void testUser()
	{
		assertNull ( ds.getUser() );
		String usr = "user";
		ds.setUser ( usr );
		assertEquals ( usr , ds.getUser() );
		
	}
	
	@Test
	public void testBorrowConnectionTimeout()
	{
		assertEquals ( 30 , ds.getBorrowConnectionTimeout() );
		ds.setBorrowConnectionTimeout( 14 );
		assertEquals ( 14 , ds.getBorrowConnectionTimeout() );
	}
	
	@Test
	public void testLoginTimeout () throws SQLException
	{
		assertEquals ( 0 , ds.getLoginTimeout() );
		ds.setLoginTimeout( 5 );
		assertEquals ( 5 , ds.getLoginTimeout() );
	}
	
	@Test
	public void testMaintenanceInterval()
	{
		assertEquals ( 60 , ds.getMaintenanceInterval() );
		ds.setMaintenanceInterval( 13 );
		assertEquals ( 13 , ds.getMaintenanceInterval() );
	}
	
	@Test
	public void testMaxIdleTime()
	{
		assertEquals ( 60 , ds.getMaxIdleTime() );
		ds.setMaxIdleTime( 11 );
		assertEquals ( 11 , ds.getMaxIdleTime() );
	}
	
	@Test
	public void testMaxPoolSize() 
	{
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setMaxPoolSize( 3 );
		assertEquals ( 3 , ds.getMaxPoolSize() );
	}
	
	@Test
	public void testMinPoolSize() 
	{
		assertEquals ( 1 , ds.getMinPoolSize() );
		ds.setMinPoolSize( 4 );
		assertEquals ( 4 , ds.getMinPoolSize() );
	}
	
	@Test
	public void testPoolSize()
	{
		assertEquals ( 1 , ds.getMinPoolSize() );
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setPoolSize ( 3 );
		assertEquals ( 3 , ds.getMinPoolSize() );
		assertEquals ( 3 , ds.getMaxPoolSize() );
		
	}
	
	@Test
	public void testReapTimeout()
	{
		assertEquals ( 0 , ds.getReapTimeout() );
		ds.setReapTimeout( 33 );
		assertEquals ( 33 , ds.getReapTimeout() );
	}
	
	@Test
	public void testTestQuery()
	{
		assertNull ( ds.getTestQuery() );
		String query = "haha";
		ds.setTestQuery ( query );
		assertEquals ( query , ds.getTestQuery() );
	}
	
	@Test
	public void testUniqueResourceName() 
	{
		assertNull ( ds.getUniqueResourceName() );
		String name = "resource";
		ds.setUniqueResourceName( name );
		assertEquals ( name , ds.getUniqueResourceName()  );
	}
	
	@Test
	public void testInitWithDriverClassNotFoundThrowsMeaningfulException () throws SQLException
	{
		ds.setUniqueResourceName( "test" );
		ds.setDriverClassName ( "com.example.NonExistingClass" );
		try {
			ds.getConnection();
			fail ( "getConnection works without existing driver class" );
		} catch ( AtomikosSQLException ok ) {
			ok.printStackTrace();
			String expectedMsg = "Driver class not found: 'com.example.NonExistingClass' - please make sure the spelling is correct.";
            assertEquals ( expectedMsg , ok.getMessage() );
		}		
	}
	
	@Test
	public void testInitWithInvalidDriverClassThrowsMeaningfulException () throws SQLException
	{
		ds.setUniqueResourceName( "test" );
		ds.setDriverClassName ( "java.lang.String" );
		try {
			ds.getConnection();
			fail ( "getConnection works with invalid driver class" );
		} catch ( AtomikosSQLException ok ) {
			ok.printStackTrace();
			String expectedMsg = "Driver class 'java.lang.String' does not seem to be a valid JDBC driver - please check the spelling and verify your JDBC vendor's documentation";
            assertEquals ( expectedMsg , ok.getMessage() );
		}		
	}
	
	@Test
	public void testReadOnly() throws Exception 
	{
		assertFalse ( ds.getReadOnly() );
		ds.setReadOnly ( true );
		assertTrue ( ds.getReadOnly() );
		ds.setReadOnly ( false );
		assertFalse ( ds.getReadOnly() );
	}
	
	@Test
	public void testDefaultIsolationLevel() throws Exception 
	{	
		assertEquals ( -1 , ds.getDefaultIsolationLevel() );
		ds.setDefaultIsolationLevel( 0 );
		assertEquals ( 0 , ds.getDefaultIsolationLevel() );
	}

	@Test
	public void testPrintPropertiesSkipsPassword() throws Exception
	{
		ds.setUser( "johndoe" );
		ds.setPassword( "secret" );
		String s = ds.printProperties();
		assertTrue( s.contains( "user=johndoe" ) );
		assertFalse( s.contains( "password=secret" ) );
	}

}
