/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Proxied;

public class JtaUnawareThreadLocalConnection extends AbstractJdbcConnectionProxy implements NonXaConnectionProxy {
    
    private static final Logger LOGGER = LoggerFactory.createLogger(JtaUnawareThreadLocalConnection.class);
    
    private final AtomikosNonXAPooledConnection pooledConnection;
    private boolean dirty;


    public JtaUnawareThreadLocalConnection(AtomikosNonXAPooledConnection pooledConnection) {
        super(pooledConnection.getConnection());
        this.pooledConnection = pooledConnection;
    }

    @Override
    protected void updateTransactionContext() throws SQLException {
        if (!getAutoCommit()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this+ ": marking as dirty...");
            }
            dirty = true;
        }
    }

    @Override
    protected boolean isEnlistedInGlobalTransaction() {
        return false;
    }

    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        pooledConnection.setErroneous();
        throw e;
    }
    
    @Override
    public String toString() {
        return "jtaUnawareThreadLocalConnection (isAvailable = " + isAvailableForReuseByPool() + ")  for vendor instance " + delegate; 
    }

    @Override
    public void transactionTerminated(boolean committed) throws SQLException {
        //should never happen???
        AtomikosSQLException.throwAtomikosSQLException(this + ": transaction termination detected - which is incompatible with this type of connection???");
    }

    @Override
    public boolean isAvailableForReuseByPool() {
        return closed;
    }
    
    @Proxied
    @Override
    public void commit() throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": commit on vendor connection...");
        }
        delegate.commit();
        dirty = false;
    }

    @Proxied
    @Override
    public void rollback() throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": rollback on vendor connection...");
        }
        delegate.rollback();
        dirty = false;

    }
    
    
    @Proxied 
    public void close() throws SQLException {
        if (dirty && !delegate.getAutoCommit()) {
           try {
               rollback(); //case 175344: ensures clean connection state for reuse
           } catch (Throwable t) {
               LOGGER.logWarning(this + ": unexpected error trying to rollback on vendor connection - marking connection as erroneous so it will be replaced by the pool...", t);
               pooledConnection.setErroneous(); //avoid reuse at all cost
           }
        } 
        markClosed();
        pooledConnection.fireOnXPooledConnectionTerminated();
    }

    @Override
	protected Class<Connection> getRequiredInterfaceType() {
		return Connection.class;
	}
}
