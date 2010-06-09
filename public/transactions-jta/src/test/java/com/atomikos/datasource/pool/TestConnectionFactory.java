package com.atomikos.datasource.pool;

public class TestConnectionFactory implements ConnectionFactory {

	public XPooledConnection createPooledConnection()
			throws CreateConnectionException {
		
		return new TestXPooledConnection();
	}

}
