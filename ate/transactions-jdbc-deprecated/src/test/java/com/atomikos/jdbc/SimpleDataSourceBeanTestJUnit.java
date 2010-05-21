package com.atomikos.jdbc;

import java.sql.SQLException;

import com.atomikos.icatch.system.Configuration;

import junit.framework.TestCase;

public class SimpleDataSourceBeanTestJUnit extends TestCase {

	private SimpleDataSourceBean bean;
	
	protected void setUp() throws Exception {
		super.setUp();
		bean = new SimpleDataSourceBean();
		bean.setXaDataSourceClassName ( "com.atomikos.jdbc.TestXADataSource" );
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if ( bean != null ) bean.close();
	}
	
	public void testInit() throws SQLException
	{
		bean.setUniqueResourceName ( "testInit" );
		bean.init();
		assertNotNull ( Configuration.getResource ( bean.getUniqueResourceName() ) );
	}
	
	public void testExclusiveConnectionMode() 
	{
		assertEquals ( true , bean.getExclusiveConnectionMode() );
		bean.setExclusiveConnectionMode ( true );
		assertEquals ( true , bean.getExclusiveConnectionMode() );
		bean.setExclusiveConnectionMode ( false );
		assertEquals ( false , bean.getExclusiveConnectionMode() );
	}
	
	public void testTestOnBorrow() 
	{
		assertFalse ( bean.getTestOnBorrow() );
		bean.setTestOnBorrow ( true );
		assertTrue ( bean.getTestOnBorrow() );
		bean.setTestOnBorrow ( false );
		assertFalse ( bean.getTestOnBorrow() );
	}

	public void testSetXaDataSource() throws SQLException
	{
		bean.setXaDataSourceClassName ( null );
		try {
			bean.init();
			fail ( "init works without xa datasource" );
		}
		catch ( SQLException ok ) {}
		TestXADataSource xads = new TestXADataSource();
		final int secs = 3;
		xads.setLoginTimeout ( secs );
		bean.setXaDataSource ( xads );
		assertEquals ( xads , bean.getXaDataSource() );
		bean.init();
		String props = bean.getXaDataSourceProperties();
		assertTrue ( props.indexOf ( "loginTimeout=" ) > 0 );
	}

}
