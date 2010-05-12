//$Id: ExportingTransactionHandler.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: ExportingTransactionHandler.java,v $
//Revision 1.1.1.1  2006/10/02 15:20:58  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/11/01 14:11:02  guy
//Updated javadoc.
//
//Revision 1.2  2005/10/24 09:50:12  guy
//Completed.
//
//Revision 1.1  2005/10/21 08:16:49  guy
//Added handlers and import/export classes for WSAT.
//
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
