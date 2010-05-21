package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A message indicating that a remote participant has been prepared. This
 * message may or may not be sent in response to a previous prepare message.
 */

public interface PreparedMessage extends TransactionMessage
{

    /**
     * Tests if the participant is done.
     * 
     * @return boolean True iff the remote participant requires no further
     *         notification.
     */

    public boolean isReadOnly ();

    /**
     * Tests if the participant will automatically rollback.
     * 
     * @return boolean True iff the remote participant will rollback on timeout
     *         by itself.
     */

    public boolean defaultIsRollback ();
}
