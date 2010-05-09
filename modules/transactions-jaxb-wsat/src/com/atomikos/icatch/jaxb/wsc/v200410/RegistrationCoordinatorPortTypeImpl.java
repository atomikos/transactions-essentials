
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.atomikos.icatch.jaxb.wsc.v200410;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.icatch.imp.ReadOnlyParticipant;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.RegisterMessageImp;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;


/**
 * Registration coordinator implementation.
 * 
 */

@javax.jws.WebService(name = "RegistrationCoordinatorPortType", serviceName = "CoordinationService",
                      portName = "RegistrationCoordinatorPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wscoor.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsc.v200410.RegistrationCoordinatorPortType")
                      
public class RegistrationCoordinatorPortTypeImpl  extends AbstractBinding implements RegistrationCoordinatorPortType
{

	 public static final String WSAT_COMPLETION_PROTOCOL_URI_08_2005 = 
	    	"http://schemas.xmlsoap.org/ws/2004/10/wsat/Completion";
	    	
	 public static final String WSAT_VOLATILE_2PC_PROTOCOL_URI_08_2005 =
			"http://schemas.xmlsoap.org/ws/2004/10/wsat/Volatile2PC";
	    
	 public static final String WSAT_DURABLE_2PC_PROTOCOL_URI_08_2005 =
			"http://schemas.xmlsoap.org/ws/2004/10/wsat/Durable2PC";

  
    public void registerOperation(
        com.atomikos.icatch.jaxb.wsc.v200410.RegisterType req
    )
    { 

		logMessageIfDebug();
		
    	
    	
		
        try
        {
					HttpTransport transport = WsatHttpTransport.getSingleton();
					String protocolURI = req.getProtocolIdentifier().toString();
					
                	if ( WSAT_COMPLETION_PROTOCOL_URI_08_2005.equals ( protocolURI ) ) {
                		//ignore; just wait until completion comes in
                		//and at that time simulate an incoming 1PC participant
                		//instruction
                		
                	}
                	else if ( WSAT_VOLATILE_2PC_PROTOCOL_URI_08_2005.equals ( protocolURI ) ) {
                		//register a SubTxAware wrapper
            
                		CompositeTransaction ct  = findTransaction();
                		if ( ct != null ) {
            				//String senderURI = getSenderURI();
                			    
            				
							EndpointReferenceType epr = req.getParticipantProtocolService();
							
							//THE TARGET address is where the new participant can be reached;
							//this is needed in the registration process
							OutgoingAddressingHeaders targetAddress = OutgoingAddressingHeaders.extractFromEPR ( epr );
							String senderURI = targetAddress.getTargetURI();
							
            				String targetURI = getTargetURI();
            				
            				Object senderAddress = getSenderAddress();
            				
            				RegisterMessageImp rm = new RegisterMessageImp (
            					transport.getCommitProtocol() , transport.getFormat(),
            					targetAddress, targetURI , senderAddress , senderURI , false );
            				
            				transport.requestReceived ( rm );
                		}
                		else {
                			//invalid state - cf WS-AT state tables
							Configuration.logInfo ( "Registration port: registration request for non-existent transaction: " + getTargetURI() );
                			sendInvalidStateFault();	
                		}
                	}
                	else if ( WSAT_DURABLE_2PC_PROTOCOL_URI_08_2005.equals ( protocolURI ) ) {
                		//register a regular MessageParticipant
            			CompositeTransaction ct  = findTransaction();
            			if ( ct != null ) {
            				//String senderURI = getSenderURI();
            				
            				//add readonly participant to force PREPARE: WSAT doesn't 
            			    //support 1PC!!!
            				ReadOnlyParticipant p = new ReadOnlyParticipant ( ( CoordinatorImp ) ct.getCompositeCoordinator() );
            				ct.addParticipant ( p );
            				EndpointReferenceType epr = req.getParticipantProtocolService();
							

							
						OutgoingAddressingHeaders targetAddress = OutgoingAddressingHeaders.extractFromEPR ( epr );
						String senderURI = targetAddress.getTargetURI();
            				String targetURI = getTargetURI();
            				Object senderAddress = getSenderAddress();
            				
            				RegisterMessageImp rm = new RegisterMessageImp (
            					transport.getCommitProtocol() , transport.getFormat(),
            					targetAddress, targetURI , senderAddress , senderURI , true );
            				
            				transport.requestReceived ( rm );
            			}
            			else {
            				//invalid state - cf WS-AT state tables
            				Configuration.logInfo ( "Registration port: registration request for non-existent transaction: " + getTargetURI() );
            				sendInvalidStateFault();	
            			}    		
                	}
                	else {
                		//UNKNOWN PROTOCOL URI -> generate fault
                		Configuration.logWarning ( "Registration port: received invalid protocol URI: " + protocolURI );
                		sendInvalidProtocolFault();
                	}
        }
        catch (Exception e)
        {
            Configuration.logWarning ( "Registration port: unexpected error" , e );
            sendInvalidParametersFault();
        }

    }

}
