//$Id: Utils.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: Utils.java,v $
//Revision 1.1.1.1  2006/10/02 15:20:58  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.2  2006/03/21 13:23:54  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:24  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/11/12 13:45:49  guy
//Redesigned registration to wait for response
//
//Revision 1.5  2005/11/11 07:57:34  guy
//Corrected after testing.
//
//Revision 1.4  2005/11/08 16:38:47  guy
//Debugged.
//
//Revision 1.3  2005/11/07 10:57:20  guy
//Adapted after testing.
//
//Revision 1.2  2005/10/28 15:24:39  guy
//Corrected after testing.
//
//Revision 1.1  2005/10/21 08:17:18  guy
//Added utility class for WSC version 1.0
//
package com.atomikos.icatch.jaxb.wsc.v200410;


import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.imp.CompositeTransactionAdaptor;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.jaxb.wsa.v200408.AddressingXmlHelper;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.msg.MessageRecoveryCoordinator;
import com.atomikos.icatch.msg.RegisterMessage;
import com.atomikos.icatch.msg.TransactionMessage;
import com.atomikos.icatch.msg.Transport;
import com.atomikos.icatch.system.Configuration;
/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * Utilities for the WS-C and WS-T protocols.
 * This class is in a separate package because it should
 * work for both WS-AT and WS-BA.
 *
 * 
 */

public final class Utils
{

    public static final String TYPE_ELEMENT_NAME = "CoordinationType";

    public static final String CONTEXT_HEADER_NAME = "CoordinationContext";

    public static final String WSC_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2004/10/wscoor";
    
    public static final String WSC_BASE_ACTION_URI = WSC_NAMESPACE_URI + "/";

    public static final String WSAT_TYPE_URI = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
    
    private static final String ID_ELEMENT_NAME = "Identifier";
    
	private static final String EXPIRES_ELEMENT_NAME = "Expires";
    
    private static final String REG_SERVICE_ELEMENT_NAME = "RegistrationService";
    
 
	public static final String WSAT_DURABLE_2PC_PROTOCOL_URI_08_2005 =
		"http://schemas.xmlsoap.org/ws/2004/10/wsat/Durable2PC";

	private static SOAPFactory soapFactory = null;
    
    static {
    		try {
    			soapFactory = SOAPFactory.newInstance();
    		}
    		catch ( Exception e ) {
    			Configuration.logWarning ( "Failed to create SOAPFactory" , e );
    		}
    }
    
    
    private Utils()
    {
        //not to be instantiated
    }
    
	
    
    private static void soapFault ( String message )
    throws SOAPFaultException
    {
    
    		QName code = new QName (
                "http://schemas.xmlsoap.org/soap/envelope", "MustUnderstand" );
		
    		SOAPFault fault = null;
        try {
			fault = soapFactory.createFault ( message , code );
		} catch ( SOAPException e ) {
			throw new ProtocolException ( e );
		}
        throw new SOAPFaultException ( fault );
    }
   

    /**
     * Extracts a propagation from the header
     * @param header
     * @param type The target coordination type
     * @param transport The transport to use
     * @param defaultTimeout The timeout if none in header
     * @return The propagation, or null if not found
     */
    public static Propagation extractPropagationFromHeader ( SOAPHeaderElement header, String type , Transport transport , long defaultTimeout ) throws SOAPException
    {
    	
    	if ( header == null ) throw new IllegalArgumentException ( "Null header not allowed" );
    	
    	if ( ! header.getNamespaceURI().equals ( WSC_NAMESPACE_URI ) ) {
    		Configuration.logDebug ( "SOAPImportingTransactionManager: Expected namespace: " + 
    			WSC_NAMESPACE_URI + " but found: " + header.getNamespaceURI() );
    		
    		//ignore: return null as specified
    		return null;
		}
		if ( ! header.getLocalName().equals ( CONTEXT_HEADER_NAME ) ) {
			Configuration.logDebug ( "SOAPImportingTransactionManager: Expected element: " + 
				CONTEXT_HEADER_NAME + " but found: " + header.getLocalName() );
			soapFault ( "Expected element: " + 
				CONTEXT_HEADER_NAME + " but found: " + header.getLocalName() );	
		}
		
		Propagation ret = null;
		
		//here we are certain that a coordination context is there for this version
		String id = null;
		
		long expires = defaultTimeout;
		EndpointReference registrationServiceEPR = null;
		Iterator children = header.getChildElements ();
		boolean typeOK = false;
		while ( children.hasNext() ) {
			SOAPElement next = ( SOAPElement ) children.next();
			String name = next.getLocalName();
			if ( name.endsWith ( ID_ELEMENT_NAME )  ) {
				//extract identifier
				id = next.getValue();
			}
			else if ( name.endsWith ( TYPE_ELEMENT_NAME )  ) {
				//extract type and assert it is OK
				if ( !next.getValue().equals ( type ) ) {
					Configuration.logDebug ( "SOAPImportingTransactionManager: Expected type: " + 
						type + " but found: " + next.getValue() );
					//return null as specified
					
				}
				else typeOK = true;
					
			}
			else if ( name.endsWith ( EXPIRES_ELEMENT_NAME )) {
				
				expires = Long.parseLong ( next.getValue() );	
				//System.err.println ( "Extracting expiry value: " + expires );
			}
			
		}
			
		if ( typeOK ) {
			
			long timeout = expires;
		
			if ( timeout <= 0 ) soapFault ( "The context has already expired" );	
	
			if ( id == null ) soapFault ( "Empty required context element: " + ID_ELEMENT_NAME );
			
			//assume serial transactions for WS-T???
			boolean serial = true;

			
			//Note: address of mrc is null because it is only available
			//after WSC's registerResponse is received; we will use the ReplyTo address
			//supplied by prepare instead; this should not be a problem because
			//reply is only necessary AFTER prepare is done anyway...
			MessageRecoveryCoordinator mrc =
				new MessageRecoveryCoordinator (
					id ,
					null,
					transport
				);
			
			//Construct proxy for root;
			CompositeTransactionAdaptor rootAdaptor =
						new CompositeTransactionAdaptor ( 
							id,
							serial,
							mrc , null
						);
			Stack lineage = new Stack();
			lineage.push ( rootAdaptor );
            
			ret = new PropagationImp ( 
				lineage , 
				serial ,
				timeout );

		}
			
        return ret;
    }
    
