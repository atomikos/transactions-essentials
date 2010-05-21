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
