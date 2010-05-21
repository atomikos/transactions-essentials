package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * Implementation of registered message.
 * 
 * 
 */
public class RegisteredMessageImp extends AbstractMessage implements
        RegisteredMessage
{

    /**
     * @param protocol
     * @param format
     * @param targetAddress
     * @param targetURI
     * @param senderAddress
     * @param senderURI
     */
    public RegisteredMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );

    }

    /**
     * @see com.atomikos.icatch.msg.TransactionMessage#getMessageType()
     */
    public int getMessageType ()
    {
        return TransactionMessage.REGISTERED_MESSAGE;
    }

}
