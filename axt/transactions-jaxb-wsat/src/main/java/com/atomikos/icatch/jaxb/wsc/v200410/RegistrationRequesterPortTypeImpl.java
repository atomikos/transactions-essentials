
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.atomikos.icatch.jaxb.wsc.v200410;

import com.atomikos.icatch.msg.CommitServer;
import com.atomikos.icatch.msg.MessageRecoveryCoordinator;
import com.atomikos.icatch.msg.RegisteredMessage;
import com.atomikos.icatch.msg.RegisteredMessageImp;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;


/**
 * Implementation of RegistrationRequester port.
 * 
 */

@javax.jws.WebService(name = "RegistrationRequesterPortType", serviceName = "RegistrationRequesterService",
                      portName = "RegistrationRequesterPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wscoor.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsc.v200410.RegistrationRequesterPortType")
                      
public class RegistrationRequesterPortTypeImpl extends AbstractBinding implements RegistrationRequesterPortType {

    
    public void registerResponseOperation(
        com.atomikos.icatch.jaxb.wsc.v200410.RegisterResponseType parameters
    )
    { 
        try
        {
            logMessageIfDebug();          
            Object targetAddress = getTargetAddress();
            Object senderAddress = getSenderAddress();
            String targetURI = getTargetURI();
            String senderURI = getSenderURI();
            WsatHttpTransport t = (WsatHttpTransport) WsatHttpTransport.getSingleton();
		    RegisteredMessage msg = new RegisteredMessageImp ( t.getCommitProtocol() , t.getFormat() , 
				targetAddress, targetURI , senderAddress , senderURI  );
            t.replyReceived ( msg );
            
 		   com.atomikos.icatch.TransactionService ts = Configuration.getTransactionService();
		   String participantURI = CommitServer.extractLocalIdPart ( targetURI );
		   
		   //Configuration.logDebug ( "Getting recovery coordinator of class: " + ts.getSuperiorRecoveryCoordinator ( participantURI ).getClass() );
		   MessageRecoveryCoordinator mrc = ( MessageRecoveryCoordinator ) ts.getSuperiorRecoveryCoordinator ( participantURI );
            
		   //set address of remote coordinator on the participant
		   EndpointReferenceType epr = parameters.getCoordinatorProtocolService();
		   if ( epr == null ) Configuration.logWarning ( "WS-C: RegistrationResponse has no coordinator address!" );
		   try {
			   senderAddress = com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders.extractFromEPR ( epr );
			   if ( mrc == null ) {
					  Configuration.logDebug ( "RegistrationRequester port: message recovery coordinator not found for target: " + participantURI );
			   }
			   else mrc.setAddress ( senderAddress );        
		   }
		   catch ( Exception e ) {
			   Configuration.logWarning ( "WS-C: RegistrationResponse coordinator extraction failed!" );
		   }

		       
        }
        catch (Exception e)
        {
            Configuration.logWarning ( "RegistrationRequester port: unexpected error" , e );
        }
    }

}
