package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A reusable prepare message implementation.
 * 
 */

public class PrepareMessageImp extends AbstractMessage implements
        PrepareMessage
{
    private int globalSiblingCount_;

    private CascadeInfo[] cascadeInfo_;

    /**
     * Create a new instance. If this constructor is used, orphan info will not
     * be present.
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

    public PrepareMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );
        cascadeInfo_ = null;
    }

    /**
     * Create a new instance. If this constructor is used, orphan info will be
     * present.
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
     * @param globalSiblingCount
     *            The global count.
     * @param cascadeInfo
     *            The info for orphan detection.
     */

    public PrepareMessageImp ( int protocol , int format ,
            Object targetAddress , String targetURI , Object senderAddress ,
            String senderURI , int globalSiblingCount ,
            CascadeInfo[] cascadeInfo )
    {
        super ( protocol , format , targetAddress , targetURI , senderAddress ,
                senderURI );

        globalSiblingCount_ = globalSiblingCount;
        cascadeInfo_ = cascadeInfo;
    }

    /**
     * @see PrepareMessage
     */

    public boolean hasOrphanInfo ()
    {
        return cascadeInfo_ != null;
    }

    /**
     * @see PrepareMessage
     */

    public int getGlobalSiblingCount ()
    {
        return globalSiblingCount_;
    }

    /**
     * @see PrepareMessage
     */
    public CascadeInfo[] getCascadeInfo ()
    {
        return cascadeInfo_;
    }

    /**
     * @see TransactionMessage
     */

    public int getMessageType ()
    {
        return TransactionMessage.PREPARE_MESSAGE;
    }

    public String toString ()
    {
        StringBuffer ret = new StringBuffer ();
        ret.append ( super.toString () );

        // add orphan info if any
        if ( hasOrphanInfo () && cascadeInfo_.length > 0 ) {
            ret.append ( " with orphan information: \n" );
            for ( int i = 0; i < cascadeInfo_.length; i++ ) {
                ret.append ( "Participant: " + cascadeInfo_[i].participant );
                ret.append ( " with count: " + cascadeInfo_[i].count );
                ret.append ( "\n" );
            }
        }
        return ret.toString ();
    }

}
