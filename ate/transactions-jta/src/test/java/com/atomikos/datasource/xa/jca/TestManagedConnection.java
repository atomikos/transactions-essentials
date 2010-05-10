package com.atomikos.datasource.xa.jca;

import java.io.PrintWriter;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

public class TestManagedConnection implements ManagedConnection {

	private boolean destroyed;
	
	private XAResource xares;
	
	public TestManagedConnection ( XAResource xares ) 
	{
		this.xares = xares;
	}
	
	public Object getConnection(Subject arg0, ConnectionRequestInfo arg1)
			throws ResourceException {
			return null;
	}

	public void destroy() throws ResourceException {
		destroyed = true;

	}
	
	public boolean wasDestroyed() 
	{
		return this.destroyed;
	}

	public void cleanup() throws ResourceException {
		

	}

	public void associateConnection(Object arg0) throws ResourceException {
		

	}

	public void addConnectionEventListener(ConnectionEventListener arg0) {
		

	}

	public void removeConnectionEventListener(ConnectionEventListener arg0) {
	

	}

	public XAResource getXAResource() throws ResourceException {
		return xares;
	}

	public LocalTransaction getLocalTransaction() throws ResourceException {
		
		return null;
	}

	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		
		return null;
	}

	public void setLogWriter(PrintWriter arg0) throws ResourceException {
		

	}

	public PrintWriter getLogWriter() throws ResourceException {
		
		return null;
	}

}
