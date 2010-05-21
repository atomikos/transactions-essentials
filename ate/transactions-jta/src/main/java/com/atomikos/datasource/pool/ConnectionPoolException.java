package com.atomikos.datasource.pool;

 /**
  * Common superclass for all exceptions thrown by the 
  * pooling mechanism.
  *
  */

public class ConnectionPoolException extends Exception 
{

	private static final long serialVersionUID = 1L;
	
	public ConnectionPoolException ( String reason , Exception cause ) 
	{
		super ( reason , cause );
	}
	public ConnectionPoolException ( String reason ) 
	{
		super ( reason );
	}
}
