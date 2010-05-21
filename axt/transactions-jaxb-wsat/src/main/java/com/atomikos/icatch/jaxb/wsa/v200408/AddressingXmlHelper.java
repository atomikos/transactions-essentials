package com.atomikos.icatch.jaxb.wsa.v200408;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.atomikos.icatch.system.Configuration;

 /**
  * Helper class for WS-A XML intricacies.
  * 
  *
  */

public class AddressingXmlHelper 
{

	static private SOAPFactory soapFactory = null;
	static private MessageFactory msgFactory = null;
	static private DocumentBuilder docBuilder = null;
	
	static {
		try {
			soapFactory = SOAPFactory.newInstance();
		} catch ( Exception e ) {
			Configuration.logWarning ( "Failed to create SOAPFactory" , e );
		}
		try {
			msgFactory = MessageFactory.newInstance();
		} catch ( Exception e ) {
			Configuration.logWarning ( "Failed to create MessageFactory" , e );
		}
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch ( Exception e ) {
			Configuration.logWarning ( "Failed to create DocumentBuilder" , e );
		}
	}


	public static SOAPElement createTargetReferenceParameter ( String targetRefParameterValue ) throws SOAPException 
	{
		SOAPElement ret = soapFactory.createElement ( AddressingConstants.TARGET_ELEMENT_NAME , AddressingConstants.TARGET_NS_PREFIX , AddressingConstants.TARGET_NS_URI );
		ret.addTextNode ( targetRefParameterValue );
		return ret;
	}
	
