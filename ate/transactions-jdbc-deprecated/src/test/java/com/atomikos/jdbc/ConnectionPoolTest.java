package com.atomikos.jdbc;

import java.sql.Connection;

import javax.transaction.TransactionManager;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;


/**
 * 
 * 
 * 
 * 
 *
 * A test class for the connection pool
 */

public class ConnectionPoolTest implements Runnable
{

	public static String databaseName = "TestDB";
			
			private static UserTransactionService uts_;
 	
			private static JtaDataSourceImp datasource_;
        
			public static int MAX_CALLS = 100;
        
			public static int THREADS = 5;
        
			private int threadNumber_;
        
			private static XAConnectionFactory factory_;
        
			public static void setup() throws Exception
			{
				  uts_ = new UserTransactionServiceImp();;
				  TSInitInfo info = uts_.createTSInitInfo();
              
				  TestXADataSource ds = new TestXADataSource();
              
					factory_ = 
						new XAConnectionFactory ( "JDBCPoolingTest" , "" , "" , ds );
				  info.registerResource (
					  factory_.getTransactionalResource() );
              
				  uts_.init ( info );
					datasource_ = 
						new JtaDataSourceImp ( factory_ , THREADS , 5  , null , false );
			}
        
			public static void testThreads() throws Exception
			{
				for ( int i = 0 ; i < THREADS ; i++ ) {
					  ConnectionPoolTest tester =
						  new ConnectionPoolTest ( i );	
						Thread t = new Thread ( tester );
						t.start();
				}
        	
				Thread.sleep ( 100 * MAX_CALLS );
			}
        
			public static void shutdown() throws Exception
			{
				 uts_.shutdown ( true );
				 datasource_.close();
             
			}
        
			public static void verify() throws Exception
			{ 
				TestXADataSource tester = 
					( TestXADataSource ) factory_.getXADataSource();
            
				//XATransactionalResource gets 1 extra connection on setup!
				if ( tester.getConnectionCount() != THREADS + 1 )
					throw new Exception ( "Abnormal number of connections created: " +
					tester.getConnectionCount() + " -- possibly indicating no pool reuse?" );
                
			}
        
			public static void test() throws Exception
			{
				try {
					//System.out.println ( "setup..." );
					  setup();
					  //System.out.println ( "threads..." );
					  testThreads();
					  //System.out.println ( "verify..." );
					  verify();
					  //System.out.println ( "done." );
				}
				catch ( Throwable e ) {
					e.printStackTrace(); 
				}
				finally {
					shutdown();	
				}
                    	
			}
        
        
			public ConnectionPoolTest ( int number )
			{
				threadNumber_ = number;	
			}
        
			public void run() 
			{
				Connection c = null;
				TransactionManager tm =    
					uts_.getTransactionManager();
            
				for ( int i = 0 ; i < MAX_CALLS ; i++ )  {
				  try {
                  
					  tm.begin();
					  c = datasource_.getConnection();
              
				  }
				  catch ( Exception e ) {
					  e.printStackTrace();
					  System.out.println ( "Error in thread: " + e.getMessage() );	
				  }	
				  finally {
				  try {
					  if ( c != null )
						  c.close();
					  tm.commit();
					}
					catch ( Exception err )  {
						System.out.println ( "Error in close: " + err.getMessage() );	
					}	
				  }
				}
			}
			
	public static void main ( String[] args ) 
		  {
			  try {
				ConnectionPoolTest.test();
          	
			  }
			  catch ( Exception e ) {
				e.printStackTrace();
			  }
			  finally {
				  System.out.println ( "Done: ConnectionPoolingTest" );	
			  }	
		  }


}
