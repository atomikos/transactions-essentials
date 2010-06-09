package com.atomikos.icatch.jta;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.SimpleDataSourceBean;
import com.atomikos.jdbc.TestXADataSource;
import com.atomikos.jdbc.nonxa.NonXADataSourceBean;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.PooledAlarmTimer;

/**
 * 
 * 
 * 
 * A load tester for the JTA and transaction service.
 * This is an application with two command-line arguments: 
 * one that specifies the number of threads and
 * a second one that specifies the number of transactions
 * per thread.
 * 
 */
public class LoadTester
implements Runnable, AlarmTimerListener
{


	private static DataSource ds;

	
	private static UserTransaction utx;
	private static UserTransactionManager utm;
	
	private static boolean useDatabase = false;
	
	
	
	
	public static void init() throws Exception 
	{
		utx = new UserTransactionImp();
		utx.setTransactionTimeout ( 59 );
		utm = new UserTransactionManager();
	}

	
	private static void initDB ( String dbtype , int poolsize )
	throws Exception
	{
		if ( dbtype.equals ( "xa") ) {
		
			SimpleDataSourceBean sds = new SimpleDataSourceBean();
			sds.setUniqueResourceName ( "xaDatabase" );
			String className = System.getProperty ( "xaclass" );
			if ( className == null ) {
				System.out.println ( "System property xaclass not found - using default.");
				className = "COM.FirstSQL.Dbcp.DbcpXADataSource";
			} 
			sds.setXaDataSourceClassName ( className );
			String props = System.getProperty ( "xaproperties" );
			if ( props == null ) {
				props = "user=demo;portNumber=8000";
				System.out.println ( "System property xaproperties not found - using default.");
			} 
			sds.setXaDataSourceProperties ( props );
			sds.setConnectionPoolSize ( poolsize );
			sds.setExclusiveConnectionMode(true);
			ds = sds;
		}
		else {
			NonXADataSourceBean nds = new NonXADataSourceBean();
			nds.setDriverClassName("COM.FirstSQL.Dbcp.DbcpDriver");
			nds.setUrl("jdbc:dbcp://localhost:8000");
			nds.setUser("demo");
			nds.setPoolSize(poolsize + 1 );
			nds.setUniqueResourceName("firstSQL");
			ds = nds;
		}	
		boolean commit = true;
		Connection c = null;
		Statement s = null;
		try {
			c = getNonTxConnection();
			s = c.createStatement();
			s.executeUpdate ( 
			"create table LOADTEST ( id INTEGER )" );
		}
		catch ( SQLException exists ) {
			exists.printStackTrace();
			s.executeUpdate ( "delete from LOADTEST" );
		}
		finally {
			closeNonTxConnection ( c );
		}
		
	}	
	
	private static void initTestDriver(int threads)
	throws Exception
	{
		SimpleDataSourceBean sds = new SimpleDataSourceBean();
		sds.setUniqueResourceName("dummyXADB");
		sds.setXaDataSourceClassName("com.atomikos.jdbc.test.TestXADataSource");
		sds.setConnectionPoolSize(threads );
		
		ds = sds;	
	}
	
	private static Connection getNonTxConnection()
	throws Exception
	{
		return ds.getConnection();	
	}
	
	private static void closeNonTxConnection ( Connection c )
	throws  Exception
	{
		if ( c != null ) c.close();	
	}
	
	private static Connection getConnection ()
	throws Exception
	{
		utx.begin();
		return ds.getConnection();
	}
	
	private static void closeConnection ( Connection c , boolean commit )
	throws Exception
	{
		try {
			//if ( commit ) c.commit();
			if ( c != null ) c.close();
		}
		finally {
			if ( commit ) utx.commit();
			else utx.rollback();
		}
	}

	
	private static void insert ( int value ) 
	throws Exception
	{
		Connection c = null;
		boolean commit = ( value % 2 == 0 );
		try {
			c = getConnection();
			Statement s = c.createStatement();
			s.executeUpdate ( 
				"insert into LOADTEST values ( "  + 
				value +"  ) ");
		}
		catch ( Exception e ) {
			commit = false;
			e.printStackTrace();
			throw e;
		}
		finally {
			String tid = "unknown";
			Transaction tx = utm.getTransaction();
			if ( tx != null ) tid = tx.toString();
			Configuration.logWarning ( "Commit=" + commit + " for LoadTester insert value: " + value + " for tx " + tid );
			closeConnection ( c , commit );
		}
	}
	
	private static void check ( int numThreads )
	throws Exception
	{
		if ( useDatabase ) {
			Connection c = null;
			boolean commit = true;
			try {
				c = getConnection();
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery ( "select id from LOADTEST" );	 
				
				int errors = 0;
				while  ( rs.next() ) {
					int value = rs.getInt ( 1 );
					
					if ( value % 2 != 0 ) {
						errors++;
						System.out.println  ( "Odd key detected: " + value );
					}
				}
				if ( errors != 0 ) throw new Exception ( "Errors in checking results: " + errors );
			}
			catch ( Exception e ) {
				commit = false;
				e.printStackTrace();
				throw e;
			}
			finally {
				closeConnection ( c , commit );
			}
		}
		else {
			int count = TestXADataSource.getConnectionCount();
			if ( count > numThreads )
				System.out.println ( "WARNING: no of connections gotten: " + count );	
			else System.out.println ( "Used connections: " + count );
		}
	}
		
	
	private static int maxrequest;
	//the max number of requests for each thread
	
	
	private int activeThreads;
	//zero when all threads are done
	
	private static Map idMap;
	//maps each thread to its ID
	
	
	private long tickCount;
	//to display progress
	
	public LoadTester ( int threads )
	{
		activeThreads = threads;
		idMap = new HashMap();
		for ( int i = 0 ; i < threads ; i++ ) {
			Thread t = new Thread ( this );
			idMap.put( t , new Integer ( i ));
			t.start();
		}
		
	}
	
	private synchronized void forceExit()
	{
		activeThreads = 0;
		notifyAll();	
	}

	
	private synchronized void decActiveThreads()
	{
		activeThreads--;
		if ( activeThreads == 0 ) {
			notifyAll();
		}
	}
	
	private synchronized void waitForThreads()
	{
		while ( activeThreads > 0 ) {
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
               
				e.printStackTrace();
			}	
		}
	}


	
	/**
	 * @see java.lang.Runnable#run()
	 */
	
	public void run()
	{
		
		Thread t = Thread.currentThread();
		int id = (( Integer) idMap.get( t )).intValue();
		int first = ( id - 1 ) * maxrequest;
		int last = id * maxrequest;
		
		for ( int i = first ; i < last ; i++ ) {
			
			try {
				
				if ( useDatabase ) insert ( i );
				else beginAndEndTransaction();
			}
			catch ( Exception e ) {
				Configuration.logWarning ( "Error while inserting value: " + i , e );
				e.printStackTrace();
			}	
			
			
		}
		System.out.println ( "Thread " + id + " inserted from " + first + " to " + ( last-1 ) );
		decActiveThreads();
        
	}	

	
    private void beginAndEndTransaction() throws Exception
    {
        
        Connection c = null;
        boolean commit = true;
        try
        {
            c = getConnection();
        }
        catch (Exception e)
        {
            commit = false;
        }
        finally {
        	closeConnection ( c , commit);
        }
   
        
    }

	public void alarm(AlarmTimer arg0)
	{
		tickCount++;
		Runtime rt = Runtime.getRuntime();
		long freeMemory = rt.freeMemory();
		long totalMemory = rt.totalMemory();
		System.out.println ( "Memory used: " + ( totalMemory - freeMemory ) );
		long now = System.currentTimeMillis();
        
	}	

	public static void main ( String[] args ) throws Exception
	{
        try
        {
            if ( args.length < 2 ) {
            	System.err.println ( "Usage: java <optional: -Dxaclass=full_name -Dxaproperties=property1=value1;property2=value2> com.atomikos.icatch.jta.test.LoadTester <number of threads> <number of txs per thread> <optional: xa|nonxa>");
            	System.exit ( 1 );
            }
            init();
            
            int numThreads = Integer.parseInt ( args[0] );
            maxrequest = Integer.parseInt ( args[1] );
            
            if ( args.length >= 3 ) {
            
            			System.out.println ( "Testing with DB...");
            			initDB ( args[2] , numThreads);
            			useDatabase = true;
            		
            }
            else {
            	System.out.println ( "Testing without DB...");
            	useDatabase = false;
            	initTestDriver ( numThreads );
            }
            
            
            long start = System.currentTimeMillis();
            LoadTester tester = new LoadTester ( numThreads );
            AlarmTimer timer = new PooledAlarmTimer(10000);
            timer.addAlarmTimerListener(tester);
            TaskManager.getInstance().executeTask ( timer );
            
            tester.waitForThreads();
            
            
            long stop = System.currentTimeMillis();
            System.out.println ( "Throughput (requests per second) : " + ( numThreads * maxrequest ) /( ( float ) (  stop - start ) ) * 1000  );
            
            
            
            timer.stop();
            
            check ( numThreads );
          
            
        }
        
        catch (Exception e)
        {
       
            e.printStackTrace();
        }

		
  
		System.out.println ( "TEST DONE - PRESS ENTER FOR EXITING" );
		BufferedReader breader = 
		  new BufferedReader ( new InputStreamReader ( System.in ) );
		breader.readLine();
            
		System.exit ( 0 );
	}


}

