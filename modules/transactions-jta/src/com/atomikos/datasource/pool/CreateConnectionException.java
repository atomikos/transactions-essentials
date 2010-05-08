package com.atomikos.datasource.pool;

 /**
  * An exception to signal errors during the creation of connections.
  * 
  *
  */

public class CreateConnectionException extends ConnectionPoolException 
{

	public CreateConnectionException ( String reason , Exception cause ) 
	{
		super ( reason , cause );
	}
	public CreateConnectionException ( String reason ) 
	{
		super ( reason );
	}
}
