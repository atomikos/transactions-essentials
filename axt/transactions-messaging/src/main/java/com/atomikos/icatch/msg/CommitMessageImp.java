package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable implementation of a commit message.
 */

public class CommitMessageImp extends AbstractMessage implements CommitMessage
{
    private boolean onePhase_;

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
     * @param onePhase
     *            Is this a one phase commit or not?
     */

    public CommitMessageImp ( int protocol , int format , Object targetAddress ,
            String targetURI , Object senderAddress , String senderURI ,
            boolean onePhase )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        onePhase_ = onePhase;
    }

    /**
     * @see CommitMessage
     */

    public boolean isOnePhase ()
    {
        return onePhase_;
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.COMMIT_MESSAGE;
    }
}
