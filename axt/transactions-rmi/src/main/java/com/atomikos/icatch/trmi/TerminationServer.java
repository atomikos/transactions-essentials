//$Id: TerminationServer.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: TerminationServer.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
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
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
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
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos.
 *
 *A termination server is terminates transactions on the client's request,
 *and does this based on root ID for efficiency.
 */
 
 public interface TerminationServer extends Remote
 {
    /**
     *Commit the composite transaction.
     *@param root The root id to commit.
     *
     *@exception HeurRollbackException On heuristic rollback.
     *@exception HeurMixedException On heuristic mixed outcome.
     *@exception SysException For unexpected failures.
     *@exception SecurityException If calling thread does not have 
     *right to commit.
     *@exception RollbackException If the transaction was rolled back
     *before prepare.
     *@exception RemoteException If comm. failure happens.
     */

    public void commit( String root ) 
        throws 
          HeurRollbackException,HeurMixedException,
          SysException,java.lang.SecurityException,
          RollbackException , RemoteException;


    /**
     *Rollback the current transaction.
     *@param root The root id to rollback.
     *@exception IllegalStateException If prepared or inactive.
     *@exception SysException If unexpected error.
     *@exception RemoteException On comm. failure.
     */

    public void rollback( String root )
        throws IllegalStateException, SysException , RemoteException;

 }
