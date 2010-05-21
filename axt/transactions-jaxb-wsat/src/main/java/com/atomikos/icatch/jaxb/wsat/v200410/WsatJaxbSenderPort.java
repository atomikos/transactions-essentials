
package com.atomikos.icatch.jaxb.wsat.v200410;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.w3c.dom.Element;

import com.atomikos.icatch.jaxb.wsa.v200408.AddressingConstants;
import com.atomikos.icatch.jaxb.wsa.v200408.AddressingXmlHelper;
import com.atomikos.icatch.jaxb.wsa.v200408.IncomingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsc.v200410.AttributedURI;
import com.atomikos.icatch.jaxb.wsc.v200410.EndpointReferenceType;
import com.atomikos.icatch.jaxb.wsc.v200410.ReferencePropertiesType;
import com.atomikos.icatch.jaxb.wsc.v200410.RegisterResponseType;
import com.atomikos.icatch.jaxb.wsc.v200410.RegisterType;
import com.atomikos.icatch.jaxb.wsc.v200410.RegistrationCoordinatorPortType;
import com.atomikos.icatch.jaxb.wsc.v200410.RegistrationRequesterPortType;
import com.atomikos.icatch.jaxb.wsc.v200410.Utils;
import com.atomikos.icatch.msg.CommitMessage;
import com.atomikos.icatch.msg.ErrorMessage;
import com.atomikos.icatch.msg.ForgetMessage;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.IllegalMessageTypeException;
import com.atomikos.icatch.msg.PrepareMessage;
import com.atomikos.icatch.msg.PreparedMessage;
import com.atomikos.icatch.msg.RegisterMessage;
import com.atomikos.icatch.msg.RegisteredMessage;
import com.atomikos.icatch.msg.ReplayMessage;
import com.atomikos.icatch.msg.RollbackMessage;
import com.atomikos.icatch.msg.SenderPort;
import com.atomikos.icatch.msg.StateMessage;
import com.atomikos.icatch.msg.TransactionMessage;
import com.atomikos.icatch.msg.TransportException;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * Implementation of a sender port for WSAT in JAXB.
 *
 * 
 */
public class WsatJaxbSenderPort implements SenderPort
{
	private static final String WSAT_NS_URI = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
	
	private static final String BASE_ACTION_URI = WSAT_NS_URI + "/";
	
	private static final String WSC_NS_URI = "http://schemas.xmlsoap.org/ws/2004/10/wscoor";
	
	private static final String WSC_WSDL_URI = WSC_NS_URI + "/wscoor.wsdl";
	
	private static final String WSAT_WSDL_URI = WSAT_NS_URI + "/wsat.wsdl";
	
	private static SOAPFactory factory;
	static {
		try {
			factory = SOAPFactory.newInstance();
		} catch (SOAPException se) {
			throw new RuntimeException(se);
		}
	}
	
	private static final String REGISTER_ELEMENT_NAME = "Register";
	
    public WsatJaxbSenderPort ( HttpTransport transport )
    {
       
		transport.setSenderPort ( this );
    }

