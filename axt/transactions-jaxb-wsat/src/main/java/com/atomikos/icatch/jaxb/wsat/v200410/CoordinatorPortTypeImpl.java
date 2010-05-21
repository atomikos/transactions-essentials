

package com.atomikos.icatch.jaxb.wsat.v200410;

import com.atomikos.icatch.jaxb.wsa.v200408.IncomingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsc.v200410.AbstractBinding;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.PreparedMessageImp;
import com.atomikos.icatch.msg.ReplayMessageImp;
import com.atomikos.icatch.msg.StateMessageImp;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;


/**
 * Implementation of the coordinator.
 * 
 */

@javax.jws.WebService(name = "CoordinatorPortType", serviceName = "TransactionService",
                      portName = "CoordinatorPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wsat.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsat.v200410.CoordinatorPortType")
                      
public class CoordinatorPortTypeImpl extends AbstractBinding implements CoordinatorPortType {

  
    public void committedOperation(
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
            StateMessageImp msg = new StateMessageImp (
            	protocol , format , targetAddress , targetURI , 
            	senderAddress , senderURI , new Boolean ( true )
            ); 	    
            transport.replyReceived ( msg );    
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT coordinator port: error receiving committed " , e );
            e.printStackTrace();
        }  
    }

   
    public void preparedOperation(
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
            PreparedMessageImp msg = new PreparedMessageImp (
            	protocol , format , targetAddress , targetURI , 
            	senderAddress , senderURI , false , false
            ); 	    
            transport.replyReceived ( msg );   
        }
        catch (Exception e)
        {
            Configuration.logWarning ( "WSAT coordinator port: error receiving prepared " , e );
        } 

    }

    public void abortedOperation(
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
            StateMessageImp msg = new StateMessageImp (
            	protocol , format , targetAddress , targetURI , 
            	senderAddress , senderURI , new Boolean ( false )
            ); 	    
            transport.replyReceived ( msg );   
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT coordinator port: error receiving aborted " , e );
        }  

    }

  
    public void replayOperation(
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
            Object targetAddress = getTargetAddress();
            IncomingAddressingHeaders senderAddress = getSenderAddress();	
            OutgoingAddressingHeaders remoteParticipantAddress = senderAddress.createReplyAddress();
            String remoteURI = remoteParticipantAddress.getTargetURI();
            ReplayMessageImp msg = new ReplayMessageImp (
            	protocol , format , targetAddress , targetURI , 
            	remoteParticipantAddress , remoteURI 
            ); 	    
            transport.replyReceived ( msg );  
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT coordinator port: error receiving replay " , e );
        }   
    }

   
    public void readOnlyOperation(
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
            PreparedMessageImp msg = new PreparedMessageImp (
            	protocol , format , targetAddress , targetURI , 
            	senderAddress , senderURI , true , false
            ); 	    
            transport.replyReceived ( msg );  
        }
        catch (Exception e)
        {
			Configuration.logWarning ( "WSAT coordinator port: error receiving readOnly " , e );
        }    
 
    }

}
