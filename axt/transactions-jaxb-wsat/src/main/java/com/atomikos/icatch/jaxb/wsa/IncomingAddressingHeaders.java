package com.atomikos.icatch.jaxb.wsa;

import java.io.Serializable;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * Defines access for incoming addressing data (mainly reference properties), 
 * and allows easy conversion into reply address data. 
 * This class also minimizes application dependencies on versions of WSA. 
 * 
 * <b>The existence of configured and version-specific WSA handlers is necessary
 * for this design to work correctly.</b>
 * 
 */
public abstract class IncomingAddressingHeaders implements Serializable
{


    protected String target;

    protected String messageId;


    protected IncomingAddressingHeaders ()
    {
    }

    protected void setMessageId ( String msgId ) 
    {
    	this.messageId = msgId;
    }
    
    protected void setTarget ( String target ) 
    {
    	this.target = target;
    }

    /**
     * Gets the target URI as contained in the reference properties of the
     * addressing headers.
     * 
     * @return The URI, or null if not found.
     */
    public String getTarget ()
    {
        return this.target;
    }
    
    /**
     * Gets the messageID if any.
     * 
     * @return
     */
    public String getMessageId() 
    {
    	return this.messageId;
    }

    /**
     * Creates a reply address according to the reply parameters in the incoming
     * addressing data.
     * 
     * @return The reply address, or null if no reply headers were present. The
     *         result will automatically have the to, reference properties, and
     *         the relatesTo headers set appropriately.
     */
    public abstract OutgoingAddressingHeaders createReplyAddress ();

    /**
     * Creates a fault address according to the fault/reply parameters in the
     * incoming addressing data.
     * 
     * @return The address, or null if not applicable. The result will
     *         automatically have the to, reference properties, and the
     *         relatesTo headers set appropriately.
     */
    public abstract OutgoingAddressingHeaders createFaultAddress ();

}
