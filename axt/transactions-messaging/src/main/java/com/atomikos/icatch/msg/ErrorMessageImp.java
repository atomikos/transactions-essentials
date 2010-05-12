//$Id: ErrorMessageImp.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: ErrorMessageImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
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
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/09/20 07:28:25  guy
//Updated message address to Object (not String) to allow for WS-Addressing
//to be used.
//
//Revision 1.2  2005/08/05 15:03:46  guy
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
