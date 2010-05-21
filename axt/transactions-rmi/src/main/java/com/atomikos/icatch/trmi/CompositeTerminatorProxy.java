package com.atomikos.icatch.trmi;
import java.rmi.RemoteException;
import java.util.Stack;

import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

  /**
    *Copyright &copy; 2001, Atomikos. All rights reserved.
    *
    *A proxy class for termination; can be passed to remote clients if they
    *are allowed to terminate the transaction.
    */
    
   class CompositeTerminatorProxy implements CompositeTerminator
   {
      protected TerminationServer server_ ;
      
      protected String root_ ;
      
      public CompositeTerminatorProxy ( TerminationServer server , String root )
      {
        	server_ = server;
        	root_ = root;
      }	
      
    /**
     *@see CompositeTerminator
     */

    public void commit() 
        throws 
          HeurRollbackException,HeurMixedException,
          SysException,java.lang.SecurityException,
          RollbackException
    {
        Stack errors = new Stack();
        try {
        	server_.commit ( root_ );
        }
        catch ( RemoteException re ) {
          	errors.push ( re );
          	throw new SysException ( "Remote error in commit: " + re.getMessage() , errors );
        }
    }
    
   /**
     *@see CompositeTerminator
     */

    public void rollback()
        throws IllegalStateException, SysException
    {
        Stack errors = new Stack();
        try {
        	server_.rollback ( root_ );
        }
        catch ( RemoteException re ) {
          	errors.push ( re );
          	throw new SysException ( "Remote error in rollback: " + re.getMessage() , errors );
        }
    }



   }
