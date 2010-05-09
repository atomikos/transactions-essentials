//$Id: SubTxAwareMessageParticipant.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: SubTxAwareMessageParticipant.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:49  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/10/03 11:46:03  guy
//Added support for WS-T registration (2PC and volatile).
//
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
