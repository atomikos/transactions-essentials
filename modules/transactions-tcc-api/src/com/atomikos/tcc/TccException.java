package com.atomikos.tcc;

/**
 * Copyright &copy; 2006, Atomikos. All rights reserved.
 *
 * An exception to signal application-level errors 
 * in the TryConfirmCancel paradigm.
 */

public class TccException extends Exception 
{

	public TccException() 
	{
		super();
	}

	public TccException ( String reason ) 
	{
		super(reason);
	}

	
}
