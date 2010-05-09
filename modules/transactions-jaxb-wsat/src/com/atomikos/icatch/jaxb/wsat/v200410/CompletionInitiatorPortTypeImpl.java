


package com.atomikos.icatch.jaxb.wsat.v200410;


/**
 * Not yet implemented!
 * 
 */

@javax.jws.WebService(name = "CompletionInitiatorPortType", serviceName = "CompletionInitiatorService",
                      portName = "CompletionInitiatorPort",
                      targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", 
                      wsdlLocation = "file:./resources/wsdl/wst/v200410/wsat.wsdl" ,
		      endpointInterface = "com.atomikos.icatch.jaxb.wsat.v200410.CompletionInitiatorPortType")
                      
public class CompletionInitiatorPortTypeImpl implements CompletionInitiatorPortType {

  
    public void committedOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Notification parameters
    )
    { 
       
    }

   
    public void abortedOperation(
        com.atomikos.icatch.jaxb.wsat.v200410.Notification parameters
    )
    { 
    }

}
