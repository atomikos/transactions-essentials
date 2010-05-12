//$Id: MessageWaiter.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: MessageWaiter.java,v $
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
//Revision 1.3  2005/08/15 21:37:59  guy
//Corrected bug and added logging.
//
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.2.1  2005/07/28 12:43:42  guy
//Completed Axis implementation of 2PC/SOAP.
//
package com.atomikos.icatch.msg;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * A utility class to wait for reply messages.
 * 
 */

class MessageWaiter
{
    private int[] expectedReplies;

    private TransactionMessage receivedReply;

    public MessageWaiter ( int[] expectedReplies )
    {
        this.expectedReplies = expectedReplies;
    }

    /**
     * Wait for the reply.
     * 
     * @param timeout
     * @return The reply, or null if none.
     */
    synchronized TransactionMessage waitForReply ( long timeout )
    {
        TransactionMessage ret = null;
        if ( receivedReply == null ) {
            try {
                this.wait ( timeout );
            } catch ( InterruptedException e ) {
                // ignore: treat as timeout
                Configuration.logDebug ( "Error in waiting for reply.", e );
            }
        }
        ret = receivedReply;
        receivedReply = null;
        return ret;
    }

    /**
     * Notify the waiter of message receipt. This will wake the waiting thread
     * that is blocked.
     * 
     * @param msg
     *            The message received.
     */
    synchronized void messageReceived ( TransactionMessage msg )
    {
        boolean expected = false;
        for ( int i = 0; i < expectedReplies.length; i++ ) {
            if ( msg.getMessageType () == expectedReplies[i] )
                expected = true;
        }
        if ( expected ) {
            receivedReply = msg;
            this.notifyAll ();
        }
    }
}
