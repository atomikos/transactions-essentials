//$Id: SenderPort.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: SenderPort.java,v $
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
//Revision 1.2  2006/03/15 10:31:49  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/08/27 11:28:17  guy
//Added delegation model for sending (to avoid that the core
//classes would need the AXIS implementation classes in the global classpath)
//
package com.atomikos.icatch.msg;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * This interface allows the transport to be agnostic of how to send messages.
 * Needed to separate the core from the actual implementation classes.
 * 
 * 
 */
public interface SenderPort
{
    /**
     * Sends the given message.
     * 
     * @param msg
     * @throws TransportException
     * @throws IllegalMessageTypeException
     */
    public void send ( TransactionMessage msg ) throws TransportException,
            IllegalMessageTypeException;

}
