package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A registration message for registering participants (needed for protocols
 * such as WS-T).
 * <p>
 * <b>The targetURI is supposed to be the tid of the corresponding
 * CompositeTransaction instance!</b>
 * 
 * 
 */
public interface RegisterMessage extends TransactionMessage
{

    /**
     * Indicates if the registration is for 2PC or rather for synchronization.
     * 
     * @return True if for 2PC, false if for synchronization.
     */
    public boolean registerForTwo2PC ();
  

}
