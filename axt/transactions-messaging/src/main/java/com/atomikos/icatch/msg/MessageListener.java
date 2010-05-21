package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A callback interface to allow notification of messages received.
 * 
 */

public interface MessageListener
{
    /**
     * The callback method, called by the transport when a message is received
     * for this listener.
     * 
     * @param msg
     *            The message that was received.
     * @param The
     *            transport for the message.
     * @return boolean False if the listener wants no more messages (and can be
     *         removed).
     */

    public boolean messageReceived ( TransactionMessage msg ,
            Transport transport );
}
