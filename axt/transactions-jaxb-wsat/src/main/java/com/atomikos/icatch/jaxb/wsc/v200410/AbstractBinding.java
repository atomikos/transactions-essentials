
package com.atomikos.icatch.jaxb.wsc.v200410;

import java.io.ByteArrayOutputStream;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.jaxb.wsa.v200408.IncomingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * Generic binding support for WS-Addressing enabled
 * services.
 * 
 */
public class AbstractBinding 
{


    public AbstractBinding()
    {
        super();
    }

    
    @Resource
    protected WebServiceContext ctx;

    protected MessageContext getMessageContext()
    {
    	return ( MessageContext ) ctx.getMessageContext();
    }
   
    
    private FaultPortType getFaultPort ( OutgoingAddressingHeaders addressData ) throws SOAPException
    {
    	FaultPortType ret = null;
    	FaultService fs = new FaultService();
    	ret = fs.getFaultPort();
    	BindingProvider bp = ( BindingProvider ) ret;
 	    addressData.insertIntoRequestContext ( bp );
        return ret;
    }

//    protected AddressingHeaders getAddressingHeaders()
//    {
//    	AddressingHeaders ret = null;
//    	SOAPMessageContext msgCtx = getMessageContext();
//    	ret = ( AddressingHeaders ) msgCtx.getProperty ( 
//    		Constants.ENV_ADDRESSING_REQUEST_HEADERS );
//    	
//    	return ret;	
//    }

	/**
	 * Gets/creates/extracts a URI representation of
	 * the target of a message. This URI
	 * is needed internally, to represent the 
	 * target as a unique String value.
	 * @return The URI, derived from the 
	 * the address data and
	 * address reference properties.
	 */
    protected String getTargetURI()
    {
    	String ret = null;
    	IncomingAddressingHeaders inData = getSenderAddress();
    	ret = inData.getTarget();
    	return ret;
    }


    protected Object getTargetAddress()
    {
    	//INCOMING messages contain the same target address
    	//as the sender address: the WSA object contains
    	//all information for both target and sender
    	return getSenderAddress();
    }

	/**
	 * Gets/extracts/creates a URI representation of
	 * the remote sender of a message. This URI
	 * is needed internally, to represent the 
	 * remote sender as a unique String value.
	 * @return The URI, constructed from the 
	 * concatenation of the address data and
	 * address reference properties.
	 */

    protected String getSenderURI()
    {
    	return getSenderAddress().getSenderURI();
    }

    protected IncomingAddressingHeaders getSenderAddress()
    {
		
		return IncomingAddressingHeaders.extractFromContext ( getMessageContext() );
    }
    

    
    /**
     * Finds the transaction (if any)
     * from the reference parameters in the target 
     * address.
     * @return The transaction or null if none.
     */
    protected CompositeTransaction findTransaction()
    {
    	CompositeTransaction ret = null;
    	CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
    	
    	String targetURI = getTargetURI();
    	if ( targetURI != null ) 
    		ret = ctm.getCompositeTransaction ( targetURI );
    	else Configuration.logWarning ( "WS-T Module: targetURI is null?" );
    	
    	return ret;
    }
    
    protected void logMessageIfDebug()
    {
    	
    	//TODO check how to re-enable this
    	
//    	if ( Configuration.getConsole().getLevel() == Console.DEBUG ) {
//    		//ONLY DO THIS IF DEBUG: THIS IS EXPENSIVE XML PROCESSING!!!
//			SOAPMessageContext msgCtx = getMessageContext();
//			SOAPMessage msg = msgCtx.getMessage();
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			try
//            {
//                msg.writeTo ( out );
//                Configuration.logDebug ( "WS-T Module: received SOAP message: \n" + out.toString() );
//            }
//            catch ( Exception e )
//            {
//               //ignore: this is just logging code
//            }
//    	}
    }
    
    protected void sendInvalidStateFault() throws SOAPException
    {
		QName code = new QName ( Utils.WSC_NAMESPACE_URI , "InvalidState" );
		String reason = "The message was invalid for the current state of the activity.";
		sendFault ( code , reason );
    }
    
    private void sendFault ( QName faultCode , String reason ) throws SOAPException
    {
    		IncomingAddressingHeaders inAddress = getSenderAddress();
		OutgoingAddressingHeaders faultAddress = (OutgoingAddressingHeaders) inAddress.createFaultAddress();
		if ( faultAddress == null ) {
			Configuration.logDebug ( "WS-T Module: no fault address found - not sending fault: " + reason );
			return;	
		}
		
		faultAddress.setAction ( Utils.WSC_BASE_ACTION_URI + "fault" );
		Fault fault = new Fault();
		fault.setFaultcode ( faultCode  );	
		fault.setFaultstring ( reason );
		
	
		FaultPortType faultPort = getFaultPort ( faultAddress );
		if ( faultPort != null ) {
			
            faultPort.faultOperation ( fault );

		}
   		//don't bother sending fault if port is null
   	
    }
    
    protected void sendInvalidProtocolFault () throws SOAPException
    {
		QName code = new QName ( Utils.WSC_NAMESPACE_URI , "InvalidProtocol" );
		String reason = "The protocol is invalid or is not supported by the coordinator.";
		sendFault ( code , reason );
    }
    
	protected void sendInvalidParametersFault () 
	{
		QName code = new QName ( Utils.WSC_NAMESPACE_URI , "InvalidParameters" );
		String reason = "The message contained invalid parameters and could not be processed.";
		try {
			sendFault ( code , reason );
		} catch (SOAPException e) {
			Configuration.logWarning ( "Could not send invalid parameters fault" , e );
		}
	}



}
