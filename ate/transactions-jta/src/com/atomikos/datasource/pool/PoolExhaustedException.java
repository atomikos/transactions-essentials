package com.atomikos.datasource.pool;

 /**
  * Exception signaling pool exhaustion.
  *
  */
public class PoolExhaustedException extends ConnectionPoolException {


	public PoolExhaustedException ( String reason ) {
		super ( reason );
	}

}
