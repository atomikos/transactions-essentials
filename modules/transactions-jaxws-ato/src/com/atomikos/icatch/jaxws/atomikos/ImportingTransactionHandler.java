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
//Revision 1.14  2005/11/19 16:56:42  guy
//Improved logging and added namespace for propagation/extent nested elements
//
//Revision 1.13  2005/11/01 14:10:58  guy
//Updated javadoc.
//
//Revision 1.12  2005/10/24 09:50:03  guy
//Added tid of imported tx to insertExtent parameters.
//
//Revision 1.11  2005/08/30 07:20:58  guy
//Refactored to improve reuse across protocols.
//
//Revision 1.10  2005/08/27 11:28:51  guy
//Modified to use new Sender paradigm.
//
//Revision 1.9  2005/08/26 13:30:08  guy
//Added check if TM is running.
//
//Revision 1.8  2005/08/19 16:04:01  guy
//Debugged.
//
//Revision 1.7  2005/08/19 13:48:50  guy
//Debugged.
//
//Revision 1.6  2005/08/19 07:45:09  guy
//Improved code.
//
//Revision 1.5  2005/08/11 09:24:33  guy
//Adapted to new import preferences.
//
//Revision 1.4  2005/08/10 09:04:45  guy
//Added interfaces.
//
//Revision 1.3  2005/08/08 11:23:41  guy
//Completed implementation.
//
//Revision 1.2  2005/08/05 15:04:00  guy
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

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxws.GenericImportingTransactionHandler;
import com.atomikos.icatch.jaxws.SOAPImportingTransactionManager;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005-2009, Atomikos. All rights reserved.
 * 
 * A Message Handler for a web service endpoint.
 * <p>
 * Add this handler to the service's incoming handler chain
 * if you want to extract the transaction context from 
 * incoming SOAP requests. The request needs to
 * contain the transaction propagation in Atomikos
 * format (as added by an {@link ExportingTransactionHandler}
 * on the client side, for instance).
 * <p>
 * In addition to the init parameters of the superclass, this
 * handler also accepts an optional init parameter named
 * <b>JTA</b>. If set to 'true' then newly created transactions
 * will be JTA transactions. By default this is false.
 * <b>
 * IMPORTANT NOTE: this handler only works on platforms
 * where the message handlers are executed in the SAME
 * thread as the service itself.
 * </b>
 * 
 */
public class ImportingTransactionHandler 
extends GenericImportingTransactionHandler
{
	
	

	private Set<QName> headerNames = null;
	
	private SOAPImportingTransactionManagerImp itm =
		new com.atomikos.icatch.jaxws.atomikos.SOAPImportingTransactionManagerImp();
	

	/**
	 * Sets whether this handler should create new transactions in JTA-compatible mode or not (i.e., TCC). 
	 * This only affects use cases where the importPreference leads to the creation of a new transaction.
	 * 
	 * Default is false.
	 * 
	 * @param jtaCompatible
	 */
	
	@Resource
	public void setJtaCompatible ( boolean jtaCompatible )
	{
		Configuration.logInfo ( this + ": jtaCompatible = " + jtaCompatible );
		itm.setCreateJtaTransactions ( jtaCompatible );
	}
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
     * @see com.atomikos.icatch.jaxrpc.GenericImportingTransactionHandler#getSOAPImportingTransactionManager()
     */
    protected SOAPImportingTransactionManager getSOAPImportingTransactionManager()
    {
        return itm;
    }

	@Override
	public String toString() 
	{
		return "Atomikos ImportingTransactionHandler: ";
	}
	
	@Override
	protected boolean getActiveRecovery() {
		return !itm.useJta();
	}

}
