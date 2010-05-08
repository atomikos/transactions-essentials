package com.atomikos.datasource.pool;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.atomikos.datasource.pool.TestXPooledConnection.TestReapable;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class ConnectionPoolTestJUnit extends TransactionServiceTestCase {

	private ConnectionPool connectionPool;
	private UserTransactionServiceImp uts;
	private TestConnectionPoolProperties cpp;


	public ConnectionPoolTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp() {
		super.setUp();

		
		//start the transaction service
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		info.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
		uts.init ( info );
		
		
		cpp = new TestConnectionPoolProperties();
		cpp.setMaxPoolSize(1);
		cpp.setMinPoolSize(1);
		cpp.setBorrowConnectionTimeout(3);
		cpp.setMaxIdleTime(0);
		cpp.setReapTimeout(0);
		cpp.setResourceName("atomTestRes1");
		cpp.setMaintenanceInterval(1);
		
		com.atomikos.datasource.pool.ConnectionFactory cf = new TestConnectionFactory();
		
		try {
			connectionPool = new ConnectionPool(cf, cpp);
		} catch (ConnectionPoolException e) {
			throw new RuntimeException(e);
		}
	}

	protected void tearDown() {
		//shutdown transaction service
		uts.shutdown ( true );
		//destroy pool
		connectionPool.destroy();
		//cleanup super stuff
		super.tearDown();
	}

	public void testReplacementOfErroneousConnection() throws Exception {
		
		int size = connectionPool.totalSize();
		//assert size is 1 so we get the same (erroneous) connection
		assertEquals ( 1 , size );
		TestReapable c = ( TestReapable) connectionPool.borrowConnection ( null );
		assertFalse ( c.isErroneous() );
		assertFalse ( c.wasUnderlyingConnectionDestroyed() );
		c.setErroneous();
		assertTrue ( c.isErroneous() );
		c.close();
		//erroneous is replaced upon next borrow
		Reapable c2 = connectionPool.borrowConnection ( null );
		c2.close();
		//erroneous connection must have been closed 
		assertTrue ( c.wasUnderlyingConnectionDestroyed() );
		//pool must be same size as before
		assertEquals ( size , connectionPool.totalSize() );
	}
	
	public void testDestroy() throws Exception {
		connectionPool.destroy();
		assertEquals ( 0 , connectionPool.availableSize() );
		assertEquals ( 0 , connectionPool.totalSize() );
		try {
			connectionPool.borrowConnection ( null );
			fail ( "borrow works after destroy" );
		}
		catch ( ConnectionPoolException ok ) {}
		
		//assert destroy is idempotent
		connectionPool.destroy();
	}
	
	public void testBasicPoolSizeManagement() throws Exception {

		int size = connectionPool.availableSize();
		assertEquals ( size , connectionPool.totalSize() );
		assertEquals ( size , cpp.getMinPoolSize() );
		Reapable c =  connectionPool.borrowConnection ( null );
		assertEquals ( size - 1 , connectionPool.availableSize());
		c.close();
		assertEquals ( size , connectionPool.availableSize() );
		assertEquals ( size , connectionPool.totalSize() );
	}
	
	public void testMultiThreadBorrowConnectionSuccess() throws Exception {
		ArrayList threads = new ArrayList();
		for (int i=0; i<3 ;i++) {
			ConnectionConsumerThread thread = new ConnectionConsumerThread(connectionPool);
			thread.start();
			threads.add(thread);
		}
		
		boolean failed = false;
		for (int i=0; i<threads.size() ;i++) {
			ConnectionConsumerThread thread = (ConnectionConsumerThread) threads.get(i);
			thread.join();
			if (thread.exception != null) {
				failed = true;
				thread.exception.printStackTrace();
			}
		}
		
		assertFalse(failed);
	}
	
	public void testMultiThreadBorrowConnectionFailure() throws Exception {
		ArrayList threads = new ArrayList();
		for (int i=0; i<5 ;i++) {
			ConnectionConsumerThread thread = new ConnectionConsumerThread(connectionPool);
			thread.start();
			threads.add(thread);
		}
		
		int failedCount = 0;
		for (int i=0; i<threads.size() ;i++) {
			ConnectionConsumerThread thread = (ConnectionConsumerThread) threads.get(i);
			thread.join();
			if (thread.exception != null) {
				failedCount++;
			}
		}
		
		assertEquals(2, failedCount);
	}
	
	public int cpt = 0;
	public void testReap() throws Exception
	{	
		cpp.setReapTimeout(2);
		//make sure borrow timeout is higher than reap timeout to make reaping act
		cpp.setBorrowConnectionTimeout ( ( int ) ( ( cpp.getMaintenanceInterval()  + cpp.getReapTimeout() ) * 2 ) );
		
		Reapable c = connectionPool.borrowConnection ( null );
		TestXPooledConnection.TestReapable con = (TestXPooledConnection.TestReapable) c;
		c.close();
		
		Thread.sleep ( cpp.getMaintenanceInterval() * 1000 * 2 );
		//make sure reap timeout expired before going on, see issue 32800
		
		c = connectionPool.borrowConnection ( null );
		XPooledConnectionEventListener listener = new XPooledConnectionEventListener() {
			public void onXPooledConnectionTerminated(XPooledConnection connection) {
				cpt++;
			}
		};
		con.getTestXPooledConnection().registerXPooledConnectionEventListener(listener);
		assertEquals(0, cpt);
		
		//allow reaping thread to run, make sure the connection hasn't been reaped yet
		Thread.sleep ( cpp.getMaintenanceInterval() * 1000 + 100 );
		assertEquals(0, cpt);
		
		//allow reaping to happen, make sure the connection has now been reaped
		Thread.sleep ( cpp.getMaintenanceInterval() * 1000 + 100 );
		assertEquals(1, cpt);
		
		Reapable c2 = null;
		try {
			c2 = connectionPool.borrowConnection ( null );
		} 
		catch ( CreateConnectionException error ) {
			error.printStackTrace();
			fail ( "reaping does not work?" );
		}
		//borrow of c2 should not time out
		c.close();
		c2.close();
	}
	
	protected String formatDate() {
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date());
	}
	protected String formatDate(long timestamp) {
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date(timestamp));
	}

	public void testMaxPoolSize() throws Exception {
		for ( int i = 0 ; i < cpp.getMaxPoolSize() ; i++ ) {
			connectionPool.borrowConnection ( null );
		}
		
		assertEquals ( 0 , connectionPool.availableSize() );
		
		//pool should now be exhausted
		//try dynamic increase
		try {
			connectionPool.borrowConnection ( null );
			fail ( "not exhausted?" );
		}
		catch ( PoolExhaustedException ok ) {}
		assertEquals ( cpp.getMaxPoolSize() , connectionPool.totalSize() );
		cpp.setMaxPoolSize(cpp.getMaxPoolSize() + 1 );
		//borrow should now work
		connectionPool.borrowConnection ( null );
		assertEquals ( cpp.getMaxPoolSize() , connectionPool.totalSize() );
	}
	
	
	
	public void testMinPoolSize() throws Exception
	{
		cpp.setMinPoolSize(1);
		cpp.setMaxPoolSize(2);
		cpp.setMaxIdleTime(1);
		
		//exhaust pool to make sure max size reached
		Reapable[] conns = new Reapable[cpp.getMaxPoolSize()];
		for ( int i = 0 ; i < cpp.getMaxPoolSize() ; i++ ) {
			conns[i] = connectionPool.borrowConnection ( null );
		}
		
		//assert exhausted
		assertEquals ( 0 , connectionPool.availableSize() );
		
		//make connections available again
		for ( int i = 0 ; i < cpp.getMaxPoolSize() ; i++ ) {
			conns[i].close();
		}
		
		assertEquals ( cpp.getMaxPoolSize() , connectionPool.totalSize() );
		assertEquals ( cpp.getMaxPoolSize() , connectionPool.availableSize() );
		
		//wait for shrinking to happen
		Thread.sleep ( ( cpp.getMaintenanceInterval() * 1000 + cpp.getMaxIdleTime() * 1000 ) * 2 );
		
		//pool should now be at minsize
		assertEquals ( cpp.getMinPoolSize() , connectionPool.totalSize() );
		assertEquals ( cpp.getMinPoolSize() , connectionPool.availableSize() );
	}
	
	public void testShrinkPoolChecksForAvailabilityOfConnections() throws Exception 
	{
		cpp.setMinPoolSize ( 0 );
		cpp.setMaxPoolSize ( 1 );
		cpp.setMaxIdleTime ( 1 );
		cpp.setReapTimeout ( 0 );
		TestReapable tr = (TestReapable) connectionPool.borrowConnection ( null );
		Thread.sleep ( ( cpp.getMaxIdleTime() + cpp.getMaintenanceInterval() ) * 5 * 1000 );
		assertFalse ( tr.wasUnderlyingConnectionDestroyed() );
	}
	
	private class ConnectionConsumerThread extends Thread {
		public Exception exception;
		private ConnectionPool connectionPool;
		
		public ConnectionConsumerThread(ConnectionPool connectionPool) {
			this.connectionPool = connectionPool;
		}
		
		public void run() {
			try {
				Reapable c = connectionPool.borrowConnection ( null );
				Thread.sleep(1100);
				c.close();
			} catch (Exception e) {
				exception = e;
			}
		}
	}
	
	private static class TestConnectionPoolProperties implements ConnectionPoolProperties 
	{
		private int maxPoolSize;
		private int minPoolSize;
		private int borrowConnectionTimeout;
		private int maxIdleTime;
		private int reapTimeout;
		private String resourceName;
		private int maintenanceInterval;
		
		public int getMaxPoolSize() {
			return maxPoolSize;
		}
		public void setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}
		public int getMinPoolSize() {
			return minPoolSize;
		}
		public void setMinPoolSize(int minPoolSize) {
			this.minPoolSize = minPoolSize;
		}
		public int getBorrowConnectionTimeout() {
			return borrowConnectionTimeout;
		}
		public void setBorrowConnectionTimeout(int borrowConnectionTimeout) {
			this.borrowConnectionTimeout = borrowConnectionTimeout;
		}
		public int getMaxIdleTime() {
			return maxIdleTime;
		}
		public void setMaxIdleTime(int maxIdleTime) {
			this.maxIdleTime = maxIdleTime;
		}
		public int getReapTimeout() {
			return reapTimeout;
		}
		public void setReapTimeout(int reapTimeout) {
			this.reapTimeout = reapTimeout;
		}
		public String getUniqueResourceName() {
			return resourceName;
		}
		public void setResourceName(String resourceName) {
			this.resourceName = resourceName;
		}
		public int getMaintenanceInterval() {
			return maintenanceInterval;
		}
		public void setMaintenanceInterval(int maintenanceInterval) {
			this.maintenanceInterval = maintenanceInterval;
		}
		public String getTestQuery() {
			return null;
		}
		public boolean getLocalTransactionMode() {
			return true;
		}
		public int getDefaultIsolationLevel() {
			return 0;
		}
		
	}

}
