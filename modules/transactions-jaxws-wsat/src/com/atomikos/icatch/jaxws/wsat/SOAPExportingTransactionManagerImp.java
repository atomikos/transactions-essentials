//$Id: SOAPExportingTransactionManagerImp.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: SOAPExportingTransactionManagerImp.java,v $
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
//Revision 1.2  2006/03/21 13:23:53  guy
//Introduced active recovery and CompTx properties as meta-tags.
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
