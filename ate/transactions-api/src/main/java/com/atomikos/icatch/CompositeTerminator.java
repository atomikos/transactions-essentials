package com.atomikos.icatch;


/**
 *
 *
 *A handle to terminate the composite transaction.
 *Must ALWAYS be used to handle termination throughout the system,
 *also for subtransactions!
 *
 *
 */

public interface CompositeTerminator 
{
    /**
     *Commit the composite transaction.
     *
     *@exception HeurRollbackException On heuristic rollback.
     *@exception HeurMixedException On heuristic mixed outcome.
     *@exception SysException For unexpected failures.
     *@exception SecurityException If calling thread does not have 
     *right to commit.
     *@exception HeurHazardException In case of heuristic hazard.
     *@exception RollbackException If the transaction was rolled back
     *before prepare.
     */

    public void commit() 
        throws 
	  HeurRollbackException,HeurMixedException,
	  HeurHazardException,
	  SysException,java.lang.SecurityException,
	  RollbackException;



    /**
     *Rollback the current transaction.
     *@exception IllegalStateException If prepared or inactive.
     *@exception SysException If unexpected error.
     */

    public void rollback()
        throws IllegalStateException, SysException;
}
