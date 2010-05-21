package com.atomikos.icatch.jaxws;


import javax.xml.soap.SOAPMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2007, Atomikos. All rights reserved.
 * 
 * A generic superclass for all protocol-specific exporting transaction
 * handlers. This handler only works on platforms where the handler is invoked
 * in the same thread as the actual service endpoint.
 * 
 * 
 */

public abstract class GenericExportingTransactionHandler
implements SOAPHandler<SOAPMessageContext>
{

    protected abstract SOAPExportingTransactionManager getExportingTransactionManager ();

    protected CompositeTransaction getCompositeTransaction ()
    {
        CompositeTransaction ret = null;
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();

        ret = ctm.getCompositeTransaction ();
        return ret;
    }

    /**
     * Inserts/extracts the transaction information to/from the message headers of the
     * outgoing request. The propagation added by this handler will be in the
     * Atomikos format.
     * 
     * @throws ProtocolException
     *             If there is no transaction for the calling thread, or if the
     *             transaction was already rolled back.
     */
    
    public boolean handleMessage ( SOAPMessageContext ctx ) throws ProtocolException 
    {
    		boolean ret = false;
    		Boolean outboundProperty = ( Boolean ) ctx.get ( MessageContext.MESSAGE_OUTBOUND_PROPERTY );
    		if ( outboundProperty.booleanValue() ) {
    			ret = handleRequest ( ctx );
    		}
    		else {
    			ret = handleResponse ( ctx );
    		}
    		return ret;
    }
    
   
    private boolean handleRequest ( SOAPMessageContext ctx ) throws ProtocolException,
            SOAPFaultException
    {
        boolean ret = true;
        Configuration
                .logDebug ( "ExportingTransactionHandler: entering handleRequest..." );
        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();
        if ( ctm == null ) {
            // not running
            throw new ProtocolException (
                    "Transaction service is required but not running." );
        }
        SOAPMessageContext sctx = (SOAPMessageContext) ctx;
        SOAPMessage msg = sctx.getMessage ();
        CompositeTransaction ct = getCompositeTransaction ();
        if ( ct == null )
            throw new ProtocolException ( "Transaction already rolled back" );
        String tid = ct.getTid ();
        // add tx propagation to the msg
        try {
            Configuration
                    .logDebug ( "ExportingTransactionHandler: adding propagation for "
                            + tid );
            getExportingTransactionManager ().insertPropagation ( tid, msg );
        } catch ( Exception e ) {
            Configuration.logWarning (
                    "ExportingTransactionHandler: error in adding propagation",
                    e );
            throw new ProtocolException ( e );
        }

        Configuration
                .logDebug ( "ExportingTransactionHandler: handleRequest done." );
        return ret;
    }

    /**
     * Extracts the returned transaction information from the response message
     * headers, and makes the remote work subject to the coordinated termination
     * of the local transaction.
     * 
     * @throws ProtocolException
     *             If the local transaction referenced in the message headers no
     *             longer exists (which happens typically after timing out).
     */
    
    private boolean handleResponse ( MessageContext ctx )
            throws ProtocolException, SOAPFaultException
    {
        boolean ret = true;
        Configuration
                .logDebug ( "ExportingTransactionHandler: entering handleResponse..." );
        SOAPMessageContext sctx = (SOAPMessageContext) ctx;
        SOAPMessage msg = sctx.getMessage ();

        // extract tx information from msg
        try {
            Configuration
                    .logDebug ( "ExportingTransactionHandler: extracting extent..." );
            getExportingTransactionManager ().extractExtent ( msg );
        } catch ( Exception e ) {
            Configuration.logWarning (
                    "ExportingTransactionHandler: error in extracting extent",
                    e );
            throw new ProtocolException ( e );
        }
        Configuration
                .logDebug ( "ExportingTransactionHandler: handleResponse done." );

        return ret;
    }
    
    public boolean handleFault ( SOAPMessageContext ctx ) throws ProtocolException
    {
        Configuration
                .logDebug ( "ExportingTransactionHandler: handleFault" );

        //nothing to do: ignore extent
        return true;
    }
    
    public void close ( MessageContext ctx ) 
	{
		//called just when the MEP ends - NOT the end of the handler life!!!

		
	}
}
