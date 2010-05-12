//$Id: OutgoingAddressData.java,v 1.1.1.1 2006/10/02 15:21:13 guy Exp $
//$Log: OutgoingAddressData.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:13  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:59  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:14  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/11/08 16:39:00  guy
//Added code based on tests.
//
//Revision 1.2  2005/10/21 08:18:56  guy
//Added method addToCall to work with outgoing calls.
//
//Revision 1.1  2005/10/18 12:41:01  guy
//Added addressing logic.
//
package com.atomikos.icatch.jaxb.wsa;

import java.io.Serializable;

import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;

/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * A utility for easy manipulation of outgoing addressing data. When an outgoing
 * message should contain addressing data for replying, here is where the
 * relevant parameters can be configured. This class also minimizes application
 * dependencies on specific versions of WSA.
 * 
 * <b>The existence of configured and version-specific WSA handlers is necessary
 * for this design to work correctly.</b>
 * 
 */

public abstract class OutgoingAddressingHeaders implements Serializable
{
   

    private String replyTo;

    private String replyToTarget;

    private String faultTo;

    private String faultToTarget;

    private String messageId;

    private String action;

    protected OutgoingAddressingHeaders ()
    {
    }

    /**
     * Sets the value of the replyTo address header.
     * 
     * @param uri
     */
    public void setReplyTo ( String uri )
    {
        this.replyTo = uri;
    }

    /**
     * Inserts a reference property that indicates the local target URI on this
     * side. This allows the same port to be used for different target entities.
     * 
     * @param uri
     */
    public void setReplyToTarget ( String uri )
    {
        this.replyToTarget = uri;
    }

    /**
     * Sets the value of the faultTo address header.
     * 
     * @param uri
     */
    public void setFaultTo ( String uri )
    {
        this.faultTo = uri;
    }

    /**
     * Inserts a reference property that indicates the local targetURI on this
     * side. This allows the same port to be used for different target entities.
     * 
     * @param uri
     */
    public void setFaultToTarget ( String uri )
    {
        this.faultToTarget = uri;
    }

    /**
     * Inserts a message ID conform the WS-A specification. This message ID
     * should be used by the remote party when replying to this message.
     * 
     * @param id
     */
    public void setMessageId ( String id )
    {
        this.messageId = id;
    }

    /**
     * Sets the mandatory action URI for the message.
     * 
     * @param uri
     */

    public void setAction ( String uri )
    {
        this.action = uri;
    }

    public String getFaultTo ()
    {
        return this.faultTo;
    }

    public String getFaultToTarget ()
    {
        return this.faultToTarget;
    }

    public String getReplyTo ()
    {
        return this.replyTo;
    }

    public String getReplyToTarget ()
    {
        return this.replyToTarget;
    }

    public String getMessageId ()
    {
        return this.messageId;
    }

    public String getAction ()
    {
        return this.action;
    }

    public abstract String getTo ();

    /**
     * Adds the configured data to the context or the given provider.
     * 
     * @param ctx
     * @throws SOAPException 
     */
    public abstract void insertIntoRequestContext ( BindingProvider bp ) throws SOAPException;

}
