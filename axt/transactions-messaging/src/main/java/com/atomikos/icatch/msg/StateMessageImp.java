package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable implementation of a state message.
 */

public class StateMessageImp extends AbstractMessage implements StateMessage
{
    private Boolean committed_;

    // private boolean coordinator_;

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
     * @param committed
     *            Null if not known, True/False otherwise.
     */

    public StateMessageImp ( int protocol , int format , Object targetAddress ,
            String targetURI , Object senderAddress , String senderURI ,
            Boolean committed )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        committed_ = committed;
        // coordinator_ = coordinator;
    }

    /**
     * @see StateMessage
     */

    public Boolean hasCommitted ()
    {
        return committed_;
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.STATE_MESSAGE;
    }

}
