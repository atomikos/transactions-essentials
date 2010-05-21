package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002-2009, Atomikos. All rights reserved.
 * 
 * A base implementation of all message classes.
 */

public abstract class AbstractMessage implements TransactionMessage
{

    // private Object msg_;
    // //the implementation-specific wrapped msg

    private int protocol_;

    private int format_;

    private Object senderAddress_;

    private String senderURI_;

    private Object targetAddress_;

    private String targetURI_;

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

    public AbstractMessage ( int protocol , int format , Object targetAddress ,
            String targetURI , Object senderAddress , String senderURI )
    {
        // msg_ = message;
        protocol_ = protocol;
        format_ = format;
        targetAddress_ = targetAddress;
        targetURI_ = targetURI;
        senderAddress_ = senderAddress;
        senderURI_ = senderURI;
    }

    /**
     * @see TransactionMessage
     */

    public int getProtocol ()
    {
        return protocol_;
    }

    /**
     * @see TransactionMessage
     */

    public int getFormat ()
    {
        return format_;
    }

    // /**
    // *@see TransactionMessage
    // */
    //    
    // public String getContentAsText()
    // {
    // return msg_.toString();
    // }

    /**
     * @see TransactionMessage
     */

    public Object getTargetAddress ()
    {
        return targetAddress_;
    }

    /**
     * @see TransactionMessage
     */

    public String getTargetURI ()
    {
        return targetURI_;
    }

    /**
     * @see TransactionMessage
     */

    public Object getSenderAddress ()
    {
        return senderAddress_;
    }

    /**
     * @see TransactionMessage
     */

    public String getSenderURI ()
    {
        return senderURI_;
    }

    public String toString ()
    {
        StringBuffer ret = new StringBuffer ();
        ret.append ( "MESSAGETYPE: " + getMessageType () );
        ret.append ( " " );
        ret.append ( "FROM : " );
        ret.append ( getSenderURI () );
        ret.append ( " AT ADDRESS: " );
        ret.append ( getSenderAddress () );
        ret.append ( " TO: " );
        ret.append ( getTargetURI () );
        ret.append ( " AT ADDRESS: " );
        ret.append ( getTargetAddress () );
        return ret.toString ();
    }
}
