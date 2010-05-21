package com.atomikos.datasource.xa.session;

 /**
  * 
  * 
  * 
  * Exception signaling that the state has 
  * been corrupted. Occurrences should almost
  * invariably cause the session handle to be
  * discarded.
  * 
  *
  */

public class InvalidSessionHandleStateException 
extends Exception 
{
	
	InvalidSessionHandleStateException ( String msg ) 
	{
		super ( msg );
	}
	
	
	InvalidSessionHandleStateException ( String msg , Exception cause ) 
	{
		super ( msg , cause );
	}
	
}
