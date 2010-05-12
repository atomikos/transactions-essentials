//$Id: Transport.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: Transport.java,v $
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
//Revision 1.4  2005/10/03 11:46:03  guy
//Added support for WS-T registration (2PC and volatile).
//
//Revision 1.3  2005/09/20 07:28:25  guy
//Updated message address to Object (not String) to allow for WS-Addressing
//to be used.
//
//Revision 1.2  2005/08/05 15:03:47  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.3  2005/07/28 12:43:42  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.2  2005/07/25 11:47:23  guy
//Refactored: removed MessageFactory (methods moved to Transport).
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.6  2002/11/13 17:46:51  guy
//Redesigned to make the commit protocol explicit in the core.
//
//Revision 1.1.2.5  2002/11/07 12:53:07  guy
//Tuned a bit.
//
//Revision 1.1.2.4  2002/11/07 10:00:12  guy
//Tuned messaging framework.
//
//Revision 1.1.2.3  2002/11/05 11:01:30  guy
//Corrected design a bit.
//
//Revision 1.1.2.2  2002/11/05 09:33:31  guy
//Changed: MsgListener adds Transport to callback, and CommitServer
//now listens on TS start/stop events.
//
//Revision 1.1.2.1  2002/10/31 16:06:49  guy
//Added basic message framework for 2PC over message systems.
//

package com.atomikos.icatch.msg;

import com.atomikos.datasource.RecoverableResource;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A generic transport interface. A Transport creates, sends and receives
 * messages and has callback mechanisms for message listeners. It is used by the
 * MessageParticipant. Message delivery is NOT reliable: messages can be lost
 * and are discarded if nobody is listening for them or waiting in a receive.
 * Note that a transport is also a recoverable resource, since
 * MessageParticipants ask the transport whether they can be recovered or not.
 * Only the transport knows if it can handle the protocol and wire protocol of a
 * given MessageParticipant.
 */

public interface Transport extends RecoverableResource
{
    /**
     * Constant indicating an unknown transport protocol.
     */

    public static final int UNKNOWN_PROTOCOL = -1;

    /**
     * Constant indicating a HTTP transport protocol.
     */

    public static final int HTTP = 0;

    /**
     * Constant indicating SMTP transport protocol.
     */

    public static final int SMTP = 1;

    /**
     * Constant indicating a JMS transport protocol.
     */

    public static final int JMS = 2;

    /**
     * Constant indicating FTP transport protocol.
     */

    public static final int FTP = 3;

    /**
     * The protocol that this transport understands.
     * 
     * @return int The protocol, as defined in CommitProtocol.
     */

    public int getCommitProtocol ();

    /**
     * Get the message format that this transport understands.
     * 
     * @return int The message format, as defined in TransactionMessage.
     */

    public int getFormat ();

    /**
     * Get the transport protocol that this transport understands.
     * 
     * @return int The transport protocol. One of the predefined constants.
     */

    public int getTransportProtocol ();

    /**
     * Get the address on which this transport is listening for incoming
     * coordinator messages.
     * 
     * @return String The address.
     */

    public String getCoordinatorAddress ();

    /**
     * Get the address on which this transport is listening for incoming
     * participant messages.
     * 
     * @return String The address.
     */
    public String getParticipantAddress ();

    /**
     * Get a realistic default timeout for the given transport.
     * 
     * @return long The timeout in milliseconds. After this time, a message may
     *         be considered lost in the system.
     */

    public long getDefaultTimeout ();

    /**
     * Send and receive a message for the given target.
     * 
     * @param msg
     *            The message to send.
     * @param expected
     *            The types of messages expected. Other messages are ignored.
     *            The values should be of TransactionMessage's predefined types.
     * 
     * 
     * @param timeout
     *            The timeout in milliseconds after which the method will
     *            return, received or not.
     * @return TransactionMessage The message, or null if timedout.
     * @exception IllegalMessageTypeException
     *                If the message sent is of the wrong type.
     */

    public TransactionMessage sendAndReceive ( TransactionMessage msg ,
            long timeout , int[] expected ) throws TransportException,
            IllegalMessageTypeException;

    /**
     * Send a message.
     * 
     * @param msg
     *            The message to send.
     * @exception IllegalMessageTypeException
     *                If the message sent is of the wrong type.
     * @exception TransportException
     *                On failure.
     */

    public void send ( TransactionMessage msg ) throws TransportException,
            IllegalMessageTypeException;

    /**
     * Register a message listener for unexpected receivals. This method should
     * work even before recover is called.
     * 
     * @param listener
     *            The message listener.
     * @exception TransportException
     *                On failure.
     * 
     */

    public void registerMessageListener ( MessageListener listener )
            throws TransportException;

    /**
     * Register a message listener for incoming messages of the given type. This
     * method should work even before recover is called.
     * 
     * @param listener
     *            The listener.
     * @param messageType
     *            The type to listen for.
     * @exception TransportException
     *                On failure.
     */

    public void registerMessageListener ( MessageListener listener ,
            int messageType ) throws TransportException;

    /**
     * Remove a previously registered listener. This method should work even
     * <b>after</b> close.
     * 
     * @param listener
     *            The message listener.
     * @exception TransportException
     *                On failure.
     */

    public void removeMessageListener ( MessageListener listener )
            throws TransportException;

    /**
     * Create a new commit message.
     */

    public CommitMessage createCommitMessage ( boolean onephase ,
            String senderURI , String targetURI , Object targetAddress );

    /**
     * Create a new error message.
     */

    public ErrorMessage createErrorMessage ( int code , String senderURI ,
            String targetURI , Object targetAddress );

    /**
     * Create a new forget message.
     */

    public ForgetMessage createForgetMessage ( String senderURI ,
            String targetURI , Object targetAddress );

    /**
     * Create a new prepare message.
     */

    public PrepareMessage createPrepareMessage ( String senderURI ,
            String targetURI , Object targetAddress );

    /**
     * Create a new prepare message.
     */

    public PrepareMessage createPrepareMessage ( int globalSiblingCount ,
            CascadeInfo[] cascadeInfo , String senderURI , String targetURI ,
            Object targetAddress );

    /**
     * Create a new prepared message. The option default_rollback is not used by
     * our software, so we do not allow setting it to other than the default
     * value.
     */

    public PreparedMessage createPreparedMessage ( boolean readonly ,
            String senderURI , String targetURI , Object targetAddress );

    /**
     * Create a new replay message.
     */

    public ReplayMessage createReplayMessage ( String senderURI ,
            String targetURI , Object targetAddress );

    /**
     * Create a new rollback message.
     */

    public RollbackMessage createRollbackMessage ( String senderURI ,
            String targetURI , Object targetAddress );

    /**
     * Create a new state message.
     */

    public StateMessage createStateMessage ( Boolean committed ,
            String senderURI , String targetURI , Object targetAddress );

    /**
     * Create a new registration message.
     */
    public RegisterMessage createRegisterMessage ( String senderURI ,
            String targetURI , Object targetAddress , boolean for2PC );

    /**
     * Create a new registration confirmation message.
     * 
     */
    public RegisteredMessage createRegisteredMessage ( String senderURI ,
            String targetURI , Object targetAddress );
}
