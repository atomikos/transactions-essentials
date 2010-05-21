package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable forget message implementation.
 */

public class ForgetMessageImp extends AbstractMessage implements ForgetMessage
{

    /**
     * Create a new instance.
     * 
     * @param protocol
     *            The commit protocol.
     * @param format
     *            The format of the message.
     * @param targetAddress
     *            Where to send it.
     * @param targetURI
     *            For which target object this is.
     * @param senderAddress
     *            Who is sending this.
     * @param senderURI
     *            Which object is sending this.
     */

    public ForgetMessageImp ( int protocol , int format , Object targetAddress ,
            String targetURI , Object senderAddress , String senderURI )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.FORGET_MESSAGE;
    }
}
