
package com.atomikos.icatch.jaxb.wsa.v200408;

import java.util.List;

import javax.xml.soap.SOAPElement;
import javax.xml.ws.handler.MessageContext;

import com.atomikos.icatch.system.Configuration;




/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * Implementation for the 2004/08 WSA version.
 *
 * 
 */

public class IncomingAddressingHeaders extends com.atomikos.icatch.jaxb.wsa.IncomingAddressingHeaders
{

	private SOAPElement replyToEPR;
	private SOAPElement faultToEPR;
	
	private IncomingAddressingHeaders ( List<SOAPElement> rawHeadersFromIncomingMessage ) 
	{
		if ( rawHeadersFromIncomingMessage == null ) throw new NullPointerException ( "rawHeadersFromIncomingMessage" );
		for ( SOAPElement header : rawHeadersFromIncomingMessage ) {
			parseHeader  ( header );
		}
	}
	
	private void parseHeader ( SOAPElement header ) {
		String ns = header.getNamespaceURI();
		if ( AddressingConstants.WSA_NS_URI.equals ( ns ) ) {
			header.detachNode();
			String name = header.getLocalName();
			if ( AddressingConstants.WSA_REPLY_TO_EPR_LOCAL_ELEMENT_NAME.equals ( name ) ) {
				replyToEPR = header;
			} else if ( AddressingConstants.WSA_FAULT_TO_EPR_LOCAL_ELEMENT_NAME.equals ( name ) ) {
				faultToEPR = header;
			} else if ( AddressingConstants.WSA_MSG_ID_LOCAL_ELEMENT_NAME.equals ( name ) ) {
				setMessageId ( header.getTextContent() );
			}
		} else if ( AddressingConstants.TARGET_NS_URI.equals ( ns ) ) {
			setTarget ( header.getTextContent() );
			header.detachNode();
		} else {
			//log as debug since we get all remaining headers here
			Configuration.logDebug ( "WS-AT: ignoring unkown header namespace: " + ns );
		}
	}


	@Override
	public OutgoingAddressingHeaders createFaultAddress() {
		OutgoingAddressingHeaders ret = OutgoingAddressingHeaders.extractFromEPR ( faultToEPR );
		ret.setRelatesTo ( getMessageId() );
		return ret;
	}
	
	@Override
	public OutgoingAddressingHeaders createReplyAddress() {
		OutgoingAddressingHeaders ret = OutgoingAddressingHeaders.extractFromEPR ( replyToEPR );
		ret.setRelatesTo ( getMessageId() );
		return ret;
	}
	


	
	/**
	 * Gets/extracts/creates a URI representation of
	 * the remote sender of a message - as determined 
	 * by the replyTo EPR (if any). This URI
	 * is needed internally, to represent the 
	 * remote sender as a unique String value.
	 * @return The URI as a String, constructed from the 
	 * concatenation of the replyTo address data and
	 * address reference properties, or null if no 
	 * replyTo EPR was found.
	 */
	
	public String getSenderURI() 
	{
		return AddressingXmlHelper.toString ( replyToEPR );
	}
	
	/**
	 * Extracts an instance from the WS-A headers in an incoming SOAP message.
	 * 
	 * @param messageContext
	 * @return An instance that allows getting the WSA RefProps for the local
	 * target transaction as well as creating a reply or fault instance.
	 * In this case, all the getters should return non-null results, since
	 * the WS-A applies to a local transaction know in this service.
	 */
	
	@SuppressWarnings("unchecked")
	public static IncomingAddressingHeaders extractFromContext(
			MessageContext messageContext) {
		List<SOAPElement> headers = ( List<SOAPElement> ) messageContext.get ( 
				AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME );
		IncomingAddressingHeaders ret = new IncomingAddressingHeaders ( headers );
		return ret;
	}

	SOAPElement getReplyToEPR() {
		return replyToEPR;
	}
	

}
