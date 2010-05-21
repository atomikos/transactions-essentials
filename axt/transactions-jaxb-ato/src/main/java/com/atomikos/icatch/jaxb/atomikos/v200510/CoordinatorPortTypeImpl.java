
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.atomikos.icatch.jaxb.atomikos.v200510;

import com.atomikos.icatch.msg.ErrorMessageImp;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.PreparedMessageImp;
import com.atomikos.icatch.msg.ReplayMessageImp;
import com.atomikos.icatch.msg.StateMessageImp;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * The Coordinator implementation.
 * 
 */

@javax.jws.WebService(name = "CoordinatorPortType", serviceName = "TransactionService",
                      portName = "CoordinatorPort",
                      targetNamespace = "http://www.atomikos.com/schemas/2005/10/transactions", 
                     // wsdlLocation = "file:./resources/wsdl/atomikos/v200510/atomikos.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortType")
                      
public class CoordinatorPortTypeImpl implements CoordinatorPortType {

    

    /* (non-Javadoc)
     * @see com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortType#participantErrorNotification(com.atomikos.icatch.jaxb.atomikos.v200510.ErrorMessageType  errorContent )*
     */
    public void participantErrorNotification(
        com.atomikos.icatch.jaxb.atomikos.v200510.ErrorMessageType req
    )
    { 
       Configuration.logInfo("Executing operation participantErrorNotification");
       HttpTransport transport = AtomikosHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();    	
		String senderUri = req.getSender().getReference().toString();
		String targetUri = req.getTarget().getReference().toString();
		String senderAddress = req.getSender().getEndPoint().toString();
		String targetAddress = req.getTarget().getEndPoint().toString();   
		ErrorMessageImp msg = new ErrorMessageImp ( 
			protocol , format , targetAddress , targetUri ,
			senderAddress, senderUri , req.getErrorCode().intValue()
		);   	
		transport.replyReceived ( msg );

    }

    /* (non-Javadoc)
     * @see com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortType#prepared(com.atomikos.icatch.jaxb.atomikos.v200510.PreparedMessageType  prepareResponseContent )*
     */
    public void prepared(
        com.atomikos.icatch.jaxb.atomikos.v200510.PreparedMessageType req
    )
    { 
    	Configuration.logInfo("Executing operation prepared");
		HttpTransport transport = AtomikosHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();    	
		String senderUri = req.getSender().getReference().toString();
		String targetUri = req.getTarget().getReference().toString();
		String senderAddress = req.getSender().getEndPoint().toString();
		String targetAddress = req.getTarget().getEndPoint().toString();   
		PreparedMessageImp msg = new PreparedMessageImp (
			protocol , format , targetAddress , targetUri , 
			senderAddress , senderUri , req.isReadOnly() , false
		); 	    
		transport.replyReceived ( msg );

    }

    /* (non-Javadoc)
     * @see com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortType#participantStateNotification(com.atomikos.icatch.jaxb.atomikos.v200510.StateMessageType  stateContent )*
     */
    public void participantStateNotification(
        com.atomikos.icatch.jaxb.atomikos.v200510.StateMessageType req
    )
    { 
       Configuration.logInfo("Executing operation participantStateNotification");
       HttpTransport transport = AtomikosHttpTransport.getSingleton();
       if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
       int protocol = transport.getCommitProtocol();
       int format = transport.getFormat();    	
       String senderUri = req.getSender().getReference().toString();
       String targetUri = req.getTarget().getReference().toString();
       String senderAddress = req.getSender().getEndPoint().toString();
       String targetAddress = req.getTarget().getEndPoint().toString();      	    
       StateMessageImp msg = new StateMessageImp (
    		   protocol , format , targetAddress , targetUri , 
    		   senderAddress , senderUri , req.isCommitted()
       );	
       transport.replyReceived ( msg );

    }

    /* (non-Javadoc)
     * @see com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortType#replayCompletion(com.atomikos.icatch.jaxb.atomikos.v200510.TransactionMessageType  replayContent )*
     */
    public void replayCompletion(
        com.atomikos.icatch.jaxb.atomikos.v200510.TransactionMessageType req
    )
    { 
    	Configuration.logInfo("Executing operation replayCompletion");
    	HttpTransport transport = AtomikosHttpTransport.getSingleton();
    	if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
    	int protocol = transport.getCommitProtocol();
    	int format = transport.getFormat();    	
    	String senderUri = req.getSender().getReference().toString();
    	String targetUri = req.getTarget().getReference().toString();
    	String senderAddress = req.getSender().getEndPoint().toString();
    	String targetAddress = req.getTarget().getEndPoint().toString();       
    	ReplayMessageImp msg = new ReplayMessageImp (
    			protocol , format , targetAddress , targetUri ,
    			senderAddress , senderUri 
    	);
    	transport.requestReceived ( msg );

    }

}