    /**
     * Extracts a registration service address from a soap header element.
     * @param header The header
     * @return The address information, or null if not found.
     */
    public static OutgoingAddressingHeaders extractRegistrationServiceAddress ( SOAPHeaderElement header ) throws SOAPException
    {

		if ( header == null ) throw new IllegalArgumentException ( "Null header not allowed" );
    	
		if ( ! header.getNamespaceURI().equals ( WSC_NAMESPACE_URI ) ) {
			Configuration.logDebug ( "SOAPImportingTransactionManager: Expected namespace: " + 
				WSC_NAMESPACE_URI + " but found: " + header.getNamespaceURI() );
			throw new IllegalArgumentException ( "Expected namespace: " + 
				WSC_NAMESPACE_URI + " but found: " + header.getNamespaceURI() );
		}
		if ( ! header.getLocalName().equals ( CONTEXT_HEADER_NAME ) ) {
			Configuration.logDebug ( "SOAPImportingTransactionManager: Expected element: " + 
				CONTEXT_HEADER_NAME + " but found: " + header.getLocalName() );
			throw new IllegalArgumentException ( "Expected element: " + 
				CONTEXT_HEADER_NAME + " but found: " + header.getLocalName() );	
		}

		OutgoingAddressingHeaders ret = null;
		Iterator children = header.getChildElements ();
		while ( children.hasNext() ) {
			SOAPElement next = ( SOAPElement ) children.next();
			String name = next.getLocalName();
			Configuration.logDebug ( "SOAPImportingTransactionManager: Inspecting element " + name );
			if ( name.endsWith ( REG_SERVICE_ELEMENT_NAME ) ) { 
				 ret = com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders.extractFromEPR(next);
			}		
		}
		return ret;	
    }

