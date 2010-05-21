package com.atomikos.icatch.jaxws.wsat;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxws.GenericExportingTransactionHandler;
import com.atomikos.icatch.jaxws.SOAPExportingTransactionManager;
import com.atomikos.icatch.jaxb.wsc.v200410.Utils;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 *  * A Message Handler for a web service client. 
 * <p>
 * Add this 
 * handler to a SOAP client's handler chain if you want the client's 
 * transaction to be propagated along with the SOAP call.
 * The transaction context headers added by this handler will be in 
 * WS-AtomicTransaction format, with <b>mustUnderstand</b> set to true.
 * <b>
 * IMPORTANT NOTE: this handler only works on platforms
 * where the message handlers are executed in the SAME
 * thread as the service itself.
 * </b>
 * 
 *
 * 
 */
public class ExportingTransactionHandler
    extends GenericExportingTransactionHandler
{

	private Set<QName> headerNames = null;
	
	private SOAPExportingTransactionManagerImp etm = new SOAPExportingTransactionManagerImp();
	
    /**
     * @see com.atomikos.icatch.jaxrpc.GenericExportingTransactionHandler#getExportingTransactionManager()
     */
    protected SOAPExportingTransactionManager getExportingTransactionManager()
    {
		return etm;
    }

	/**
	* @see javax.xml.rpc.handler.Handler#getHeaders()
	*/
   public Set<QName> getHeaders()
   {

	   if ( headerNames == null ) {
		   QName propagationHeader = 
			   new QName ( Utils.WSC_NAMESPACE_URI, Utils.CONTEXT_HEADER_NAME);
			
		   headerNames = new HashSet<QName>();
		   headerNames.add ( propagationHeader );
	   }
	   return headerNames;  

   }


}
