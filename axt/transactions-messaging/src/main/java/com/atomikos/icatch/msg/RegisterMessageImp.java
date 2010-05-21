package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * 
 * 
 */
public class RegisterMessageImp extends AbstractMessage implements
        RegisterMessage
{

    private boolean for2PC;

    /**
     * @param protocol
     * @param format
     * @param targetAddress
     * @param targetURI
     * @param senderAddress
     * @param senderURI
     * @param for2PC
     */
    public RegisterMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI , boolean for2PC )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        this.for2PC = for2PC;
    }

    /**
     * @see com.atomikos.icatch.msg.TransactionMessage#getMessageType()
     */
    public int getMessageType ()
    {
        return TransactionMessage.REGISTER_MESSAGE;
    }

    /**
     * @see com.atomikos.icatch.msg.RegisterMessage#registerForTwo2PC()
     */
    public boolean registerForTwo2PC ()
    {
        return for2PC;
    }

}
