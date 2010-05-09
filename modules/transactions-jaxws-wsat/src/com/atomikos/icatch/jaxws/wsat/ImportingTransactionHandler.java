//$Id: ImportingTransactionHandler.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: ImportingTransactionHandler.java,v $
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
//Revision 1.1  2005/10/21 08:16:50  guy
//Added handlers and import/export classes for WSAT.
//
package com.atomikos.icatch.jaxws.wsat;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxws.GenericImportingTransactionHandler;
import com.atomikos.icatch.jaxws.SOAPImportingTransactionManager;
import com.atomikos.icatch.jaxb.wsc.v200410.Utils;

/**
 * 
 * Copyright &copy; 2005-2007, Atomikos. All rights reserved.
 * 
 * A Message Handler for a web service endpoint.
 * <p>
 * Add this handler to the service's incoming handler chain
 * if you want to extract the transaction context from 
 * incoming SOAP requests. The request needs to
 * contain the transaction propagation in WS-AtomicTransaction
 * format.
 * 
 * <b>
 * IMPORTANT NOTE: this handler only works on platforms
 * where the message handlers are executed in the SAME
 * thread as the service itself.
 * </b>
 *
 * 
 */
public class ImportingTransactionHandler
    extends GenericImportingTransactionHandler
{
	private Set<QName> headerNames;




    private SOAPImportingTransactionManagerImp itm = null;
	
	

 
    public ImportingTransactionHandler()
    {
        super();
        
    }
    
   

    /**
     * @see com.atomikos.icatch.jaxrpc.GenericImportingTransactionHandler#getSOAPImportingTransactionManager()
     */
    protected SOAPImportingTransactionManager getSOAPImportingTransactionManager()
    {
        if ( itm == null ) {
        	itm = new SOAPImportingTransactionManagerImp ( getNewTransactionTimeout() );
        }
        return itm;
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



	@Override
	protected boolean getActiveRecovery() 
	{
		return false;
	}

 

}
