package com.atomikos.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import com.atomikos.util.DynamicProxySupport;
import com.atomikos.util.Proxied;

public class AtomikosJdbcStatementProxy extends DynamicProxySupport<Statement> {

	private final AtomikosJdbcConnectionProxy connection;
	
	public AtomikosJdbcStatementProxy(AtomikosJdbcConnectionProxy connectionProxy, Statement delegate) {
		super(delegate);
		this.connection = connectionProxy;
	}
	
	
	@Override
	protected void throwInvocationAfterClose(String method) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	@Proxied
	public void close() throws SQLException {
		try {
			this.delegate.close();	
		} finally {
			// safe to remove: statement will not be reused 
			this.connection.removeStatement(this.delegate);	
		}
		
		
	}

}
