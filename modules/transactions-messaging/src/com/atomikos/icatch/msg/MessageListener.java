//$Id: MessageListener.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: MessageListener.java,v $
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
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.2  2005/07/29 13:08:18  guy
//Completed and tested.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.2  2002/11/05 09:33:31  guy
//Changed: MsgListener adds Transport to callback, and CommitServer
//now listens on TS start/stop events.
//
//Revision 1.1.2.1  2002/10/31 16:06:48  guy
//Added basic message framework for 2PC over message systems.
//

package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A callback interface to allow notification of messages received.
 * 
 */

public interface MessageListener
{
    /**
     * The callback method, called by the transport when a message is received
     * for this listener.
     * 
     * @param msg
     *            The message that was received.
     * @param The
     *            transport for the message.
     * @return boolean False if the listener wants no more messages (and can be
     *         removed).
     */

    public boolean messageReceived ( TransactionMessage msg ,
            Transport transport );
}
