package com.atomikos.icatch.jaxb.wsa.v200408;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.system.Configuration;

public class WSAHandler implements SOAPHandler<SOAPMessageContext>
{
	private static final Set<QName> HANDLED_HEADERS =
		Collections.unmodifiableSet ( new HashSet<QName> (
				Arrays.asList (
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_ACTION_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_ADDRESS_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_FAULT_TO_EPR_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_REPLY_TO_EPR_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_FROM_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_MSG_ID_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.WSA_NS_URI , AddressingConstants.WSA_RELATES_TO_LOCAL_ELEMENT_NAME ),
						new QName ( AddressingConstants.TARGET_NS_PREFIX , AddressingConstants.TARGET_ELEMENT_NAME )		
				)));

	public Set<QName> getHeaders() 
	{
		return HANDLED_HEADERS;
	}

	public void close ( MessageContext ct ) 
	{
		
		
	}

	public boolean handleFault ( SOAPMessageContext ctx ) 
	{
		//this method is only called when the handle method generates an exception - i.e. never?
		
		Configuration.logWarning ( "WS-A: missing fault handling!" );		
		logContextInDebugMode ( ctx );
		//continue processing
		return true;
	}

	public boolean handleMessage ( SOAPMessageContext ctx ) 
	{
		Configuration.logInfo ( "WSA handler: handleMessage" );
		Boolean out = (Boolean) ctx.get ( MessageContext.MESSAGE_OUTBOUND_PROPERTY );
		if ( Boolean.TRUE.equals ( out ) ) {
			addHeadersToOutgoingRequest  ( ctx );
		} else {
			extractHeadersFromIncomingRequest  ( ctx );
		}
		
		logContextInDebugMode ( ctx );
		
		//continue processing
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void extractHeadersFromIncomingRequest ( SOAPMessageContext ctx ) 
	{
		Configuration.logInfo ( "WSA handler: extracting WSA headers..." );

		List<SOAPElement> headerList = new ArrayList<SOAPElement>();
		try {
			SOAPMessage msg = ctx.getMessage();
			SOAPHeader header;
			header = msg.getSOAPHeader();
			if ( header != null ) {
				Iterator<SOAPElement> headers = header.getChildElements();
				while ( headers.hasNext() ) {
					SOAPElement hdr = headers.next();
					headerList.add ( hdr );
				}
			}
			Configuration.logDebug ( "WSA handler: found " + headerList.size() + " headers" );
			ctx.put ( AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME  , headerList );
			ctx.setScope ( AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME , MessageContext.Scope.APPLICATION );
			
		} catch ( Throwable e ) {
			handleUnexpectedSOAPExceptionForIncomingRequest  ( ctx , e );
		} finally {

		}
		
	}



	//needed because JAX-WS does not allow adding SOAPElement headers - they have to be converted to SOAPHeaderElement headers (yuk!!!)
	private void addHeaderElementWithChildElements ( SOAPHeader container , QName headerName , Iterator children ) throws SOAPException 
	{
		if ( container == null ) throw new NullPointerException ( "container" ); 
		SOAPHeaderElement hdr = container.addHeaderElement ( headerName );
		while ( children.hasNext() ) {
			Object next = children.next();
			//can be other than SOAPElement, like Text!!!
			if ( next instanceof SOAPElement ) {
				SOAPElement el = ( SOAPElement ) next;
				el.detachNode();
				hdr.addChildElement ( el );
			} else if ( next instanceof Text ) {
				Text txt = ( Text ) next;
				hdr.addTextNode ( txt.getTextContent() );
			} else {
				Configuration.logWarning ( "WSA handler: unexpected header of type " + next.getClass().getName() );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addHeadersToOutgoingRequest ( SOAPMessageContext ctx ) 
	{
		Configuration.logInfo ( "WSA handler: adding WSA headers..." );
		
		try {
			SOAPMessage msg = ctx.getMessage();
			SOAPHeader header = msg.getSOAPHeader();
			if ( header == null ) {
				header = msg.getSOAPPart().getEnvelope().addHeader();
			}
			List<SOAPElement> headers = (List<SOAPElement>) ctx.get ( AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME );
			if ( headers != null )  {
				for ( SOAPElement hdr : headers ) {
					addHeaderElementWithChildElements ( header , hdr.getElementQName() , hdr.getChildElements() );
				}
			}
		} catch (Throwable e) {
			handleUnexpectedSOAPExceptionForOutgoingRequest  ( ctx , e );
		}
		
	}

	private void handleUnexpectedSOAPExceptionForOutgoingRequest ( SOAPMessageContext ctx, Throwable e ) 
	{
		Configuration.logWarning ( "WS-A: error when dealing with outgoing request headers" , e );

	}
	
	private void logContextInDebugMode(SOAPMessageContext ctx) {
		if ( Configuration.getConsole().getLevel() == Console.DEBUG ) {
			ByteArrayOutputStream bout = null;
			try {
				bout = new ByteArrayOutputStream();
				ctx.getMessage().writeTo ( bout );
				bout.close();
				Configuration.logDebug ( bout.toString() );
			} catch (Exception e) {
				Configuration.logDebug ( "Could not log SOAP message" , e );
			}

		}
		

	}

	private void handleUnexpectedSOAPExceptionForIncomingRequest ( SOAPMessageContext ctx, Throwable e) 
	{
		Configuration.logWarning ( "WS-A: error when dealing with incoming request headers" , e );

	}

	@SuppressWarnings("unchecked")
	public static void addWSAHandler ( Endpoint endpoint ) {
		List<Handler> handlers = endpoint.getBinding().getHandlerChain();
		handlers.add ( new WSAHandler() );
		endpoint.getBinding().setHandlerChain ( handlers );
	}

	
}
