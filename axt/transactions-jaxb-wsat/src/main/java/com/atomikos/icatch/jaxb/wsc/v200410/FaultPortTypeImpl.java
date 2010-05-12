
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.atomikos.icatch.jaxb.wsc.v200410;

import com.atomikos.icatch.system.Configuration;



/**
 * Fault port implementation.
 * 
 */

@javax.jws.WebService(name = "FaultPortType", serviceName = "FaultService",
                      portName = "FaultPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", 
                      //wsdlLocation = "file:./resources/wsdl/wst/v200410/wscoor.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsc.v200410.FaultPortType")
                      
public class FaultPortTypeImpl extends AbstractBinding implements FaultPortType 
{
   

  
    public void faultOperation(
        com.atomikos.icatch.jaxb.wsc.v200410.Fault content
    )
    { 
    	logMessageIfDebug();
    	String message = content.getFaultstring();
    	String faultTarget = getTargetURI();
    	Configuration.logWarning ( "WS-C ERROR for local target : " + faultTarget + " with reason: " + message );
    	//this is an error during registration -> ignore, let local tx timeout
      
    }

}
