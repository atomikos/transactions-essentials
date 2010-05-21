package com.atomikos.icatch.msg;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.SubTxAwareParticipant;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A subtx aware implementation that wraps a MessageParticipant.
 * 
 * 
 */

public class SubTxAwareMessageParticipant implements SubTxAwareParticipant
{
    private MessageParticipant mp;

    public SubTxAwareMessageParticipant ( MessageParticipant mp )
    {
        super ();
        this.mp = mp;
    }

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant#committed(com.atomikos.icatch.CompositeTransaction)
     */
    public void committed ( CompositeTransaction tx )
    {
        try {
            // WS-T requires prepare phase already
            // ignore answer, just let prepare repeat
            // during actual 2PC (prepare must be idempotent
            // according to specs!)
            mp.prepare ();
        } catch ( Exception e ) {
            // ignore; this is only a courtesy
        }

    }

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant#rolledback(com.atomikos.icatch.CompositeTransaction)
     */
    public void rolledback ( CompositeTransaction tx )
    {
        // do nothing here

    }

}
