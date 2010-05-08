package com.atomikos.jdbc;

import java.sql.SQLException;

import javax.naming.NamingException;

import com.atomikos.icatch.HeuristicMessage;

import junit.framework.TestCase;

public class DataSourceBeanTestJUnit extends TestCase {

	private DataSourceBean ds;
	
	protected void setUp() throws Exception {
		super.setUp();
		ds = new DataSourceBean();
		
	}
	
	public void testConnectionPoolSize() {
		ds.setConnectionPoolSize ( "3" );
		assertEquals ( "3" , ds.getConnectionPoolSize() );
	}
	
	public void testConnectionTimeout() 
	{
		ds.setConnectionTimeout ( "30" );
		assertEquals ( "30" , ds.getConnectionTimeout() );
	}
	
	public void testDataSourceName() 
	{
		String name = "name";
		ds.setDataSourceName( name );
		assertEquals ( name , ds.getDataSourceName() );
	}
	
	public void testLoginTimeout() throws SQLException 
	{
		testXADataSource();
		int val = 33;
		ds.setLoginTimeout ( val );
		assertEquals ( val , ds.getLoginTimeout() );
	}

	public void testTestOnBorrow() 
	{
		
		assertEquals ( "false" , ds.getTestOnBorrow() );
		ds.setTestOnBorrow ( "true" );
		assertEquals ( "true" , ds.getTestOnBorrow() );
		ds.setTestOnBorrow ( "false" );
		assertEquals ( "false" , ds.getTestOnBorrow() );
	}

	
	public void testUniqueResourceName() 
	{
		String name = "name";
		ds.setUniqueResourceName ( name );
		assertEquals ( name , ds.getUniqueResourceName() );
	}
	
	public void testValidatingQuery() 
	{
		String query = "query";
		ds.setValidatingQuery ( query );
		assertEquals ( query , ds.getValidatingQuery() );
	}
	
	public void testXADataSource() 
	{
		TestXADataSource xads = new TestXADataSource();
		ds.setXaDataSource ( xads );
		assertEquals ( xads , ds.getXaDataSource() );
	}
	
	public void testXidFormat() 
	{
		ds.setXidFormat ( "oracle" );
		assertEquals ( "oracle" , ds.getXidFormat() );
	}
	
	public void testExclusiveConnectionMode() 
	{
		assertEquals ( "true" , ds.isExclusiveConnectionMode() );
		ds.setExclusiveConnectionMode ( "true" );
		assertEquals ( "true" , ds.isExclusiveConnectionMode() );
		ds.setExclusiveConnectionMode ( "false" );
		assertEquals ( "false" , ds.isExclusiveConnectionMode() );
	}
	
	public void testReference() throws NamingException
	{
		assertNotNull ( ds.getReference() );
	}
	
	public void testClose() throws SQLException
	{
		ds.close();
	}
	
	public void testConnection() throws Exception
	{
		testXADataSource();
		assertNotNull ( ds.getConnection() );
		assertNotNull ( ds.getConnection ( (HeuristicMessage) null ) );
		assertNotNull ( ds.getConnection ( "" ));
	}
}
