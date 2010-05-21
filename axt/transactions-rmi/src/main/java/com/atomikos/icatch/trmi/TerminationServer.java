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
