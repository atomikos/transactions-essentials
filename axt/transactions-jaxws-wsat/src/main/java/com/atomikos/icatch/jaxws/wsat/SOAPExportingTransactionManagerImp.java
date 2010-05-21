package com.atomikos.icatch.jaxws.wsat;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.atomikos.icatch.jaxb.wsc.v200410.Utils;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.jaxws.SOAPExportingTransactionManager;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 *  A helper class to export a transaction via SOAP.
 * The application can construct an instance of 
 * this class to add the WS-AtomicTransaction propagation information  
 * of the current thread's transaction to an
 * outgoing SOAP message.
 *
 * 
 */

public class SOAPExportingTransactionManagerImp
implements SOAPExportingTransactionManager
{

    /**
     * @see com.atomikos.icatch.jaxrpc.SOAPExportingTransactionManager#extractExtent(javax.xml.soap.SOAPMessage)
     */
    public void extractExtent(SOAPMessage msg) throws SOAPException, RollbackException
    {
        //nothing to do: registration already done by remote 
        
    }

    /**
     * @see com.atomikos.icatch.jaxrpc.SOAPExportingTransactionManager#insertPropagation(java.lang.String, javax.xml.soap.SOAPMessage)
     */
    public void insertPropagation ( String tid, SOAPMessage msg ) throws RollbackException, SOAPException
    {
		CompositeTransactionManager ctm =
					Configuration.getCompositeTransactionManager();
		CompositeTransaction tx =
					ctm.getCompositeTransaction ( tid );
		if ( tx == null )
			throw new RollbackException ( tid );
		String root = tx.getCompositeCoordinator().getCoordinatorId();
		WsatHttpTransport transport =  (WsatHttpTransport) WsatHttpTransport.getSingleton();
		Utils.insertPropagationHeader ( msg , root,
			tid ,  tx.getTimeout() ,transport.getRegistrationServiceURL() , Utils.WSAT_TYPE_URI
		);
        
    }

 
}
