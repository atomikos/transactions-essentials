package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A prepare message.
 */

public interface PrepareMessage extends TransactionMessage
{
    /**
     * Check if the message is carrying information for orphan detection.
     * 
     * @return boolean True iff the message has info concerning orphan
     *         detection.
     */

    public boolean hasOrphanInfo ();

    /**
     * Get the globally detected no of invocations for the target participant.
     * This method should only be called if hasOrphanInfo returns true.
     * 
     * @return int The globally detected no of invocations for the target
     *         participant.
     */

    public int getGlobalSiblingCount ();

    /**
     * Get the orphan detection information to cascade. This method should only
     * be called if hasOrphanInfo returns true.
     * 
     * @return CascadeInfo[] The cascade info.
     */

    public CascadeInfo[] getCascadeInfo ();
    


}
