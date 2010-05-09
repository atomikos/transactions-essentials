//$Id: TransactionMessage.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: TransactionMessage.java,v $
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
//Revision 1.1.4.2  2005/07/28 12:43:42  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.5  2002/11/13 17:46:51  guy
//Redesigned to make the commit protocol explicit in the core.
//
//Revision 1.1.2.4  2002/11/07 10:00:11  guy
//Tuned messaging framework.
//
//Revision 1.1.2.3  2002/11/05 12:07:52  guy
//Added default implementations of all message types.
//
//Revision 1.1.2.2  2002/11/05 11:01:30  guy
//Corrected design a bit.
//
//Revision 1.1.2.1  2002/10/31 16:06:49  guy
//Added basic message framework for 2PC over message systems.
//

package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002-2009, Atomikos. All rights reserved.
 * 
 * A TransactionMessage is a generic type for two-phase commit related messages
 * exchanged in a loosely-coupled environment.
 */

public interface TransactionMessage
{
 
    
    /**
     * Constant indicating that the message SOAP formatted.
     */

    public static final int FORMAT_SOAP = 2;

    /**
     * Constant indicating that the message is in unknown format.
     */

    public static final int FORMAT_UNKNOWN = 3;

    /**
     * Constant indicating a commit message.
     */

    public static final int COMMIT_MESSAGE = 5;

    /**
     * Constant indicating an error message.
     */

    public static final int ERROR_MESSAGE = 6;

    /**
     * Constant indicating a forget message.
     */

    public static final int FORGET_MESSAGE = 7;

    /**
     * Constant indicating a prepare message.
     */

    public static final int PREPARE_MESSAGE = 8;

    /**
     * Constant indicating a prepared message.
     */

    public static final int PREPARED_MESSAGE = 9;

    /**
     * Constant indicating a replay message.
     */

    public static final int REPLAY_MESSAGE = 10;

    /**
     * Constant indicating a rollback message.
     */

    public static final int ROLLBACK_MESSAGE = 11;

    /**
     * Constant indicating a state message.
     */

    public static final int STATE_MESSAGE = 12;

    /**
     * Constant indicating a registration message.
     */
    public static final int REGISTER_MESSAGE = 13;

    /**
     * Constant indicating a registration response message.
     */
    public static final int REGISTERED_MESSAGE = 14;

    /**
     * Get the protocol identifier.
     * 
     * @return int One of the CommitProtocol values.
     */

    public int getProtocol ();

    /**
     * Get the format identifier.
     * 
     * @return int One of the FORMAT_* values.
     */

    public int getFormat ();

    // /**
    // *Get the content of the message expressed as text.
    // *@return String The message in text.
    // */
    //    
    // public String getContentAsText();

    /**
     * Get the destination address of the message.
     * 
     * The target address is opaque
     * to the messaging package; its concrete content is only known by
     * the protocol-specific wire-level code. The messaging package
     * calls this method when processing a registration message:
     * whereas the senderAddress indicates where to reply to, 
     * the targetAddress is expected to be the contact address
     * information (to be used by two-phase commit) for reaching 
     * the remote participant that is registering.
     * 
     * @return Object The destination address.
     */

    public Object getTargetAddress ();

    /**
     * Get the URI of the target object of the message.
     * This value is closely related to the senderURI.
     * 
     * 
     * @return String The globally unique target object URI.
     */

    public String getTargetURI ();

    /**
     * Get the sender address for the message. The sender address is opaque
     * to the messaging package; its concrete content is only known by
     * the protocol-specific wire-level code.
     * 
     * The messaging code ensures, however, that all reply messages (created
     * for incoming two-phase request messages) have a
     * targetAddress corresponding to the senderAddress of the request.
     * 
     * 
     * @return The sender address.
     */

    public Object getSenderAddress ();

    /**
     * Get the sender URI for the message. The senderURI
     * is assumed to uniquely identify the sending 
     * transaction object for a request or reply message.
     * 
     * The senderURI is related to the targetURI in the
     * 2-phase commit conversations:
     * <ul>
     * <li>For <em>outgoing 2PC requests</em>, 
     *     senderURI and targetURI are the same 
     * 	   and correspond to the URI of the remote participant.
     * </li>
     * <li>
     * 	   For <em>incoming 2PC replies</em>,
     *     the targetURI should be the same as the
     *     senderURI of the original request, or 
     *     the messaging code in this package will not work.
     * </li>
     * <li>
     *     For <em>incoming 2PC requests</em>,
     *     the targetURI should be the URI of a local participant.
     *     The senderURI is opaque and used only for setting the
     *     right correlation info as the targetURI of the reply.
     * </li>
     * <li>
     * 	  For <em>outgoing 2PC replies</em> 
     *    (including registration confirmation),
     * 	  the senderURI corresponds to the original 
     *    targetURI found in the incoming request message and
     *    the targetURI is the senderURI of the incoming request
     *    message.
     * </li>
     * <li>
     *    For <em>outgoing replay requests</em>,
     *    the senderURI is the URI of the local participant
     *    requesting the replay.
     * </li>
     * <li>
     * 	  For <em>incoming registration requests</em>,
     * 	  the senderURI represents the remote participant's URI
     *    to use in this transaction service when sending commit requests.
     * </li>
     * <li>
     * 	  For <em>outgoing registration requests</em>,
     *    the senderURI is an URI combined of the local participant
     *    plus the URL of the local registration requesting port.
     * </li>
     * </ul>
     * 
     * @return String The globally unique sender URI.
     */

    public String getSenderURI ();

    /**
     * Get the message type.
     * 
     * @return int One of the predefined types.
     */

    public int getMessageType ();

}
