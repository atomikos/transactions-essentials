package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Atomikos. All rights reserved.
 * 
 * A commit message.
 */

public interface CommitMessage extends TransactionMessage
{
    /**
     * Test if the commit is a one-phase commit.
     * 
     * @return boolean True iff one phase.
     */

    public boolean isOnePhase ();
    

}
