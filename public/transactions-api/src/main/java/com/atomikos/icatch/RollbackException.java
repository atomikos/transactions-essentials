/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;



/**
 * An exception indicating that a transaction has already been rolled back.
 */

 public class RollbackException extends Exception
 {

	private static final long serialVersionUID = 1L;

	public RollbackException(String msg)
    {
      super (msg);
    }

	public RollbackException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
    public RollbackException()
    {
      super();
    }
 }
