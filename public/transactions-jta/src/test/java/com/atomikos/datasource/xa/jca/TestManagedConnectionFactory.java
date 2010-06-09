package com.atomikos.datasource.xa.jca;

import java.io.PrintWriter;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

public class TestManagedConnectionFactory implements ManagedConnectionFactory {

	private XAResource xares;
	
	private TestManagedConnection lastManagedConnection;
	
	public TestManagedConnectionFactory ( XAResource xares )
	{
		this.xares = xares;
	}
	
	public Object createConnectionFactory(ConnectionManager arg0)
			throws ResourceException {
		return null;
	}

	public Object createConnectionFactory() throws ResourceException {
		
		return null;
	}
	
	

	public ManagedConnection createManagedConnection(Subject arg0,
			ConnectionRequestInfo arg1) throws ResourceException {
		lastManagedConnection = new TestManagedConnection ( xares );
		return lastManagedConnection;
	}
	
	public ManagedConnection getLastCreatedManagedConnection()
	{
		return lastManagedConnection;
	}

	public ManagedConnection matchManagedConnections(Set arg0, Subject arg1,
			ConnectionRequestInfo arg2) throws ResourceException {
		
		return createManagedConnection ( null , null );
	}

	public void setLogWriter(PrintWriter arg0) throws ResourceException {
		

	}

	public PrintWriter getLogWriter() throws ResourceException {
		
		return null;
	}

}
