//$Id: PrepareMessageImp.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: PrepareMessageImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/09/20 07:28:25  guy
//Updated message address to Object (not String) to allow for WS-Addressing
//to be used.
//
//Revision 1.3  2005/08/31 14:34:40  guy
//Changed URI mechanism: an imported coordinator gets a global URI that is
//a combination of its root ID (needed to detect orphans) and its
//globally unique participant address. Otherwise, orphan detection will
//not work: all related transactions would have the same URI!
//
//Revision 1.2  2005/08/05 15:03:47  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.2  2005/07/28 12:43:42  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.2  2002/11/07 10:00:11  guy
//Tuned messaging framework.
//
//Revision 1.1.2.1  2002/11/05 12:07:51  guy
//Added default implementations of all message types.
//

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
