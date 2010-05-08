//$Id: CommitMessage.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: CommitMessage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
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
//Revision 1.5  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/11/05 13:10:52  guy
//Added log output when unexpected error happens.
//
//Revision 1.2  2004/10/12 13:03:25  guy
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

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A commit message implemenation.
 */

class CommitMessage extends PropagationMessage
{
    protected boolean onephase_ = false;

    protected int retrycount_ = 0;

    // no of tries retry is done

    public CommitMessage ( Participant participant , Result result ,
            boolean onephase )
    {
        super ( participant , result );
        onephase_ = onephase;
    }

    /**
     * A commit message.
     * 
     * @return Object An array of heuristic messages.
     * @exception PropagationException
     *                If problems. If heuristics, this will be a heuristic
     *                exception; otherwise, commit has to be retried since
     *                participant can be indoubt. Hence, if not heuristic in
     *                nature, then the error is transient.
     */

    protected Object send () throws PropagationException
    {
        Participant part = getParticipant ();
        HeuristicMessage[] msgs = null;
        try {
            msgs = part.commit ( onephase_ );
            return msgs;
        } catch ( RollbackException rb ) {
            throw new PropagationException ( rb, false );
        } catch ( HeurMixedException heurm ) {
            throw new PropagationException ( heurm, false );
        } catch ( HeurRollbackException heurr ) {
            throw new PropagationException ( heurr, false );
        } catch ( Exception e ) {
            // heuristic hazard or not, participant might be indoubt.
            // fill in exact heuristic messages by using buffer effect
            // of participant proxies.
            String msg = "Unexpected error in commit";
            Configuration.logWarning ( msg, e );
            HeurHazardException heurh = new HeurHazardException ( part
                    .getHeuristicMessages () );
            throw new PropagationException ( heurh, true );
        }
    }

    public String toString ()
    {
        return ("CommitMessage to " + getParticipant ());
    }

}
