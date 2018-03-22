/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * A participant that wants to be notified of local termination of a node in a 
 * nested transaction tree. 
 */

public interface SubTxAwareParticipant
{
    /**
     * Notification of termination.
     *
     * @param transaction The composite transaction that has terminated
     * locally at its node.
     */

     void committed ( CompositeTransaction transaction );
    
    /**
     * Notification that some transaction has been rolledback.
     *
     * @param parent The transaction that has rolled back at its node.
     */

     void rolledback ( CompositeTransaction transaction );
}
