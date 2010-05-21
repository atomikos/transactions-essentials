package com.atomikos.icatch;

/**
 *
 *
 *A participant that wants to be notified of local termination of a node in a 
 *nested transaction tree. 
 */

public interface SubTxAwareParticipant 
extends java.io.Serializable
{
    /**
     *Notification of termination.
     *
     *@param tx The composite transaction that has terminated
     *locally at its node.
     */

    public void committed ( CompositeTransaction tx );
    
    /**
     *Notification that some tx has been rolledback.
     *
     *@param parent The tx that has rolled back at its node.
     */

    public void rolledback ( CompositeTransaction tx );
}
