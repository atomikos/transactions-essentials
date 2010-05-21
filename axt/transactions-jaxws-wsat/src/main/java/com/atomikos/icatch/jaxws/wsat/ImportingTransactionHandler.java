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
