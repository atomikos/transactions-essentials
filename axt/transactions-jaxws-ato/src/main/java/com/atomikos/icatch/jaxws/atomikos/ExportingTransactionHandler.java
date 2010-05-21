package com.atomikos.icatch.jaxws.atomikos;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxws.GenericExportingTransactionHandler;
import com.atomikos.icatch.jaxws.SOAPExportingTransactionManager;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A Message Handler for a web service client. 
 * <p>
 * Add this 
 * handler to a SOAP client's handler chain if you want the client's 
 * transaction to be propagated along with the SOAP call.
 * The propagation headers added by this handler will be in 
 * Atomikos format, with <b>mustUnderstand</b> set to true.
 * <b>
 * IMPORTANT NOTE: this handler only works on platforms
 * where the message handlers are executed in the SAME
 * thread as the service itself.
 * </b>
 * 
 * 
 */
public class ExportingTransactionHandler 
extends GenericExportingTransactionHandler
{
	private Set<QName> headerNames = null;
	
	private SOAPExportingTransactionManagerImp etm =
		new SOAPExportingTransactionManagerImp();
	
	
	/**
     * @see javax.xml.rpc.handler.Handler#getHeaders()
     */
    public Set<QName> getHeaders()
    {
    		
      	if ( headerNames == null ) {
      		QName propagationHeader = 
      			new QName ( "http://www.atomikos.com/schemas/2005/10/transactions","Propagation","atomikos");
      		QName extentHeader =
      			new QName ( "http://www.atomikos.com/schemas/2005/10/transactions","Extent","atomikos");
      		headerNames = new HashSet<QName>();
      		headerNames.add ( propagationHeader );
      		headerNames.add ( extentHeader );
      	}
      	return headerNames;  
    }
    
   
   /**
    * Inserts the transaction propagation into the
    * message headers of the outgoing request.
    * The propagation added by this handler will
    * be in the Atomikos format.
    * @throws JAXRPCException If there is no 
    * transaction for the calling thread, or if the transaction was 
    * already rolled back.
    */ 
    
    /**
     * @see com.atomikos.icatch.jaxrpc.GenericExportingTransactionHandler#getExportingTransactionManager()
     */
    protected SOAPExportingTransactionManager getExportingTransactionManager()
    {
        return etm;
    }


}
