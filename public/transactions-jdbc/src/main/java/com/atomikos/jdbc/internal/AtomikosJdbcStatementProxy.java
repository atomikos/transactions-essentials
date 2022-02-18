/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.atomikos.util.DynamicProxySupport;
import com.atomikos.util.Proxied;

public class AtomikosJdbcStatementProxy<StmtInterface extends Statement> extends DynamicProxySupport<StmtInterface> {

	private final AbstractJdbcConnectionProxy connection;
	
	public AtomikosJdbcStatementProxy(AbstractJdbcConnectionProxy connectionProxy, StmtInterface delegate) {
		super(delegate);
		this.connection = connectionProxy;
	}
	
	
	@Override
	protected void throwInvocationAfterClose(String method) throws Exception {
		String msg = "Statement was already closed - calling " + method + " is no longer allowed!";
		AtomikosSQLException.throwAtomikosSQLException ( msg );
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


    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        throw e;
    }
    
    @Override
    public String toString() {
        return "atomikosJdbcStatementProxy for vendor instance " + delegate;
    }
    
    @Override
    protected Class<StmtInterface> getRequiredInterfaceType() {
    	if (delegate instanceof CallableStatement) {
    		return (Class<StmtInterface>) CallableStatement.class;
    	} else if (delegate instanceof PreparedStatement) {
    		return (Class<StmtInterface>) PreparedStatement.class;
    	} 
    	return (Class<StmtInterface>) Statement.class;
    }

}
