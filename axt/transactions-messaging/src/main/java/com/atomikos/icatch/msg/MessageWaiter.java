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
