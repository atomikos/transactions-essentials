package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A message that carries the state of a remote party.
 */

public interface StateMessage extends TransactionMessage
{
    /**
     * Test if the sender has committed, rolled back or if not applicable.
     * 
     * @return Boolean Null if not applicable, True if committed and False if
     *         rolled back.
     */

    public Boolean hasCommitted ();

}
