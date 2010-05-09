//$Id: CompositeTerminatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//$Log: CompositeTerminatorProxy.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:16  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.4  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.3  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//


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
