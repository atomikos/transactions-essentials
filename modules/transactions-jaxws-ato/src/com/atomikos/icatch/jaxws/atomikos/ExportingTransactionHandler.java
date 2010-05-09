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
//Revision 1.12  2005/11/01 14:10:58  guy
//Updated javadoc.
//
//Revision 1.11  2005/10/21 08:16:33  guy
//Adapted to new abstract class design in propagation package.
//
//Revision 1.10  2005/09/02 08:17:11  guy
//Updated javadoc (mustUnderstand!)
//
//Revision 1.9  2005/08/30 12:51:46  guy
//Added logging.
//
//Revision 1.8  2005/08/26 13:30:08  guy
//Added check if TM is running.
//
//Revision 1.7  2005/08/19 16:04:01  guy
//Debugged.
//
//Revision 1.6  2005/08/19 13:48:50  guy
//Debugged.
//
//Revision 1.5  2005/08/19 07:45:09  guy
//Improved code.
//
//Revision 1.4  2005/08/10 09:04:45  guy
//Added interfaces.
//
//Revision 1.3  2005/08/08 11:23:41  guy
//Completed implementation.
//
//Revision 1.2  2005/08/05 15:03:59  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.2.2  2005/08/04 13:25:45  guy
//Added Soap import/export utility classes and handlers.
//
//Revision 1.1.2.1  2005/08/01 10:06:03  guy
//Added skeleton for import/export handlers.
//
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
