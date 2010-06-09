package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.datasource.xa.jdbc.TestJdbcTransactionalResource;
import com.atomikos.diagnostics.Console;
import com.atomikos.diagnostics.PrintStreamConsole;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AtomikosXAPooledConnection;
import com.atomikos.jdbc.TestXADataSource;

public class AtomikosXAPooledConnectionTestJUnit extends TransactionServiceTestCase {

	private AbstractXPooledConnection xaPooledConnection;

	public AtomikosXAPooledConnectionTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp() {
		super.setUp();
		TestXADataSource xads = new TestXADataSource();
		String name = getName();
		if (name.length() > 45)
			name = name.substring(name.length() - 45);
		TestJdbcTransactionalResource res = new TestJdbcTransactionalResource ( xads , name );
		
		try {
			XAConnection xac = xads.getXAConnection();
			xaPooledConnection = new AtomikosXAPooledConnection(xac, res, new ConnectionPoolProperties() {

				public int getBorrowConnectionTimeout() {
					return 0;
				}

				public int getMaintenanceInterval() {
					return 0;
				}

				public int getMaxIdleTime() {
					return 0;
				}

				public int getMaxPoolSize() {
					return 0;
				}

				public int getMinPoolSize() {
					return 0;
				}

				public int getReapTimeout() {
					return 0;
				}

				public String getTestQuery() {
					return null;
				}

				public String getUniqueResourceName() {
					return null;
				}

				public boolean getLocalTransactionMode() {
					return true;
				}

				public int getDefaultIsolationLevel() {
					return 0;
				}
				
			});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected void tearDown() {
		super.tearDown();
		xaPooledConnection.destroy();
	}


	public void testXPooledConnectionEventListenerOneTerminatedEvent() throws Exception {
		CountingXPooledConnectionEventListener listener = new CountingXPooledConnectionEventListener();
		xaPooledConnection.registerXPooledConnectionEventListener(listener);
		
		Connection c = (Connection) xaPooledConnection.createConnectionProxy ( null );
		c.close();

		xaPooledConnection.destroy();
		assertEquals(1, listener.terminatedCounter);
		assertEquals(0, listener.errorCounter);
	}
	
	public void testErroneousAfterSQLExceptionInProxy() throws Exception {
		Connection c = (Connection) xaPooledConnection.createConnectionProxy ( null );
		try {
			c.prepareStatement ( "bla" );
			fail ( "Prepare statement works?" );
		} catch ( SQLException ok ) {
			
		}
		c.close();
		assertTrue ( xaPooledConnection.isErroneous() );
		

	}
	
	public void testLastTimeReleased() {
		//see case 24394

		long now = System.currentTimeMillis();
		if (  ( now - 30 * 1000 ) > xaPooledConnection.getLastTimeReleased() ) fail ( "wrong initialization of lastTimeReleased" );
		
	}
	
	public void testXPooledConnectionEventListenerTenTerminatedEvents() throws Exception {
		CountingXPooledConnectionEventListener listener = new CountingXPooledConnectionEventListener();
		xaPooledConnection.registerXPooledConnectionEventListener(listener);
		
		for (int i = 0; i < 10; i++) {
			Connection c = (Connection) xaPooledConnection.createConnectionProxy ( null );
			c.close();
		}
		
		xaPooledConnection.destroy();
		assertEquals(10, listener.terminatedCounter);
		assertEquals(0, listener.errorCounter);
	}
	
	public void testReapUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException 
	{
		long first = xaPooledConnection.getLastTimeReleased();
		Thread.sleep ( 10 );
		//pc.createConnectionProxy ( null );
		xaPooledConnection.reap();
		long second = xaPooledConnection.getLastTimeReleased();
		assertTrue ( "reaping does not update the lastTimeReleased value?" , second > first );
	}

	public void testCloseUpdatesLastTimeReleased() throws CreateConnectionException, InterruptedException {
		long first = xaPooledConnection.getLastTimeReleased();
		Thread.sleep ( 10 );
		Reapable proxy = xaPooledConnection.createConnectionProxy(null);
		proxy.close();
		long second = xaPooledConnection.getLastTimeReleased();
		assertTrue ( "closing does not update the lastTimeReleased value?" , second > first );
	}
	

	private class CountingXPooledConnectionEventListener implements XPooledConnectionEventListener {
		int terminatedCounter = 0;
		int errorCounter = 0;
		public void onXPooledConnectionTerminated(XPooledConnection connection) {
			terminatedCounter++;
		}
		public void onXPooledConnectionError(XPooledConnection connection) {
			errorCounter++;
		}
	}

}
