

package com.atomikos.icatch.jaxb.wsat.v200410;

import com.atomikos.icatch.jaxb.wsc.v200410.AbstractBinding;
import com.atomikos.icatch.msg.CommitMessage;
import com.atomikos.icatch.msg.CommitMessageImp;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.PrepareMessage;
import com.atomikos.icatch.msg.PrepareMessageImp;
import com.atomikos.icatch.msg.RollbackMessage;
import com.atomikos.icatch.msg.RollbackMessageImp;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;


/**
 * Implementation of participant port.
 * 
 */

@javax.jws.WebService(name = "ParticipantPortType", serviceName = "TransactionService",
                      portName = "ParticipantPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wsat.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsat.v200410.ParticipantPortType")
                      
public class ParticipantPortTypeImpl extends AbstractBinding implements ParticipantPortType {

  
    public void rollbackOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Notification parameters
    )
    { 
    		logMessageIfDebug();
		HttpTransport transport = WsatHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		try
        {
            
            int protocol = transport.getCommitProtocol();
            int format = transport.getFormat();     
            String targetURI = getTargetURI();
            String senderURI = getSenderURI();
            Object targetAddress = getTargetAddress();
            Object senderAddress = getSenderAddress();
            RollbackMessage msg = new RollbackMessageImp (  
            	protocol , 
            	format , targetAddress , targetURI , 
            	senderAddress , senderURI );
            transport.requestReceived ( msg ); 
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT participant port: error receiving rollback " , e );
        }   

    }

  
    public void commitOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Notification parameters
    )
    { 
		logMessageIfDebug();
		HttpTransport transport = WsatHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		
		try
        {
            
            int protocol = transport.getCommitProtocol();
            int format = transport.getFormat();     
            String targetURI = getTargetURI();
            String senderURI = getSenderURI();
            Object targetAddress = getTargetAddress();
            Object senderAddress = getSenderAddress();
            CommitMessage msg = new CommitMessageImp (  
            	protocol , 
            	format , targetAddress , targetURI , 
            	senderAddress , senderURI , false );
            //Configuration.logDebug ( "WSAT participant port: dispatching commit message to transport..." );
            transport.requestReceived ( msg );    
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT participant port: error receiving commit " , e );
        }	
    }

    public void prepareOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Notification parameters
    )
    { 
    		logMessageIfDebug();
		HttpTransport transport = WsatHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		
        try
        {
            int protocol = transport.getCommitProtocol();
            int format = transport.getFormat();     
            String targetURI = getTargetURI();
            String senderURI = getSenderURI();
            Object targetAddress = getTargetAddress();
            Object senderAddress = getSenderAddress();
            PrepareMessage msg = new PrepareMessageImp (  
            	protocol , 
            	format , targetAddress , targetURI , 
            	senderAddress , senderURI );            	
            transport.requestReceived ( msg );
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT participant port: error receiving prepare " , e );
        }

    }

}
