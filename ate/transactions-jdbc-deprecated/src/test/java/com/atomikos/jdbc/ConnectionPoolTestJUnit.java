package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.transaction.TransactionManager;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class ConnectionPoolTestJUnit extends TransactionServiceTestCase
{
	
	public static int MAX_CALLS = 100;
    
	public static int THREADS = 5;
	
	private static int runningThreads = 0;
	
	private static Object threadWaiter = new Object();
	
	private static Exception errorInThreads = null;
	

	
	private static void decCounter()
	{
		synchronized ( threadWaiter ) {
			runningThreads--;
			if ( runningThreads == 0 ) threadWaiter.notifyAll();
			System.err.println ( "One thread finished!");
		}
		
	}
	
	private static void waitForThreads() throws InterruptedException
	{
		synchronized ( threadWaiter ) {
			while ( runningThreads > 0 ) {
				System.err.println ( "Waiting for " + runningThreads  + " threads to finish...");
				threadWaiter.wait();
			}
			System.err.println ( "Threads finished!");
		}
	}

	private UserTransactionService uts;
	private JtaDataSourceImp datasource;
	private XAConnectionFactory factory;
	
	public ConnectionPoolTestJUnit ( String name ) 
	{
		super ( name );
	}
	
	public void setUp()
	{
		super.setUp();
		uts =
            new UserTransactionServiceImp();
        
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "ConnectionPoolTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        	TestXADataSource ds = new TestXADataSource();
            
		factory = 
				new XAConnectionFactory ( "JDBCPoolingTest" , "" , "" , ds );
		uts.registerResource (
			  factory.getTransactionalResource() );
      
		uts.init ( info );
		try {
			datasource = 
					new JtaDataSourceImp ( factory , THREADS , 5 , null , false );
		} catch (SQLException e) {
			
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
	}
	
	public void tearDown()
	{
		uts.shutdown ( true );
		datasource.close();
		super.tearDown();
		
	}
	
	public void testThreads() throws Exception
	{
		runningThreads = THREADS;
		for ( int i = 0 ; i < THREADS ; i++ ) {
			
			  TestThread thread =
				  new TestThread();	
				Thread t = new Thread ( thread );
				t.start();
		}
	
		waitForThreads();
		
		TestXADataSource tester = 
			( TestXADataSource ) factory.getXADataSource();
    
		//XATransactionalResource gets 1 extra connection on setup!
		//NOTE: due to concurrency of the cleanup thread, the number
		//of connections gotten may be much higher, so only WARN here
		if ( tester.getConnectionCount() != THREADS + 1 )
			System.err.println ( "Abnormal number of connections created: " +
			tester.getConnectionCount() + " -- possibly indicating no pool reuse?" );
		
		if ( errorInThreads != null ) failTest ( errorInThreads.getMessage() );
	}
	
	
	private class TestThread 
	implements Runnable
	{
		
	
		public void run() 
		{
			Connection c = null;
			TransactionManager tm =    
				uts.getTransactionManager();
	    
			for ( int i = 0 ; i < MAX_CALLS ; i++ )  {
			  try {
	          
				  tm.begin();
				  c = datasource.getConnection();
	      
			  }
			  catch ( Exception e ) {
				  errorInThreads = e;
				  e.printStackTrace();
				  System.err.println ( "Error in thread: " + e.getMessage() );	
			  }	
			  finally {
			  try {
				  if ( c != null )
					  c.close();
				  tm.commit();
				}
				catch ( Exception err )  {
					errorInThreads = err;
					System.err.println ( "Error in close: " + err.getMessage() );	
				}	
				
			  }
			}
			decCounter();
		}
	}
	
	
}
