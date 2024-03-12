/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.XPooledConnectionEventListener;
import com.atomikos.jdbc.internal.AtomikosNonXAPooledConnection;

public class JtaUnawareThreadLocalConnectionTestJUnit {

    
    @Mock
    private Connection mockedVendorConnection;
    @Mock
    private Statement mockedVendorStatement;
    @Mock
    private ConnectionPoolProperties mockedCPP;

    @Mock
    private XPooledConnectionEventListener<Connection> mockedListener;

    
    private AtomikosNonXAPooledConnection pc;
    private Connection proxyConnection;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockedVendorConnection.isValid(Mockito.anyInt())).thenReturn(true);
        Mockito.when(mockedVendorConnection.createStatement()).thenReturn(mockedVendorStatement);
        mockedCPP = Mockito.mock(ConnectionPoolProperties.class);
        Mockito.when(mockedCPP.getLocalTransactionMode()).thenReturn(true);
        pc = new AtomikosNonXAPooledConnection(mockedVendorConnection, mockedCPP, false);
        pc.registerXPooledConnectionEventListener(mockedListener);
        proxyConnection = pc.createConnectionProxy();
    }

    @Test
    public void testRollbackOnCloseOfDirtyConnection() throws Exception {
       Mockito.when(mockedVendorConnection.getAutoCommit()).thenReturn(false);
       proxyConnection.createStatement();
       proxyConnection.close();
       Mockito.verify(mockedVendorConnection, Mockito.times(1)).rollback();
    }
    
    @Test
    public void testNoExtraRollbackOnCloseOfConnectionAfterRollback() throws Exception {
       Mockito.when(mockedVendorConnection.getAutoCommit()).thenReturn(false);
       proxyConnection.createStatement();
       proxyConnection.rollback();
       Mockito.verify(mockedVendorConnection, Mockito.times(1)).rollback();
       proxyConnection.close();
       Mockito.verify(mockedVendorConnection, Mockito.times(1)).rollback();
    }
    
    @Test
    public void testNoExtraRollbackOnCloseOfConnectionAfterCommit() throws Exception {
       Mockito.when(mockedVendorConnection.getAutoCommit()).thenReturn(false);
       proxyConnection.createStatement();
       proxyConnection.commit();
       proxyConnection.close();
       Mockito.verify(mockedVendorConnection, Mockito.times(0)).rollback();
    }
    
    @Test
    public void testNoExtraRollbackOnCloseWithAutoCommit() throws Exception {
       Mockito.when(mockedVendorConnection.getAutoCommit()).thenReturn(true);
       proxyConnection.createStatement();
       proxyConnection.close();
       Mockito.verify(mockedVendorConnection, Mockito.times(0)).rollback();
    }
    
    @Test
	public void testCloseNotifiesPool() throws Exception { // test for case 189263
    	proxyConnection.close();
    	Mockito.verify(mockedListener).onXPooledConnectionTerminated(Mockito.any());
	}

}
