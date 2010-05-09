


package com.atomikos.icatch.jaxb.wsat.v200410;

import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxb.wsc.v200410.AbstractBinding;
import com.atomikos.icatch.msg.ErrorMessage;
import com.atomikos.icatch.msg.ErrorMessageImp;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * Fault port implementation
 * 
 */

@javax.jws.WebService(name = "FaultPortType", serviceName = "FaultService",
                      portName = "FaultPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wsat.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsat.v200410.FaultPortType")
                      
public class FaultPortTypeImpl extends AbstractBinding implements FaultPortType {

    
    public void faultOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Fault req
    )
    { 
    		logMessageIfDebug();
		HttpTransport transport = WsatHttpTransport.getSingleton();
		if ( transport == null ) throw new IllegalStateException ( "Service not initialized" );
		int protocol = transport.getCommitProtocol();
		int format = transport.getFormat();       
		String targetURI = getTargetURI();
		String senderURI = getSenderURI();
		Object targetAddress = getTargetAddress();
		Object senderAddress = getSenderAddress();		
		int errorCode = ErrorMessage.UNKNOWN_ERROR;
		QName faultCode = req.getFaultcode();
		String code = faultCode.getLocalPart();
		if ( code != null ) {
			if ( code.endsWith ( "InconsistentInternalState" ))
				errorCode = ErrorMessage.HEUR_MIXED_ERROR;
			//IN ALL OTHER ERROR CASES: KEEP UNKNOWN
			Configuration.logWarning ( "WSAT fault port: received fault: " + faultCode );
		}
		if ( targetAddress != null ) {
			ErrorMessageImp msg = new ErrorMessageImp (protocol , format , targetAddress , targetURI , 
				senderAddress , senderURI , errorCode
			); 	    
			transport.replyReceived ( msg );    
		}
			    
    }

}
