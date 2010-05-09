
package com.atomikos.icatch.jaxws.atomikos;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;


import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.imp.CompositeTransactionAdaptor;
import com.atomikos.icatch.imp.ExtentImp;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.msg.CommitServer;
import com.atomikos.icatch.msg.MessageParticipant;
import com.atomikos.icatch.msg.MessageRecoveryCoordinator;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * Utilities for this package.
 * 
 */
public final class Utils
{
	
	

    private Utils()
    {
       //instances not allowed
    }
    
    public static final String SOAP_ENVELOPE_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";

    public static final String ATOMIKOS_ACTOR =
    	"http://www.atomikos.com/transactions";
    	
    public static final String ATOMIKOS_NAMESPACE =
    	"http://www.atomikos.com/schemas/2005/10/transactions";
    	
    static final String EXTENT_ELEMENT_NAME = "Extent";
    static final String TID_ATTRIBUTE_NAME = "tid";
    static final String EXTENT_INFO_ELEMENT_NAME = "ExtentInfo";
    static final String PARTICIPANT_ELEMENT_NAME = "Participant";
    static final String COUNT_ELEMENT_NAME = "Count";
    static final String HEURISTIC_INFO_ELEMENT_NAME = "HeuristicInfo";
    static final String ENDPOINT_ELEMENT_NAME = "EndPoint";
    static final String REFERENCE_ELEMENT_NAME = "Reference";
    static final String PROPAGATION_ELEMENT_NAME = "Propagation";
    static final String ROOT_ELEMENT_NAME = "Root";
    static final String TID_ELEMENT_NAME = "Tid";
    static final String SERIAL_ELEMENT_NAME = "Serial";
    static final String TIMEOUT_ELEMENT_NAME = "Timeout";
    static final String COORDINATOR_ELEMENT_NAME = "Coordinator";
    static final String PROPERTIES_ELEMENT_NAME = "Properties";
    static final String PROPERTY_ELEMENT_NAME = "Property";
    static final String PROPERTY_NAME_ELEMENT_NAME = "Name";
    static final String PROPERTY_VALUE_ELEMENT_NAME = "Value";
    
    private static SOAPFactory soapFactory = null;
    
    static {
    		try {
    			soapFactory = SOAPFactory.newInstance();
    		}
    		catch ( Exception e ) {
    			Configuration.logWarning ( "Failed to create SOAPFactory" , e );
    		}
    }

	private static void insertAddressNode ( SOAPElement parent , Name name , String uri , String address ) throws SOAPException
	{
		//SOAPFactory soapFactory = SOAPFactory.newInstance();
		
		SOAPElement addressElement = parent.addChildElement ( name );
		if ( address != null && ! "".equals ( address.trim() ) ) {
			Name addressName = soapFactory.createName ( ENDPOINT_ELEMENT_NAME );
			SOAPElement endpointElement = addressElement.addChildElement ( addressName );
			endpointElement.addTextNode ( address );
		}	
		Name uriName = soapFactory.createName ( REFERENCE_ELEMENT_NAME );
		
		SOAPElement uriElement = addressElement.addChildElement ( uriName );
		uriElement.addTextNode ( uri );
		
	}
	