	private String generateId()
	{
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
    
 
	private static <T> T getPort ( OutgoingAddressingHeaders addressData , 
			String wsdlUrlAsString,
			String nsUri , String serviceName , String portName , 
			Class <T> portType ) throws MalformedURLException, SOAPException 
	{
		T ret = null;
		URL wsdlUrl = new URL ( wsdlUrlAsString );
		Service s = Service.create(wsdlUrl , new QName(nsUri, serviceName));
		QName portQName = new QName ( nsUri , portName );
		ret = s.getPort ( portQName , portType );
		BindingProvider bp = ( BindingProvider ) ret;
 	    addressData.insertIntoRequestContext ( bp );
		return ret;
	}
    
    private RegistrationCoordinatorPortType getRegistrationPort ( OutgoingAddressingHeaders addressData ) throws SOAPException, MalformedURLException
    {
		return getPort ( addressData , WSC_WSDL_URI , WSC_NS_URI , "CoordinationService" , 
				"RegistrationCoordinatorPort" , RegistrationCoordinatorPortType.class );
		
    }
    
    private RegistrationRequesterPortType getRegisteredPort ( OutgoingAddressingHeaders addressData ) throws SOAPException, MalformedURLException
    {
    	
    	return getPort ( addressData , WSC_WSDL_URI , WSC_NS_URI , "RegistrationRequesterService" , 
				"RegistrationRequesterPort" , RegistrationRequesterPortType.class );
    }
    

    
    private ParticipantPortType getParticipantPort ( OutgoingAddressingHeaders addressData ) throws SOAPException, MalformedURLException 
    {
    	return getPort ( addressData , WSAT_WSDL_URI , WSAT_NS_URI , "TransactionService" , 
    			"ParticipantPort" , ParticipantPortType.class );
    }
    
    
    private CoordinatorPortType getCoordinatorPortType ( OutgoingAddressingHeaders addressData ) throws SOAPException, MalformedURLException
    {
    	return getPort ( addressData , WSAT_WSDL_URI , WSAT_NS_URI , "TransactionService" , 
    			"CoordinatorPort" , CoordinatorPortType.class );
    }

	private FaultPortType getFaultPortType ( OutgoingAddressingHeaders addressData ) throws SOAPException, MalformedURLException
	{		
		return getPort ( addressData , WSAT_WSDL_URI , WSAT_NS_URI , "FaultService" , 
    			"FaultPort" , FaultPortType.class );
	}
    /**
     * @see com.atomikos.icatch.msg.SenderPort#send(com.atomikos.icatch.msg.TransactionMessage)
     */
    public void send ( TransactionMessage msg )
        throws TransportException, IllegalMessageTypeException
    {
		Configuration.logDebug ( "Transport: about to send msg " + msg );
		

		try {
			
			String senderURI = msg.getSenderURI();
			//String senderAddress = ( String ) msg.getSenderAddress();
			WsatHttpTransport transport = ( WsatHttpTransport ) WsatHttpTransport.getSingleton();
			String coordinatorAddress = transport.getCoordinatorAddress();
			String participantAddress = transport.getParticipantAddress();
			String faultAddress = transport.getWsatFaultServiceURL();
			
			if ( msg instanceof PrepareMessage ) {
				OutgoingAddressingHeaders replyAddress = ( OutgoingAddressingHeaders ) msg.getTargetAddress();
				Notification toSend = new Notification();
				replyAddress.setAction ( BASE_ACTION_URI + "Prepare" );
				replyAddress.setFaultTo ( faultAddress );
				replyAddress.setReplyTo ( coordinatorAddress );
				replyAddress.setReplyToTarget ( senderURI );
				replyAddress.setFaultToTarget ( senderURI );
				replyAddress.setMessageId ( generateId() );
				ParticipantPortType participant = getParticipantPort ( replyAddress );
				participant.prepareOperation ( toSend );
			}
			else if ( msg instanceof CommitMessage ) {
				OutgoingAddressingHeaders replyAddress = ( OutgoingAddressingHeaders ) msg.getTargetAddress();
				Notification toSend = new Notification();	
				replyAddress.setAction ( BASE_ACTION_URI + "Commit" );
				replyAddress.setFaultTo ( faultAddress );
				replyAddress.setReplyTo ( coordinatorAddress );
				replyAddress.setReplyToTarget ( senderURI );
				replyAddress.setFaultToTarget ( senderURI );
				replyAddress.setMessageId ( generateId() );				
				ParticipantPortType participant =  getParticipantPort ( replyAddress );
				participant.commitOperation ( toSend );
			}
			else if ( msg instanceof RollbackMessage ) {
				OutgoingAddressingHeaders replyAddress = ( OutgoingAddressingHeaders ) msg.getTargetAddress();
				Notification toSend = new Notification();
				replyAddress.setAction ( BASE_ACTION_URI + "Rollback" );
				replyAddress.setFaultTo ( faultAddress );
				replyAddress.setReplyTo ( coordinatorAddress );
				replyAddress.setReplyToTarget ( senderURI );
				replyAddress.setFaultToTarget ( senderURI );
				replyAddress.setMessageId ( generateId() );
				
				ParticipantPortType participant =  getParticipantPort ( replyAddress );
				participant.rollbackOperation ( toSend );
			}
			else if ( msg instanceof ForgetMessage ) {
				Configuration.logDebug ( "Transport: ForgetMessage does not apply to WS-AT -> ignoring it!" );
			}
			else if ( msg instanceof StateMessage ) {
				IncomingAddressingHeaders inAddress = ( IncomingAddressingHeaders ) msg.getTargetAddress();
				OutgoingAddressingHeaders replyAddress = (OutgoingAddressingHeaders) inAddress.createReplyAddress();
				//DON't add reply data: not allowed for terminal messages
				StateMessage smsg = ( StateMessage ) msg;
				if ( smsg.hasCommitted() != null ) {
					boolean committed = smsg.hasCommitted().booleanValue();
					Notification toSend = new Notification();
					CoordinatorPortType coordinator = getCoordinatorPortType ( replyAddress );
					if ( committed ) {
						replyAddress.setAction ( BASE_ACTION_URI + "Committed" );
						coordinator.committedOperation ( toSend );
					}
					else {
						replyAddress.setAction ( BASE_ACTION_URI + "Aborted" );	
						coordinator.abortedOperation ( toSend );
					}
					
				}
				else {
					//no state known - ignore in WSAT
					Configuration.logDebug ( "Transport: Empty StateMessage does not apply to WS-AT -> ignoring it!" );	
				}
			}
			else if ( msg instanceof PreparedMessage ) {
				IncomingAddressingHeaders inAddress = ( IncomingAddressingHeaders ) msg.getTargetAddress();
				OutgoingAddressingHeaders replyAddress = (OutgoingAddressingHeaders) inAddress.createReplyAddress();
				PreparedMessage pmsg = ( PreparedMessage ) msg;
				boolean readonly = pmsg.isReadOnly();
				Notification toSend = new Notification();
				CoordinatorPortType coordinator = getCoordinatorPortType ( replyAddress );
				
				if ( readonly ) {
					//WSDL OF WSAT HAS MISSING SLASH FOR ACTION URI OF READONLY???
					replyAddress.setAction ( BASE_ACTION_URI + "ReadOnly");	
					coordinator.readOnlyOperation ( toSend );
				}
				else {
					replyAddress.setFaultTo ( faultAddress );
					replyAddress.setReplyTo ( participantAddress );
					replyAddress.setReplyToTarget ( senderURI );
					replyAddress.setFaultToTarget ( senderURI );
					replyAddress.setMessageId ( generateId() );
					replyAddress.setAction ( BASE_ACTION_URI + "Prepared" );
					coordinator.preparedOperation ( toSend );
				}
			}
			else if ( msg instanceof ReplayMessage ) {
				OutgoingAddressingHeaders replyAddress = ( OutgoingAddressingHeaders ) msg.getTargetAddress();
				Notification toSend = new Notification();
				CoordinatorPortType coordinator = getCoordinatorPortType ( replyAddress );
				replyAddress.setAction ( BASE_ACTION_URI + "Replay" );
				replyAddress.setFaultTo ( faultAddress );
				replyAddress.setReplyTo ( participantAddress );
				replyAddress.setReplyToTarget ( senderURI );
				replyAddress.setFaultToTarget ( faultAddress );
				replyAddress.setMessageId ( generateId() );	
				coordinator.replayOperation ( toSend );
			}
			else if ( msg instanceof ErrorMessage ) {
				IncomingAddressingHeaders inAddress = ( IncomingAddressingHeaders ) msg.getTargetAddress();
				OutgoingAddressingHeaders replyAddress = (OutgoingAddressingHeaders) inAddress.createReplyAddress();
				ErrorMessage emsg = ( ErrorMessage ) msg;
				int error = emsg.getErrorCode();
				CoordinatorPortType coordinator = getCoordinatorPortType ( replyAddress );
				if ( error == ErrorMessage.ROLLBACK_ERROR ) {
					Notification toSend = new Notification();
					replyAddress.setAction ( BASE_ACTION_URI + "Aborted" );	
					coordinator.abortedOperation ( toSend );
				}
				else if ( error >= ErrorMessage.HEURISTIC_MIN && error <= ErrorMessage.HEURISTIC_MAX ) {
					//Inconsistent internal state 
					OutgoingAddressingHeaders faultToAddress = (OutgoingAddressingHeaders) inAddress.createFaultAddress();
					faultToAddress.setAction ( BASE_ACTION_URI + "fault" );
					Fault fault = new Fault();
					fault.setFaultcode ( new QName ( BASE_ACTION_URI , "InconsistentInternalState" ) );	
					fault.setFaultstring ( "A global consistency failure has occurred. This is an unrecoverable condition." );
					
					FaultPortType faultPort = getFaultPortType ( faultToAddress );
					faultPort.faultOperation ( fault );
					
				}
				else {
					Configuration.logDebug ( "Transport: ErrorMessage does not apply to WS-AT: code " + error + " -> not sending it!" );	
				}
				
			}
			else if ( msg instanceof RegisteredMessage ) {
				RegisterResponseType toSend = new RegisterResponseType();
				IncomingAddressingHeaders inAddress = ( IncomingAddressingHeaders ) msg.getTargetAddress();
				OutgoingAddressingHeaders replyAddress = (OutgoingAddressingHeaders) inAddress.createReplyAddress();
				EndpointReferenceType epr = new EndpointReferenceType();
				AttributedURI uri = new AttributedURI ();
				uri.setValue ( coordinatorAddress );
				epr.setAddress ( uri );
				ReferencePropertiesType refProps = new ReferencePropertiesType();
				SOAPElement el = factory.createElement(new QName(AddressingConstants.TARGET_NS_URI , AddressingConstants.TARGET_ELEMENT_NAME) );
				el.setValue ( senderURI );
				refProps.getAny().add ( el );
				epr.setReferenceProperties ( refProps );
				toSend.setCoordinatorProtocolService ( epr );
				replyAddress.setAction ( "http://schemas.xmlsoap.org/ws/2004/10/wscoor/RegisterResponse" );
				RegistrationRequesterPortType stub = getRegisteredPort ( replyAddress );
				stub.registerResponseOperation ( toSend );
				

				
			}
			else if ( msg instanceof RegisterMessage ) {
				OutgoingAddressingHeaders replyAddress = ( OutgoingAddressingHeaders ) msg.getTargetAddress();
                String registrationFaultAddress = transport.getWscFaultServiceURL();
                String registrationRequesterAddress = transport.getRegistrationRequesterServiceURL();
                String localParticipantAddress = transport.getParticipantAddress();
                
				RegisterMessage toSend = ( RegisterMessage ) msg;
				if ( ! toSend.registerForTwo2PC() ) 
					throw new IllegalMessageTypeException ( "Not supported: volatile registration over SOAP");
					
				//send registration on behalf of local Importing TM
				replyAddress.setAction ( Utils.WSC_BASE_ACTION_URI + REGISTER_ELEMENT_NAME );
				replyAddress.setReplyToTarget ( senderURI );
				replyAddress.setFaultToTarget ( senderURI );
				replyAddress.setMessageId ( msg.getTargetURI() );
				replyAddress.setReplyTo ( registrationRequesterAddress );
				replyAddress.setFaultTo ( registrationFaultAddress );
				
    
        		
				RegistrationCoordinatorPortType port =  getRegistrationPort ( replyAddress );
				RegisterType parameters = new RegisterType();
				parameters.setProtocolIdentifier ( Utils.WSAT_DURABLE_2PC_PROTOCOL_URI_08_2005 );
				ReferencePropertiesType refProps = new ReferencePropertiesType();
				Element targetRefProperty = AddressingXmlHelper.createTargetReferencePropertyAsDomElement ( msg.getTargetURI() );
				refProps.getAny().add ( targetRefProperty );
				//SOAPElement[] props = new SOAPElement[1];
				//props[0] = factory.createElement(new QName(AddressingConstants.TARGET_NS_URI, AddressingConstants.TARGET_ELEMENT_NAME ) );
				//props[0].setTextContent( msg.getTargetURI() );
				//refProps.getAny().add ( props );
				AttributedURI participantURI = new AttributedURI();
				participantURI.setValue ( localParticipantAddress );
				EndpointReferenceType participantEPR = new EndpointReferenceType();
				participantEPR.setAddress ( participantURI );
				participantEPR.setReferenceProperties ( refProps );
				parameters.setParticipantProtocolService ( participantEPR );
				port.registerOperation ( parameters );
				
			}
			else {
				Configuration.logWarning ( "Transport: unexpected message - not sent: " + msg );
			}
		
						
			
			Configuration.logInfo ( "Transport: msg sent: " + msg );
		}
		catch ( Exception e ) {
			Configuration.logWarning ( e.getMessage() , e );
			throw new TransportException ( e );
		}
		
    }

}
