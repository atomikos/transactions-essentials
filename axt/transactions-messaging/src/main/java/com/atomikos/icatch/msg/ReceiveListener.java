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