	public static SOAPElement createEprWithTargetReferenceParameter (
			String eprLocalElementName, String eprElementPrefix, 
			String eprElementNamespace , String addressValue , String targetRefPropValue ) 
	throws SOAPException 
	{
		SOAPElement el = soapFactory.createElement ( eprLocalElementName , eprElementPrefix , eprElementNamespace );
		//add the Address element
		SOAPElement address = soapFactory.createElement ( AddressingConstants.WSA_ADDRESS_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		address.addTextNode ( addressValue );
		el.addChildElement ( address );
		//add the reference parameters
		SOAPElement refProperties = soapFactory.createElement ( 
				AddressingConstants.WSA_REFERENCE_PARAMETERS_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , 
				AddressingConstants.WSA_NS_URI );
		SOAPElement targetRefProp = createTargetReferenceParameter(targetRefPropValue);
		refProperties.addChildElement ( targetRefProp );
		el.addChildElement ( refProperties );
		return el;
	}
	
	

	public static SOAPElement createReferencePropertiesFromListOfAny(
			List<Object> any) throws SOAPException {
		SOAPElement refProperties = soapFactory.createElement ( AddressingConstants.WSA_REFERENCE_PROPERTIES_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		Iterator it = any.iterator();
		while ( it.hasNext() ) {
			//JAXB wraps refProperties in DOM nodes! so convert first
			org.w3c.dom.Node domNode = (org.w3c.dom.Node) it.next();
			SOAPElement next = convertFromDomToSoapElement  ( domNode );
			if ( next != null ) refProperties.addChildElement ( next );
		}
		return refProperties;
	}

	private static SOAPElement convertFromDomToSoapElement ( org.w3c.dom.Node domNode ) throws DOMException, SOAPException 
	{
		//TODO review code: is DOM model interpreted the right way?
		SOAPElement ret = null;
		if ( domNode.getNodeType() == Node.TEXT_NODE ) {
			//get value of this node with the naming of the PARENT node (cf DOM model)
			ret = createSOAPElement ( domNode.getParentNode().getLocalName() , domNode.getParentNode().getPrefix(), 
					domNode.getParentNode().getNamespaceURI() , domNode.getNodeValue() );
		} else if ( domNode.getNodeType() == Node.ELEMENT_NODE ) {
			//create a SOAPElement with the same name and atts
			ret = soapFactory.createElement ( domNode.getLocalName() , domNode.getPrefix() , domNode.getNamespaceURI() );
			NamedNodeMap domAttrs = domNode.getAttributes();
			if ( domAttrs != null ) {
				for ( int i = 0 ; i < domAttrs.getLength() ; i++ ) {
					org.w3c.dom.Node att = domAttrs.item ( i );
					Name name = soapFactory.createName ( att.getLocalName() , att.getPrefix(), att.getNamespaceURI() );
					ret.addAttribute ( name , att.getNodeValue() );
				}
			}
			//recursively add all child nodes, but only consider elements and text
			NodeList children = domNode.getChildNodes();
			for ( int i = 0 ; i < children.getLength() ; i++ ) {
				org.w3c.dom.Node child = children.item ( i );
				if ( child.getNodeType() == Node.ELEMENT_NODE || child.getNodeType() == Node.TEXT_NODE ) {
					SOAPElement childAsSOAPElement = convertFromDomToSoapElement ( child );
					ret.addChildElement ( childAsSOAPElement );
				} else {
					Configuration.logDebug ( "Ignoring DOM content of type: " + child.getNodeType() );
				}
			}
		}
		return ret;
	}

	public static String toString ( SOAPElement replyToEPR ) 
	{
		if ( replyToEPR == null ) return "";
		StringBuffer ret = new StringBuffer();
		Iterator children = replyToEPR.getChildElements();
		while ( children.hasNext() ) {
			Node next = (Node) children.next();
			if ( next instanceof Attr || next instanceof Text ){
				ret.append ( next.getNodeValue() );
			}
			else if ( next instanceof Element ) {
				SOAPElement nextElement = ( SOAPElement ) next;
				ret.append ( toString ( nextElement ) );
			} 
		}
		return ret.toString();
	}

	public static SOAPElement createReferenceProperties ( String targetRefPropValue ) throws SOAPException 
	{
		SOAPElement refProperties = soapFactory.createElement ( AddressingConstants.WSA_REFERENCE_PROPERTIES_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		SOAPElement targetEl = createTargetReferenceParameter ( targetRefPropValue );
		refProperties.addChildElement ( targetEl );
		return refProperties;
	}
	
	public static SOAPElement createReferenceParameters ( String targetRefParValue ) throws SOAPException 
	{
		SOAPElement refProperties = soapFactory.createElement ( AddressingConstants.WSA_REFERENCE_PARAMETERS_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		SOAPElement targetEl = createTargetReferenceParameter ( targetRefParValue );
		refProperties.addChildElement ( targetEl );
		return refProperties;
	}

	public static void wrapIntoSoapMessageAndWriteToStream ( SOAPElement element , OutputStream out ) throws SOAPException, IOException 
	{
		SOAPMessage msg = msgFactory.createMessage();
		msg.getSOAPBody().addChildElement ( element );
		msg.writeTo ( out );
	}

	public static SOAPElement readFromInputStreamAndUnwrapFromSoapMessage ( InputStream in ) throws IOException, SOAPException 
	{
		MimeHeaders headers = new MimeHeaders();
		headers.addHeader("Content-Type" , "text/xml");
		SOAPMessage msg = msgFactory.createMessage ( headers , in );
		return (SOAPElement) msg.getSOAPBody().getChildElements().next();
	}

	public static SOAPElement createSOAPElement ( String localName ,
			String nsPrefix, String nsUri, String value ) throws SOAPException 
	{
		Configuration.logDebug ( "creating element: " + nsPrefix + ":" + localName + " in ns:" + nsUri + " with value: " + value );
		SOAPElement ret = soapFactory.createElement ( localName , nsPrefix , nsUri );
		ret.setTextContent ( value );
		return ret;
	}

	public static SOAPElement createFromSOAPElement ( String fromUri ) throws SOAPException 
	{
		SOAPElement ret = soapFactory.createElement ( AddressingConstants.WSA_FROM_LOCAL_ELEMENT_NAME , 
				AddressingConstants.WSA_NS_PREFIX, AddressingConstants.WSA_NS_URI );
		SOAPElement content = ret.addChildElement ( AddressingConstants.WSA_ADDRESS_LOCAL_ELEMENT_NAME , 
				AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		content.setTextContent ( fromUri );
		return ret;
	}

	public static SOAPElement createToSOAPElement ( String to ) throws SOAPException 
	{
		return createSOAPElement ( AddressingConstants.WSA_ADDRESS_LOCAL_ELEMENT_NAME , 
 	    		AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , to );
	}

	public static SOAPElement createActionSOAPElement ( String action ) throws SOAPException 
	{
		return createSOAPElement ( AddressingConstants.WSA_ACTION_LOCAL_ELEMENT_NAME , 
 	    		AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , action );
	}

	public static SOAPElement createMessageIdSOAPElement ( String msgId ) throws SOAPException  
	{
		return createSOAPElement ( AddressingConstants.WSA_MSG_ID_LOCAL_ELEMENT_NAME , 
 	    		AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , msgId );
	}

	public static SOAPElement createRelatesToSOAPElement ( String relatesTo ) throws SOAPException 
	{
		return createSOAPElement ( AddressingConstants.WSA_RELATES_TO_LOCAL_ELEMENT_NAME , 
 	    		AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , relatesTo );
	}

	public static Element createTargetReferencePropertyAsDomElement ( String value ) {
		Document doc = docBuilder.newDocument();
		Element el = doc.createElementNS ( AddressingConstants.TARGET_NS_URI , AddressingConstants.TARGET_ELEMENT_NAME );
		el.setTextContent ( value );
		return el;
	}

	public static SOAPElement createReferenceParametersFromListOfAny (
			List<Object> any) throws SOAPException {
		SOAPElement refParams = soapFactory.createElement ( AddressingConstants.WSA_REFERENCE_PARAMETERS_LOCAL_ELEMENT_NAME , AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI );
		Iterator it = any.iterator();
		while ( it.hasNext() ) {
			//JAXB wraps any in DOM nodes! so convert first
			org.w3c.dom.Node domNode = (org.w3c.dom.Node) it.next();
			SOAPElement next = convertFromDomToSoapElement  ( domNode );
			if ( next != null ) refParams.addChildElement ( next );
		}
		return refParams;
	}

	
}
