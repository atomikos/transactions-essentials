package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable implementation of a prepared msg.
 */

public class PreparedMessageImp extends AbstractMessage implements
        PreparedMessage
{
    private boolean readonly_;

    private boolean defaultRollback_;

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
     * @param readOnly
     *            True if read only.
     * @param defaultIsRollback
     *            True if default timeout means rollback.
     */

    public PreparedMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI , boolean readOnly , boolean defaultIsRollback )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        readonly_ = readOnly;
        defaultRollback_ = defaultIsRollback;
    }

    /**
     * @see PreparedMessage
     */

    public boolean isReadOnly ()
    {
        return readonly_;
    }

    /**
     * @see PreparedMessage
     */

    public boolean defaultIsRollback ()
    {
        return defaultRollback_;
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.PREPARED_MESSAGE;
    }

}
