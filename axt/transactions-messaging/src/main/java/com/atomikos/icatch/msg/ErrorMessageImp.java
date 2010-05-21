package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable implementation of an error message.
 */

public class ErrorMessageImp extends AbstractMessage implements ErrorMessage
{
    private int code_;

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
     * @param code
     *            The error code.
     */

    public ErrorMessageImp ( int protocol , int format , Object targetAddress ,
            String targetURI , Object senderAddress , String senderURI ,
            int code )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        code_ = code;
    }

    /**
     * @see ErrorMessage
     */

    public int getErrorCode ()
    {
        return code_;
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.ERROR_MESSAGE;
    }

}
