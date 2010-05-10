//$Id: Reply.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: Reply.java,v $
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
//Revision 1.1  2001/02/28 18:09:43  pardon
//Implemented classes for CoordinatorImp: propagation logic.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * A reply for propagationmessages.
 */

class Reply
{
    protected Exception exception_ = null;

    protected Object response_ = null;

    protected Participant participant_ = null;

    protected boolean retried_ = false;

    /**
     * Constructor.
     * 
     * @param response
     *            The response value.
     * @param exception
     *            The exception, or null if none.
     * @param Participant
     *            The participant whose reply this is.
     * @param retried
     *            If true, then the original messages has failed but is
     *            reschuled for retry.
     */

    public Reply ( Object response , Exception exception ,
            Participant participant , boolean retried )
    {
        response_ = response;
        exception_ = exception;
        participant_ = participant;
        retried_ = retried;
    }

    /**
     * Check if response ok.
     * 
     * @return boolean True if response is invalid. In that case, getException()
     *         returns the error.
     */

    public boolean hasFailed ()
    {
        return exception_ != null;
    }

    /**
     * To check if retried for failure case.
     * 
     * @return boolean True if the original message will be retried.
     */

    public boolean isRetried ()
    {
        return hasFailed () && retried_;
    }

    /**
     * Get any errors. Not null if hasFailed() is true.
     * 
     * @return Exception The exception.
     */

    public Exception getException ()
    {
        return exception_;
    }

    /**
     * Get the response. OK if hasFailed() returns false.
     * 
     * @return Object Application specific; can be null.
     */

    public Object getResponse ()
    {

        return response_;
    }

    /**
     * Get the participant who replied this.
     * 
     * @return Participant.
     */

    public Participant getParticipant ()
    {
        return participant_;
    }
}
