//$Id: ForgetMessage.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: ForgetMessage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2002/03/06 16:21:09  guy
//Adapted forget mechanism to include waiting for result. Otherwise, forget
//is not propagated due to TERMINATED state.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.2  2001/03/07 18:57:36  pardon
//Continued on CoordinatorImp.
//
//Revision 1.1  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * A forget message implemenation.
 */

class ForgetMessage extends PropagationMessage
{

    ForgetMessage ( Participant participant )
    {
        super ( participant , null );
    }

    ForgetMessage ( Participant p , ForgetResult result )
    {
        super ( p , result );
    }

    /**
     * A forget message.
     * 
     * @return Object The participant to whom this was sent.
     * @exception PropagationException
     *                Never returned; we don't care now.
     */

    protected Object send () throws PropagationException
    {
        try {
            Participant part = getParticipant ();
            part.forget ();

        } catch ( Exception e ) {
        }

        return getParticipant ();
    }

    public String toString ()
    {
        return ("ForgetMessage to " + getParticipant ());
    }

}
