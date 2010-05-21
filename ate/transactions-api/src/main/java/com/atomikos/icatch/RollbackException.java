package com.atomikos.icatch;

/**
 *
 *
 *An exception indicating that a tx has been rolled back.
 */
 
 public class RollbackException extends Exception
 {
    public RollbackException ( String msg ) 
    {
      super (msg);	
    }	
    
    public RollbackException ( ) 
    {
      super ();	
    }
 }
