//$Id: RollbackMessage.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: RollbackMessage.java,v $
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
//Revision 1.2  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//
//Revision 1.1  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * A rollback message implemenation.
 */

class RollbackMessage extends PropagationMessage
{
    protected int retrycount_ = 0;
    // no of retries so far

    protected boolean indoubt_ = false;

    // true if participant can be indoubt.

    public RollbackMessage ( Participant participant , Result result ,
            boolean indoubt )
    {
        super ( participant , result );
        indoubt_ = indoubt;
    }

    /**
     * A rollback message.
     * 
     * @return Object An array of heuristic messages.
     * @exception PropagationException
     *                If problems. If heuristics, this will be a fatal
     *                exception; otherwise, rollback has to be retried since
     *                participant can be indoubt. In that case, the error is
     *                transient in nature.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        HeuristicMessage[] msgs = null;
        try {
            msgs = part.rollback ();

        } catch ( HeurCommitException heurc ) {
            // System.err.println ( "RollbackMessage: heur commit detected" );
            throw new PropagationException ( heurc, false );
        } catch ( HeurMixedException heurm ) {
            throw new PropagationException ( heurm, false );
        }

        catch ( Exception e ) {
            // only retry if might be indoubt. Otherwise ignore.
            if ( indoubt_ ) {
                // here, participant might be indoubt!
                // fill in exact heuristic msgs by using buffered effect
                // of proxies
                HeurHazardException heurh = new HeurHazardException ( part
                        .getHeuristicMessages () );
                throw new PropagationException ( heurh, true );
            }
        }
        return msgs;
    }

    public String toString ()
    {
        return ("RollbackMessage to " + getParticipant ());
    }

}
