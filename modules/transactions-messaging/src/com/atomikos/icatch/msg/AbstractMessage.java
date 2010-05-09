//$Id: AbstractMessage.java,v 1.1.1.1 2006/10/02 15:21:14 guy Exp $
//$Log: AbstractMessage.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:14  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:24:02  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/11/01 09:10:21  guy
//Improved/debugged during testing.
//
//Revision 1.3  2005/09/20 07:28:25  guy
//Updated message address to Object (not String) to allow for WS-Addressing
//to be used.
//
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.3  2005/07/29 13:08:18  guy
//Completed and tested.
//
//Revision 1.1.4.2  2005/07/28 12:43:41  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.1  2002/11/05 12:07:51  guy
//Added default implementations of all message types.
//

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
