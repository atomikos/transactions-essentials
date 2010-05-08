//$Id: PrepareMessage.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: PrepareMessage.java,v $
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
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.2  2001/03/07 18:57:36  pardon
//Continued on CoordinatorImp.
//
//Revision 1.1  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;

/**
 * 
 * 
 * A prepare message implemenation.
 */

class PrepareMessage extends PropagationMessage
{

    public PrepareMessage ( Participant participant , Result result )
    {
        super ( participant , result );
    }

    /**
     * A prepare message.
     * 
     * @return Object Boolean True if YES vote, False if NO vote, null if
     *         readonly vote.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        int ret = 0;
        Boolean result = null;
        try {
            ret = part.prepare ();
            if ( ret == Participant.READ_ONLY )
                result = null;
            else
                result = new Boolean ( true );
        } catch ( HeurHazardException heurh ) {
            throw new PropagationException ( heurh, false );
        } catch ( RollbackException jtr ) {
            // NO vote.
            result = new Boolean ( false );
        } catch ( Exception e ) {
            // here, participant might be indoubt!
            StringHeuristicMessage shm = new StringHeuristicMessage (
                    "Possibly heuristic participant: " + part.toString () );
            HeuristicMessage[] msgs = new HeuristicMessage[1];
            msgs[0] = shm;
            HeurHazardException heurh = new HeurHazardException ( msgs );
            throw new PropagationException ( heurh, false );

        }
        return result;
    }

    public String toString ()
    {
        return ("PrepareMessage to " + getParticipant ());
    }

}
