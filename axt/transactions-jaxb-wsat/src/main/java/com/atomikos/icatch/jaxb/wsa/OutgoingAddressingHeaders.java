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