    /**
     * Registers as a subordinate with the remote coordination service.
     * 
     * @param registrationAddress The address where to register
     * @param registrationRequesterAddress The address where the response should go to
     * @param localParticipantURI The (GLOBALLY UNIQUE) URI of the participant to register
     * @param t The transport to use (protocol specific)
     * @param timeout The timeout in millis to wait for registration acknowledgement.
     *
     */
    public static void registerAsParticipant ( OutgoingAddressingHeaders registrationAddress , String registrationRequesterAddress ,
    	String localParticipantURI , Transport t , long timeout ) throws 
    	MalformedURLException, RemoteException, SOAPException
    {
  
		
		//create a unique URI to wait for reply, by combining requester URL and participant URI
		String senderUri = registrationRequesterAddress + localParticipantURI;
		//target URI indicates the target for the REPLY in this case!
		String targetUri = localParticipantURI;
		//Transport t = WsatHttpTransport.getSingleton();
        RegisterMessage regMessage = t.createRegisterMessage ( senderUri , targetUri , registrationAddress , true );
        int[] expected = { TransactionMessage.REGISTERED_MESSAGE , TransactionMessage.ERROR_MESSAGE };
		TransactionMessage  reply = null;
        try
        {
           reply = t.sendAndReceive ( regMessage , timeout , expected );
        }
        catch (Exception e) {
			Configuration.logWarning ( "SOAPImportingTransactionManager: registration failed for participant: " + localParticipantURI , e );
			throw new SOAPException ( "Registration failed: " + e.getMessage() );
        }
        if ( reply == null ) {
			Configuration.logWarning ( "SOAPImportingTransactionManager: registration failed due to timeout for participant: " + localParticipantURI );
			Configuration.logWarning ( "SOAPImportingTransactionManager: this is typically caused by an intermediate timeout/rollback at the caller side" );
			throw new SOAPException ( "Registration failed: timeout expired without response - this is typically caused by an intermediate rollback at the caller side" );
        }
        
        //here we are certain that reply is not null
        int replyType = reply.getMessageType();
        if ( replyType == TransactionMessage.REGISTERED_MESSAGE ) {
        	//expected -> proceed	
        	Configuration.logDebug ( "SOAPImportingTransactionManager: registration ok for participant: " + localParticipantURI );
        }
        else if ( replyType == TransactionMessage.ERROR_MESSAGE ) {
        	//not registered -> don't proceed
			Configuration.logWarning ( "SOAPImportingTransactionManager: registration failed for participant: " + 
				localParticipantURI + " with error message: " + reply );
			throw new SOAPException ( "Registration failed" );
        }
    }

    /**
     * Inserts a propagation of the given type into the message.
     * @param msg
     * @param root
     * @param tid
     * @param timeout
     * @param regServiceAddress
     * @param type
     */
    public static void insertPropagationHeader ( SOAPMessage msg, String root , String tid, long timeout , String regServiceAddress , String type ) throws SOAPException
    {
        
		SOAPFactory soapFactory = SOAPFactory.newInstance();
		
        //Get SOAP message header part
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope senv = sp.getEnvelope();
        SOAPHeader sh = senv.getHeader();
		if (sh == null)
            sh = senv.addHeader();

        //add context header
		Name name = soapFactory.createName ( CONTEXT_HEADER_NAME , "wscoor" , WSC_NAMESPACE_URI );
		SOAPHeaderElement he = sh.addHeaderElement ( name );		
		//mustunderstand required by specs
		he.setMustUnderstand ( true );       
        
        //Add nested elements: id, type, expires and registration service address
        
        //Add id
        Name idName = soapFactory.createName ( ID_ELEMENT_NAME , "wscoor" , WSC_NAMESPACE_URI );
        SOAPElement el = he.addChildElement ( idName );
        el.addTextNode ( root );
        
        //Add expiry
        Name expName = soapFactory.createName ( EXPIRES_ELEMENT_NAME , "wscoor" , WSC_NAMESPACE_URI );
        el = he.addChildElement ( expName );
        el.addTextNode ( "" + timeout );
        //System.err.println ( "Inserting expiry value: " + timeout );
        
        //Add type
        Name tName = soapFactory.createName ( TYPE_ELEMENT_NAME , "wscoor" , WSC_NAMESPACE_URI );
        el = he.addChildElement ( tName );
        el.addTextNode ( type );
        
        //Add registration service address
		SOAPElement regServiceAddressElement = AddressingXmlHelper.createEprWithTargetReferenceParameter ( REG_SERVICE_ELEMENT_NAME , 
				"wscoor" , WSC_NAMESPACE_URI , regServiceAddress , tid );
		he.addChildElement ( regServiceAddressElement );
        
    }

   
    /**
     * Finds a propagation header of the given WSC type.
     * 
     * @param msg The message
     * @param type The coordination type
     * @return The header, or null if not found
     */
    public static SOAPHeaderElement findPropagationHeader ( SOAPMessage msg , String type )  throws SOAPException
    {
		SOAPHeaderElement ret = null;
		SOAPPart sp             = msg.getSOAPPart();
		SOAPEnvelope senv       = sp.getEnvelope();
		SOAPHeader sh           = senv.getHeader();
    	
		Iterator iter =  sh.examineAllHeaderElements();
		while ( iter.hasNext() && ret == null ) {
			SOAPHeaderElement next = (SOAPHeaderElement) iter.next();
			if ( next.getNamespaceURI().equals ( WSC_NAMESPACE_URI ) ) {
				//WSAT header; check if context 
				if ( next.getLocalName().equals ( CONTEXT_HEADER_NAME ) ) {
					//context; check if type is WSAT
					Iterator children = next.getChildElements ();
					while ( children.hasNext() ) {
						SOAPElement el = ( SOAPElement ) children.next();
						if ( el.getLocalName().equals ( TYPE_ELEMENT_NAME ) ) {
							if ( el.getValue().equals ( type ) )
								ret = next;							
						}
					}
				}
			}
		}
		return ret;
    }

}
