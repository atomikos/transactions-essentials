package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * This interface allows the transport to be agnostic of how to send messages.
 * Needed to separate the core from the actual implementation classes.
 * 
 * 
 */
public interface SenderPort
{
    /**
     * Sends the given message.
     * 
     * @param msg
     * @throws TransportException
     * @throws IllegalMessageTypeException
     */
    public void send ( TransactionMessage msg ) throws TransportException,
            IllegalMessageTypeException;

}