	/**
	 * 
	 * Extracts endpoint of address element.
	 * @param addressNode The address element.
	 * @return The endpoint, or null if not found.
	 * @throws SOAPException
	 */
	private static String extractAddressFromAddressNode ( SOAPElement addressNode )
	throws SOAPException
	{
		String ret = null;
		Iterator children = addressNode.getChildElements();
		while ( children.hasNext() ) {
			SOAPElement child = ( SOAPElement ) children.next();
			if ( child.getLocalName().equals ( ENDPOINT_ELEMENT_NAME ) ) {
				ret = child.getValue();
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * Extracts reference of address element.
	 * @param addressNode The address element.
	 * @return The reference, or null if not found.
	 * @throws SOAPException
	 */
	private static String extractReferenceFromAddressNode ( SOAPElement addressNode )
	throws SOAPException
	{
		String ret = null;
		Iterator children = addressNode.getChildElements();
		while ( children.hasNext() ) {
			SOAPElement child = ( SOAPElement ) children.next();
			if ( child.getLocalName().equals ( REFERENCE_ELEMENT_NAME ) ) {
				ret = child.getValue();
			}
		}
		
		return ret;
	}	
	
	private static SOAPFaultException createSOAPFaultException ( String reason , QName code )
	{
        SOAPFault fault = null;
        try {
			fault = soapFactory.createFault ( reason , code );
		} catch ( SOAPException e ) {
			throw new ProtocolException ( e );
		}
        return new SOAPFaultException ( fault );
	}
	
	public static void insertPropagationHeader ( 
		SOAPMessage msg , String root , String tid , 
		boolean serial , long timeout , String coordinatorAddress, String coordinatorId , Properties properties )
		throws SOAPException
	{
		//SOAPFactory soapFactory = SOAPFactory.newInstance();
		
		Name name = soapFactory.createName ( PROPAGATION_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
		SOAPHeader header = msg.getSOAPHeader();
		if (header == null)
              header = envelope.addHeader();
		
		SOAPHeaderElement he = header.addHeaderElement ( name );
		he.setActor ( Utils.ATOMIKOS_ACTOR );
		he.setMustUnderstand ( true );
		Name rName = soapFactory.createName ( ROOT_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPElement rElement = ( SOAPElement ) he.addChildElement ( rName );
		rElement.addTextNode ( root );
		Name tName = soapFactory.createName ( TID_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPElement tElement = ( SOAPElement ) he.addChildElement ( tName );
		tElement.addTextNode ( tid );
		Name timeoutName = soapFactory.createName ( TIMEOUT_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPElement timeoutElement = ( SOAPElement ) he.addChildElement ( timeoutName );
		timeoutElement.addTextNode ( timeout + "");
		Name sName = soapFactory.createName ( SERIAL_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPElement sElement = ( SOAPElement ) he.addChildElement ( sName );
		sElement.addTextNode ( serial + "" );
		Name cName = soapFactory.createName ( COORDINATOR_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		
		String coordinatorURI = CommitServer.createGlobalUri ( AtomikosHttpTransport.getSingleton().getCoordinatorAddress() , coordinatorId  );
		insertAddressNode ( he , cName , coordinatorURI , coordinatorAddress );
		
		if ( properties != null && ! properties.isEmpty() ) {
			Name propertiesName = soapFactory.createName ( PROPERTIES_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
			SOAPElement propertiesElement = ( SOAPElement ) he.addChildElement ( propertiesName );
			Enumeration propertyNames = properties.propertyNames();
			while ( propertyNames.hasMoreElements() ) {
				Name propertyName = soapFactory.createName ( PROPERTY_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
				SOAPElement propertyElement = propertiesElement.addChildElement ( propertyName );
				Name propertyNameName = soapFactory.createName ( PROPERTY_NAME_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
				SOAPElement propertyNameElement = propertyElement.addChildElement ( propertyNameName );
				String pname = ( String ) propertyNames.nextElement();
				propertyNameElement.addTextNode ( pname );
				Name propertyValueName = soapFactory.createName ( PROPERTY_VALUE_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
				SOAPElement propertyValueElement = propertyElement.addChildElement ( propertyValueName );
				String pvalue = properties.getProperty ( pname );
				propertyValueElement.addTextNode ( pvalue );
			}
		}
		
	}

	public static void insertExtentHeader ( SOAPMessage msg , String parentTid , String localUri , 
	HeuristicMessage[] msgs ,Hashtable table  )
	throws  SOAPException
	{

		Configuration.logDebug ( "Inserting extent for tid " + parentTid);
		//SOAPFactory soapFactory = SOAPFactory.newInstance();
		Name name = soapFactory.createName ( Utils.EXTENT_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
		SOAPHeader header = msg.getSOAPHeader();
		SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
		if (header == null)
              header = envelope.addHeader();
		
		SOAPHeaderElement he = header.addHeaderElement ( name );
		he.setActor ( Utils.ATOMIKOS_ACTOR );
		he.setAttribute ( Utils.TID_ATTRIBUTE_NAME , parentTid );
		
		//VERY IMPORTANT: convert the local URI into a globally unique one by appending our address
		localUri = CommitServer.createGlobalUri ( AtomikosHttpTransport.getSingleton().getParticipantAddress() , localUri );
		
		
		//make sure the table is updated to include the new participant 
		Integer count = ( Integer ) table.get ( localUri );
		if ( count == null ) count = new Integer ( 0 );
		count = new Integer ( count.intValue() + 1 );
		table.put ( localUri , count );
		
		Enumeration uris = table.keys();
		while ( uris.hasMoreElements() ) {
			String uri = ( String ) uris.nextElement();
			count = ( Integer ) table.get ( uri );
			Name infoName = soapFactory.createName ( Utils.EXTENT_INFO_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
			SOAPElement extentInfoElement = he.addChildElement ( infoName );
			Name pName = soapFactory.createName ( Utils.PARTICIPANT_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
			if ( uri.equals( localUri ) ) {
				Configuration.logDebug ( "Adding address node with uri " + uri + " and address " + AtomikosHttpTransport.getSingleton().getParticipantAddress());
				insertAddressNode ( extentInfoElement , pName , uri , AtomikosHttpTransport.getSingleton().getParticipantAddress() );
			}
			else {
				Configuration.logDebug ( "Adding address node with uri " + uri );
				insertAddressNode ( extentInfoElement , pName , uri , null );
			}
			Name cName = soapFactory.createName ( COUNT_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE );
			SOAPElement countElement = extentInfoElement.addChildElement ( cName );
			countElement.addTextNode ( count.toString() );
			if ( uri.equals ( localUri ) ) {
				//add heuristic info for direct participant only
				
				for ( int i = 0 ; i < msgs.length ; i++ ) {
					Name hName = soapFactory.createName ( HEURISTIC_INFO_ELEMENT_NAME , "atomikos" , Utils.ATOMIKOS_NAMESPACE);
					SOAPElement infoElement = extentInfoElement.addChildElement ( hName );
					//Configuration.logDebug ( "Adding heur msg: " + msgs[i]);
					infoElement.addTextNode ( msgs[i].toString() );
				}
			}
		}

	}	

	public static Propagation extractPropagationFromHeader ( SOAPHeaderElement header )
	throws SOAPFaultException, SOAPException
	{
			
		String root = null, tid = null, timeoutString = null, serialString = null;
		String coordinatorAddress = null, coordinatorUri = null;
		
		Properties props = new Properties();
		
		Iterator propagationElements = header.getChildElements();	
		while ( propagationElements.hasNext() ) {
			SOAPElement el = ( SOAPElement ) propagationElements.next();
			String elName = el.getLocalName();
			if ( ROOT_ELEMENT_NAME.equals ( elName ) ) 
				root = el.getValue();
			else if ( TID_ELEMENT_NAME.equals ( elName ))
				tid = el.getValue();
			else if ( TIMEOUT_ELEMENT_NAME.equals ( elName ) )
				timeoutString = el.getValue();
			else if ( SERIAL_ELEMENT_NAME.equals ( elName ) )
				serialString = el.getValue();
			else if ( COORDINATOR_ELEMENT_NAME.equals ( elName ) ) {
				coordinatorAddress = extractAddressFromAddressNode ( el );
				coordinatorUri = extractReferenceFromAddressNode ( el );	
			}
			else if ( PROPERTIES_ELEMENT_NAME.equals ( elName ) ) {
				Iterator properties = el.getChildElements();
				while ( properties.hasNext() ) {
					SOAPElement p = ( SOAPElement ) properties.next();
					String name = null , value = null;
					Iterator content = p.getChildElements();
					while ( content.hasNext() ) {
						SOAPElement cel = ( SOAPElement ) content.next();
						String celName = cel.getLocalName();
						if ( PROPERTY_NAME_ELEMENT_NAME.equals ( celName ) ) {
							name = cel.getValue();
						}
						else if ( PROPERTY_VALUE_ELEMENT_NAME.equals ( celName ) ) {
							value = cel.getValue();
						}
					}
					props.setProperty  ( name , value );
;				}
			}
			else {
				
				QName code = new QName ( SOAP_ENVELOPE_NAMESPACE , "MustUnderstand" );
				throw createSOAPFaultException ( 
								"Unexpected element in transaction propagation header: " + elName , 
								code );				
			}
		}
			
		if ( root == null ) {
			QName code = new QName (
					SOAP_ENVELOPE_NAMESPACE, "MustUnderstand" );
			throw createSOAPFaultException ( 
											"Missing element in transaction propagation header: " +
												ROOT_ELEMENT_NAME , 
										    code);		
		}
		if ( tid == null ) {
			QName code = new QName (
					SOAP_ENVELOPE_NAMESPACE, "MustUnderstand" );
			throw createSOAPFaultException ( 
											"Missing element in transaction propagation header: " +
												TID_ELEMENT_NAME , 
											code );						
		}
		if ( serialString == null ) {
			QName code = new QName (
					SOAP_ENVELOPE_NAMESPACE , "MustUnderstand" );
			throw createSOAPFaultException ( 
											"Missing element in transaction propagation header: " +
												SERIAL_ELEMENT_NAME , 
											code );					
		}
		if ( timeoutString == null ) {
			QName code = new QName (
					SOAP_ENVELOPE_NAMESPACE, "MustUnderstand" );
			throw createSOAPFaultException ( 
											"Missing element in transaction propagation header: " +
												TIMEOUT_ELEMENT_NAME , 
											code );					
		}			
		if ( coordinatorAddress == null || coordinatorUri == null ) {
			QName code = new QName (
					SOAP_ENVELOPE_NAMESPACE, "MustUnderstand" );
			throw createSOAPFaultException ( 
											"Missing or incomplete element in transaction propagation header: " +
												COORDINATOR_ELEMENT_NAME , 
											code );		
		}			
		
		
		//here we are sure nothing is null
		root = root.trim();
		serialString = serialString.trim();
		timeoutString = timeoutString.trim();
		tid = tid.trim();
		coordinatorUri = coordinatorUri.trim();
		coordinatorAddress = coordinatorAddress.trim();
			
		
		
		
		MessageRecoveryCoordinator mrc =
			new MessageRecoveryCoordinator (
				coordinatorUri,
				coordinatorAddress,
		AtomikosHttpTransport.getSingleton()
			);
		boolean serial = "true".equals ( serialString );
		long timeout = 0;
		try {
			timeout = Long.parseLong ( timeoutString );
		}
		catch ( NumberFormatException e ) {
			QName code = new QName ( SOAP_ENVELOPE_NAMESPACE , "Client");
			throw createSOAPFaultException ( 
											"Wrong element value in transaction propagation header: " +
												TIMEOUT_ELEMENT_NAME , 
											code );			
		}
		
		CompositeTransactionAdaptor rootAdaptor =
			new CompositeTransactionAdaptor ( 
				root,
				serial,
				mrc , props
			);
		Stack lineage = new Stack();
		lineage.push ( rootAdaptor );
        
        
		if ( !root.equals ( tid ) ) {
			CompositeTransactionAdaptor parent =
				new CompositeTransactionAdaptor (
					lineage ,
					tid,
					serial,
					mrc , null 
				);
				lineage.push ( parent );
		}
		
        
		PropagationImp atoPropagation = 
			new PropagationImp ( 
				lineage , 
				serial ,
				timeout );


		return atoPropagation;
	}
	
	public static String extractExtentFromHeader ( SOAPHeaderElement header , Extent extent )
	throws SOAPException
	{
		//SOAPFactory factory = SOAPFactory.newInstance();
		String ret = null;
		ret = header.getAttribute ( TID_ATTRIBUTE_NAME );
		if ( ret == null ) {
			throw new SOAPException ( "Missing attribute in extent: " + TID_ATTRIBUTE_NAME );	
		}
		
		
		Hashtable remotes = new Hashtable();
		Stack directs = new Stack();
		Iterator elements = header.getChildElements();
		boolean directParticipantFound = false;
		
		if ( ! elements.hasNext() ) 
			throw new SOAPException ( "An empty extent was detected" );
	
			
		while ( elements.hasNext() ) {
			
			SOAPElement el = ( SOAPElement ) elements.next();
			Configuration.logDebug ( "Parsing element: " + el.getLocalName() );
			String uri = null, address = null , countString = null;
			List messages = new ArrayList();
			
			Iterator it = el.getChildElements();
			while ( it.hasNext() ) {
				SOAPElement elem = ( SOAPElement ) it.next();
				if ( PARTICIPANT_ELEMENT_NAME.equals ( elem.getLocalName()) ) {
					uri = extractReferenceFromAddressNode ( elem );
					address = extractAddressFromAddressNode ( elem );
					String logmsg = "Parsed participant: " + uri;
					if ( address != null ) logmsg = logmsg + " with address " + address;
					Configuration.logDebug ( logmsg );
				}
				else if ( COUNT_ELEMENT_NAME.equals ( elem.getLocalName()) ) {
					
					countString = elem.getValue();	
					Configuration.logDebug ( "Parsed count: " + countString );					
				}
				else if ( HEURISTIC_INFO_ELEMENT_NAME.equals ( elem.getLocalName())) {
					
					messages.add ( new StringHeuristicMessage ( elem.getValue() ));
					Configuration.logDebug ( "Parsed heuristic message: " + elem.getValue());
				}
				else throw new SOAPException ( "Illegal element in extent: " + elem.getLocalName()  );
			}
			

			
			HeuristicMessage[] harray = ( HeuristicMessage[] ) messages.toArray ( new HeuristicMessage[0] );
			
			if ( uri == null ) throw new SOAPException ( "No participant reference found in element " +  el.getLocalName());
			if ( countString == null ) throw new SOAPException ( "No count found in element " + el.getLocalName() );
			
			
			if ( address != null ) {
				//direct participant -> add, but ONLY IF DIFFERENT FROM OURSELVES
				//to avoid deadlocking 2PC methods
				directParticipantFound = true;
				if ( ! address.equals ( AtomikosHttpTransport.getSingleton().getParticipantAddress() ) ) {
					Participant p = new MessageParticipant ( uri , address ,
					AtomikosHttpTransport.getSingleton() , harray , false , false ); 
					
					
					directs.push ( p );
				}	
			}
			Integer count = new Integer ( countString );
			remotes.put ( uri , count );
			
		}
		
		if ( !directParticipantFound ) throw new SOAPException ( "Invalid extent: at least one participant should include " + ENDPOINT_ELEMENT_NAME );	
				
		
		Extent tmp = new ExtentImp ( remotes , directs );
		extent.add(tmp);
		return ret;	
	}

}
