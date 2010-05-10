package com.atomikos.icatch.standalone;

import java.util.Properties;

import junit.framework.TestCase;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;

public class Issue59343TestJunit extends TestCase {

private UserTransactionService uts;
	
	public Issue59343TestJunit ( String name ) 
	{
		super ( name );
	}

	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty ( com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME, "true" );
	}


	protected void tearDown() throws Exception {
		
		if ( uts != null ) {
			uts.shutdown ( true );
		}
		super.tearDown();
	}
	
	public void testInitWithTSInitInfoPropertiesContainingServiceParameter()
	{
		uts = new com.atomikos.icatch.config.UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		info.setProperty ( "com.atomikos.icatch.service" , "com.atomikos.icatch.standalone.UserTransactionServiceFactory" );
		uts.init ( info );
		uts.getCompositeTransactionManager();
	}
	
	public void testInitWithPropertiesArgumentContainingServiceParameter()
	{
		uts = new com.atomikos.icatch.config.UserTransactionServiceImp();
		Properties p = new Properties();
		p.setProperty ( "com.atomikos.icatch.service" , "com.atomikos.icatch.standalone.UserTransactionServiceFactory" );
		uts.init ( p );
		uts.getCompositeTransactionManager();
	}
	
	public void testInitWithPropertiesConstructorArgumentContainingServiceParameter()
	{
		Properties p = new Properties();
		p.setProperty ( "com.atomikos.icatch.service" , "com.atomikos.icatch.standalone.UserTransactionServiceFactory" );
		uts = new com.atomikos.icatch.config.UserTransactionServiceImp ( p );
		uts.init ( p );
		uts.getCompositeTransactionManager();
	}
	
}
