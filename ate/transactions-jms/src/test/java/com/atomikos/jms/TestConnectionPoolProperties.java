package com.atomikos.jms;

import com.atomikos.datasource.pool.ConnectionPoolProperties;

public class TestConnectionPoolProperties implements ConnectionPoolProperties  {

	boolean localTransactionMode;
	
	
	
	public int getBorrowConnectionTimeout() {
		
		return 0;
	}

	public void setLocalTransactionMode ( boolean mode ) {
		this.localTransactionMode = mode;
	}
	
	public boolean getLocalTransactionMode() {
		return localTransactionMode;
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

	public int getDefaultIsolationLevel() {
		return 0;
	}
	
}