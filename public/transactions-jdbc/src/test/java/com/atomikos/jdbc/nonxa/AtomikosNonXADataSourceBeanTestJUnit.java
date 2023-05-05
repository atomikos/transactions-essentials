/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.sql.SQLException;

import com.atomikos.jdbc.AtomikosNonXADataSourceBean;
import com.atomikos.jdbc.internal.AtomikosSQLException;

import junit.framework.TestCase;

public class AtomikosNonXADataSourceBeanTestJUnit extends TestCase 
{
	private AtomikosNonXADataSourceBean ds;
	
	protected void setUp() {
		ds = new AtomikosNonXADataSourceBean();
	}
	
	protected void tearDown() {
		if ( ds != null ) ds.close();
	}
	
	public void testDriverClassName() 
	{
		assertNull ( ds.getDriverClassName() );
		String name = "driver";
		ds.setDriverClassName ( name );
		assertEquals ( name , ds.getDriverClassName() );
	}
	
	public void testPassword()
	{
		assertNull ( ds.getPassword() );
		String pw = "secret";
		ds.setPassword( pw );
		assertEquals ( pw , ds.getPassword() );
	}
	
	public void testUrl()
	{
		assertNull ( ds.getUrl() );
		String url = "url";
		ds.setUrl(url);
		assertEquals ( url , ds.getUrl() );
	}
	
	public void testUser()
	{
		assertNull ( ds.getUser() );
		String usr = "user";
		ds.setUser ( usr );
		assertEquals ( usr , ds.getUser() );
		
	}
	
	public void testBorrowConnectionTimeout()
	{
		assertEquals ( 30 , ds.getBorrowConnectionTimeout() );
		ds.setBorrowConnectionTimeout( 14 );
		assertEquals ( 14 , ds.getBorrowConnectionTimeout() );
	}
	
	public void testLoginTimeout () throws SQLException
	{
		assertEquals ( 0 , ds.getLoginTimeout() );
		ds.setLoginTimeout( 5 );
		assertEquals ( 5 , ds.getLoginTimeout() );
	}
	
	public void testMaintenanceInterval()
	{
		assertEquals ( 60 , ds.getMaintenanceInterval() );
		ds.setMaintenanceInterval( 13 );
		assertEquals ( 13 , ds.getMaintenanceInterval() );
	}
	
	public void testMaxIdleTime()
	{
		assertEquals ( 60 , ds.getMaxIdleTime() );
		ds.setMaxIdleTime( 11 );
		assertEquals ( 11 , ds.getMaxIdleTime() );
	}
	
	public void testMaxPoolSize() 
	{
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setMaxPoolSize( 3 );
		assertEquals ( 3 , ds.getMaxPoolSize() );
	}
	
	public void testMinPoolSize() 
	{
		assertEquals ( 1 , ds.getMinPoolSize() );
		ds.setMinPoolSize( 4 );
		assertEquals ( 4 , ds.getMinPoolSize() );
	}
	
	public void testPoolSize()
	{
		assertEquals ( 1 , ds.getMinPoolSize() );
		assertEquals ( 1 , ds.getMaxPoolSize() );
		ds.setPoolSize ( 3 );
		assertEquals ( 3 , ds.getMinPoolSize() );
		assertEquals ( 3 , ds.getMaxPoolSize() );
		
	}
	
	
	public void testTestQuery()
	{
		assertNull ( ds.getTestQuery() );
		String query = "haha";
		ds.setTestQuery ( query );
		assertEquals ( query , ds.getTestQuery() );
	}
	
	public void testUniqueResourceName() 
	{
		assertNull ( ds.getUniqueResourceName() );
		String name = "resource";
		ds.setUniqueResourceName( name );
		assertEquals ( name , ds.getUniqueResourceName()  );
	}
	
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
	
	public void testReadOnly() throws Exception 
	{
		assertFalse ( ds.getReadOnly() );
		ds.setReadOnly ( true );
		assertTrue ( ds.getReadOnly() );
		ds.setReadOnly ( false );
		assertFalse ( ds.getReadOnly() );
	}
	
	public void testDefaultIsolationLevel() throws Exception 
	{	
		assertEquals ( -1 , ds.getDefaultIsolationLevel() );
		ds.setDefaultIsolationLevel( 0 );
		assertEquals ( 0 , ds.getDefaultIsolationLevel() );
	}
	
	public void testLocalTransactionMode() throws Exception {
		assertEquals ( false , ds.getLocalTransactionMode() );
		ds.setLocalTransactionMode(true);
		assertEquals ( true , ds.getLocalTransactionMode() );
		assertEquals ( false , ds.getIgnoreJtaTransactions() );
	}

	public void testIgnoreJtaTransactions() throws Exception {
		assertEquals ( false , ds.getIgnoreJtaTransactions() );
		ds.setIgnoreJtaTransactions(true);
		assertEquals ( true , ds.getIgnoreJtaTransactions() );
		assertEquals ( false , ds.getLocalTransactionMode() );
	}
	
	
	
}
