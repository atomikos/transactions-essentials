//$Id: ReceiveListener.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: ReceiveListener.java,v $
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
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/05 15:03:47  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.2  2005/07/30 10:49:43  guy
//Changed to return boolean on messageReceived.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.3  2002/11/07 18:30:39  guy
//Synchronized on listener, not on transport. More elegant.
//
//Revision 1.1.2.2  2002/11/07 17:58:19  guy
//Corrected after testing misery.
//
//Revision 1.1.2.1  2002/11/07 10:00:11  guy
//Tuned messaging framework.
//

package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A listener implementation for synchronous receives.
 */

public class ReceiveListener implements MessageListener
{
    private TransactionMessage msg_;
    // the message after receival

    private String target_;
    // the target that is waiting. Only a message
    // for this target will be accepted.

    private long timeout_;

    private Transport transport_;

    /**
     * Create a new one.
     * 
     * @param target
     *            The target URI that the incoming message must match.
     * @param timeout
     *            The timeout in millis.
     */

    public ReceiveListener ( Transport t , String target , long timeout )
    {
        target_ = target;
        msg_ = null;
        timeout_ = timeout;
        transport_ = t;
    }

    /**
     * @see MessageListener
     */

    public boolean messageReceived ( TransactionMessage msg , Transport t )
    {
        synchronized ( this ) {
            if ( msg.getTargetURI ().equals ( target_ ) ) {
                msg_ = msg;
                this.notifyAll ();
            }
        }
        return true;
    }

    /**
     * Get the reply. This method will block for the timeout if none is
     * available.
     */

    public TransactionMessage getMessage ()
    {

        long start = System.currentTimeMillis ();
        synchronized ( this ) {
            try {
                if ( msg_ == null ) {
                    this.wait ( timeout_ );
                }
            } catch ( InterruptedException e ) {

            }
        }

        return msg_;
    }
}
