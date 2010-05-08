package com.atomikos.jdbc.nonxa;

import java.sql.Connection;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.jdbc.TestConnection;

public class AtomikosNonXAPooledConnectionTestJUnit extends TransactionServiceTestCase {

	private AtomikosNonXAPooledConnection pc;
	private TestConnection c;
	
	public AtomikosNonXAPooledConnectionTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp()  {
		super.setUp();
		
		ConnectionPoolProperties props = new ConnectionPoolProperties() {

			public int getBorrowConnectionTimeout() {
				return 0;
			}

			public int getDefaultIsolationLevel() {
				return 0;
			}

			public boolean getLocalTransactionMode() {
				return false;
			}

			public int getMaintenanceInterval() {
				return 10;
			}

			public int getMaxIdleTime() {
				return 10;
			}

			public int getMaxPoolSize() {
				return 1;
			}

			public int getMinPoolSize() {
				return 1;
			}

			public int getReapTimeout() {
				return 0;
			}

			public String getTestQuery() {
				return null;
			}

			public String getUniqueResourceName() {
				return "testName";
			}
			
		};
		
		c = new TestConnection(null);
		pc = new AtomikosNonXAPooledConnection ( c , props , false );
	}
	
	protected void tearDown() {
		super.tearDown();
	}


	
	public void testReapUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException 
	{
		long first = pc.getLastTimeReleased();
		Thread.sleep ( 10 );
		//pc.createConnectionProxy ( null );
		pc.reap();
		long second = pc.getLastTimeReleased();
		assertTrue ( "reaping does not update the lastTimeReleased value?" , second > first );
	}

	public void testCloseUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException {
		long first = pc.getLastTimeReleased();
		Thread.sleep ( 10 );
		Reapable proxy = pc.createConnectionProxy(null);
		proxy.close();
		long second = pc.getLastTimeReleased();
		assertTrue ( "closing does not update the lastTimeReleased value?" , second > first );
	}
	
	
}
