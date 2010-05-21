package com.atomikos.icatch.jaxb.wsa.v200408;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import com.atomikos.icatch.jaxb.wsc.v200410.EndpointReferenceType;
import com.atomikos.icatch.jaxb.wsc.v200410.ReferenceParametersType;
import com.atomikos.icatch.jaxb.wsc.v200410.ReferencePropertiesType;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * Implementation according to the 2004/08 spec.
 * 
 */

public class OutgoingAddressingHeaders extends
		com.atomikos.icatch.jaxb.wsa.OutgoingAddressingHeaders implements Externalizable
{
	private static WSAHandler handler = new WSAHandler();
	

    private static Handler getWSAHandlerInstance() {
    	return handler;
    }
	
	private SOAPElement refPropertiesAsSoapElement;
	// the reference properties to add for the remote party
	// NOT to be confused with this side's replyToEPR or faultToEPR!
	
	private SOAPElement refParametersAsSoapElement;
	// the reference parameters to add for the remote party
	// NOT to be confused with this side's replyToEPR or faultToEPR!
	
	private String to;

	private String relatesTo;

	
	public OutgoingAddressingHeaders () {
		//public constructor needed for Externalizable mechanism
	}
	
	@Override
	public String getTo() {
		return to;
	}
	
	void setTo ( String to ) {
		this.to = to;
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		
		//first: read the set containing the properties present
		Set<String> notNulls = (Set<String>) in.readObject();
		if ( notNulls.contains ( "action" ) ) {
			String action = (String) in.readObject();
			setAction ( action );
		}
		if ( notNulls.contains ( "faultTo" ) ) {
			String faultTo = (String) in.readObject();
			setFaultTo ( faultTo );
		}
		if ( notNulls.contains ( "faultToTarget" ) ) {
			String faultToTarget = (String) in.readObject();
			setFaultToTarget ( faultToTarget );
		}
		if ( notNulls.contains ( "messageId" ) ) {
			String messageId = (String) in.readObject();
			setMessageId ( messageId );
		}
		if ( notNulls.contains ( "refProperties" ) ) {
			String msgAsString = ( String ) in.readObject();
		    ByteArrayOutputStream out = new ByteArrayOutputStream ();
			OutputStreamWriter writer = new OutputStreamWriter ( out );
			writer.write ( msgAsString );
			writer.close();
			ByteArrayInputStream ins = new ByteArrayInputStream ( out.toByteArray() );
			SOAPElement refProperties = null;
			try {
				refProperties = AddressingXmlHelper.readFromInputStreamAndUnwrapFromSoapMessage ( ins );
			} catch ( SOAPException e ) {
				String msg = "Could not read reference properties";
				Configuration.logWarning ( msg , e );
				throw new IOException ( msg );
			}
			setRefPropertiesAsSoapElement ( refProperties );
		}
		
		if ( notNulls.contains ( "refParameters" ) ) {
			String msgAsString = ( String ) in.readObject();
		    ByteArrayOutputStream out = new ByteArrayOutputStream ();
			OutputStreamWriter writer = new OutputStreamWriter ( out );
			writer.write ( msgAsString );
			writer.close();
			ByteArrayInputStream ins = new ByteArrayInputStream ( out.toByteArray() );
			SOAPElement refParameters = null;
			try {
				refParameters = AddressingXmlHelper.readFromInputStreamAndUnwrapFromSoapMessage ( ins );
			} catch ( SOAPException e ) {
				String msg = "Could not read reference properties";
				Configuration.logWarning ( msg , e );
				throw new IOException ( msg );
			}
			setRefParametersAsSoapElement ( refParameters );
		}
		
		if ( notNulls.contains ( "relatesTo" ) ) {
			String relatesTo = (String) in.readObject();
			setRelatesTo ( relatesTo );
		}
		if ( notNulls.contains ( "replyTo" ) ) {
			String replyTo = (String) in.readObject();
			setReplyTo ( replyTo );
		}
		if ( notNulls.contains ( "replyToTarget" ) ) {
			String replyToTarget = (String) in.readObject();
			setReplyToTarget ( replyToTarget );
		}
		if ( notNulls.contains ( "to" ) ) {
			String to = (String) in.readObject();
			setTo ( to );
		}
		
	}

	public void writeExternal ( ObjectOutput out ) throws IOException 
	{
		Set<String> notNulls = new HashSet<String>();
		if ( getAction() != null ) notNulls.add ( "action" );
		if ( getFaultTo() != null ) notNulls.add ( "faultTo" );
		if ( getFaultToTarget() != null ) notNulls.add ( "faultToTarget" );
		if ( getMessageId() != null ) notNulls.add ( "messageId" );
		if ( getRefPropertiesAsSOAPElement() != null ) notNulls.add ( "refProperties" );
		if ( getRelatesTo() != null ) notNulls.add ( "relatesTo" );
		if ( getReplyTo() != null ) notNulls.add ( "replyTo" );
		if ( getReplyToTarget() != null ) notNulls.add ( "replyToTarget" );
		if ( getTo() != null ) notNulls.add ( "to" );
		if ( getRefParametersAsSOAPElement() != null ) notNulls.add ( "refParameters" );
		
		//first, write the set of fields that are to be read upon unmarshalling
		out.writeObject ( notNulls );
		
		//now, write each field
		if ( getAction() != null ) out.writeObject ( getAction() );
		if ( getFaultTo() != null ) out.writeObject ( getFaultTo() );
		if ( getFaultToTarget() != null ) out.writeObject ( getFaultToTarget() );
		if ( getMessageId() != null ) out.writeObject ( getMessageId() );
		
		if ( getRefPropertiesAsSOAPElement() != null ) {
			//since SOAPElements are not serializable: wrap the
			//element in a SOAPMessage 'container' and write that out to the stream
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			try {
				AddressingXmlHelper.wrapIntoSoapMessageAndWriteToStream ( getRefPropertiesAsSOAPElement() , buf );
			} catch (SOAPException e) {
				String msg =  "Failed to stream reference properties";
				Configuration.logWarning ( msg , e );
				throw new IOException ( msg );
			}
			buf.close();
			out.writeObject ( buf.toString() );
		}
		
		if ( getRefParametersAsSOAPElement() != null ) {
			//since SOAPElements are not serializable: wrap the
			//element in a SOAPMessage 'container' and write that out to the stream
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			try {
				AddressingXmlHelper.wrapIntoSoapMessageAndWriteToStream ( getRefParametersAsSOAPElement() , buf );
			} catch (SOAPException e) {
				String msg =  "Failed to stream reference parameters";
				Configuration.logWarning ( msg , e );
				throw new IOException ( msg );
			}
			buf.close();
			out.writeObject ( buf.toString() );
		}
		
		if ( getRelatesTo() != null ) out.writeObject ( getRelatesTo() );
		if ( getReplyTo() != null ) out.writeObject ( getReplyTo() );
		if ( getReplyToTarget() != null ) out.writeObject ( getReplyToTarget() );
		if ( getTo() != null ) out.writeObject ( getTo() );
	}

	void setRefPropertiesAsSoapElement ( SOAPElement refProps ) {
		this.refPropertiesAsSoapElement = refProps;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void insertIntoRequestContext ( BindingProvider bp ) throws SOAPException {
		Configuration.logDebug ( "OutgoingAddressingHeaders: inserting into request context..." );
    	Binding binding = bp.getBinding();
    	List<Handler> handlerList = binding.getHandlerChain();
    	Handler wsaHandler = getWSAHandlerInstance();
 	    handlerList.add ( wsaHandler );
 	    binding.setHandlerChain(handlerList);
 	    Map<String,Object> ctx = bp.getRequestContext();
 	    ctx.put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY , getTo() );
 	    List<SOAPElement> headers = new ArrayList<SOAPElement>();
 	    headers.add ( AddressingXmlHelper.createToSOAPElement ( getTo() ) );
 	    headers.add ( AddressingXmlHelper.createActionSOAPElement ( getAction() ) );
 	   
    	String msgId = getMessageId();   	
    	if ( msgId != null ){
    		headers.add ( AddressingXmlHelper.createMessageIdSOAPElement ( msgId ) );
    	}
    	String relatesTo = getRelatesTo();
    	if ( relatesTo != null ) {
    		headers.add ( AddressingXmlHelper.createRelatesToSOAPElement ( relatesTo ) );
    	}
    	
    	SOAPElement refProps = getRefPropertiesAsSOAPElement();
    	if ( refProps != null ) {
    		Iterator props = refProps.getChildElements();
    		while ( props.hasNext() ) {
    			SOAPElement next = ( SOAPElement ) props.next();
    			next.detachNode();
    			headers.add ( next );
    		}
    	}
    	
    	SOAPElement refParams = getRefParametersAsSOAPElement();
    	if ( refParams != null ) {
    		Iterator params = refParams.getChildElements();
    		while ( params.hasNext() ) {
    			SOAPElement next = ( SOAPElement ) params.next();
    			next.detachNode();
    			headers.add ( next );
    		}
    	}
    	
    	if ( getReplyTo() != null ) {
    		SOAPElement replyToEPR = AddressingXmlHelper.createEprWithTargetReferenceParameter (
    			AddressingConstants.WSA_REPLY_TO_EPR_LOCAL_ELEMENT_NAME, AddressingConstants.WSA_NS_PREFIX, 
    			AddressingConstants.WSA_NS_URI , getReplyTo(), getReplyToTarget() );
    		headers.add ( replyToEPR );
    		if ( msgId == null ) throw new SOAPException ( "WS-Addressing violation: messageID is required if replyTo is set" );
    	}
    	
    	if ( getFaultTo() != null ) {
    		SOAPElement faultToEPR = AddressingXmlHelper.createEprWithTargetReferenceParameter (
    				AddressingConstants.WSA_FAULT_TO_EPR_LOCAL_ELEMENT_NAME, AddressingConstants.WSA_NS_PREFIX, 
    				AddressingConstants.WSA_NS_URI , getFaultTo(), getFaultToTarget() );
    		headers.add ( faultToEPR );
    		if ( msgId == null ) throw new SOAPException ( "WS-Addressing violation: messageID is required if faultTo is set" );
    	}
    	
    	//Following commented out: does not appear in interop scenarios
    	//headers.add ( AddressingXmlHelper.createFromSOAPElement ( AddressingConstants.WSA_ANONYMOUS_URI ));
    	
    	ctx.put ( AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME , headers );
	}

	SOAPElement getRefParametersAsSOAPElement() 
	{
		return refParametersAsSoapElement;
	}

	SOAPElement getRefPropertiesAsSOAPElement() 
	{
		return refPropertiesAsSoapElement;
	}

	/**
	 * Extracts an instance from the WSA information in a given EPR.
	 * 
	 * @param epr
	 * @return An instance 
	 * for sending to the service indicated by the EPR (used for WS-C registration
	 * messages). 
	 * 
	 * @throws SOAPException 
	 */
	
	public static OutgoingAddressingHeaders extractFromEPR ( EndpointReferenceType epr ) 
	throws SOAPException 
	{
		OutgoingAddressingHeaders replyAddress = new OutgoingAddressingHeaders();
		String replyToAddress = epr.getAddress().getValue();
		replyAddress.setTo ( replyToAddress );
		ReferencePropertiesType refProps = epr.getReferenceProperties();
		if ( refProps != null ) {
			SOAPElement refPropertiesAsSoapElement = AddressingXmlHelper.createReferencePropertiesFromListOfAny ( refProps.getAny() );
			replyAddress.setRefPropertiesAsSoapElement ( refPropertiesAsSoapElement );
		}
		ReferenceParametersType refParams = epr.getReferenceParameters();
		if ( refParams != null ) {
			SOAPElement refParametersAsSoapElement = AddressingXmlHelper.createReferenceParametersFromListOfAny ( refParams.getAny() );
			replyAddress.setRefParametersAsSoapElement ( refParametersAsSoapElement );
		}
		return replyAddress;
	}

	void setRefParametersAsSoapElement(
			SOAPElement refParametersAsSoapElement) {
		
		this.refParametersAsSoapElement = refParametersAsSoapElement;
	}

	/**
	 * Similar to {@link extractFromEPR(EndpointReferenceType)} but with the
	 * EPR embedded in a SOAPElement.
	 * 
	 */
	public static OutgoingAddressingHeaders extractFromEPR ( SOAPElement eprAsSOAPElement ) 
	{
		 OutgoingAddressingHeaders ret = new OutgoingAddressingHeaders();
		 String to = null;
		 SOAPElement refProps = null;
		 SOAPElement refParams = null;
		 Iterator it = eprAsSOAPElement.getChildElements();
		 while ( it.hasNext() ) {
			 SOAPElement next = ( SOAPElement ) it.next();
			 Configuration.logDebug ( "OutgoingAddressingHeaders: inspecting element " + next.getLocalName() );
			 if ( AddressingConstants.WSA_ADDRESS_QNAME.equals ( next.getElementQName() ) ) {
				 to = next.getTextContent();
			 } else if ( AddressingConstants.WSA_REFERENCE_PROPERTIES_QNAME.equals ( next.getElementQName() ) ) {
				 refProps = next;
				 //TODO check if we need to detach the refProps node?
			 } else if ( AddressingConstants.WSA_REFERENCE_PARAMETERS_QNAME.equals ( next.getElementQName() ) ) {
				//TODO check if we need to detach the refParams node?
				 refParams = next;
			 }
		 }
		 ret.setTo ( to );
		 ret.setRefPropertiesAsSoapElement ( refProps );
		 ret.setRefParametersAsSoapElement ( refParams );
		 return ret;
	}

    /**
     * Returns a unique String representation of the remote receiver; typically
     * this could be the to-address URI suffixed with the reference properties.
     * @return
     */
	public String getTargetURI() {
		return to + AddressingXmlHelper.toString ( refPropertiesAsSoapElement ) + 
			AddressingXmlHelper.toString ( refParametersAsSoapElement );
	}

	/**
	 * Returns the relatesTo field - if any.
	 * @return
	 */
	public String getRelatesTo() {
		return relatesTo;
	}

	/**
	 * Sets the relatesTo field.
	 * 
	 * @param msgId The message ID of a previous incoming message
	 * (used as correlation for a reply).
	 */
	public void setRelatesTo ( String msgId ) {
		this.relatesTo = msgId;
	}

}
