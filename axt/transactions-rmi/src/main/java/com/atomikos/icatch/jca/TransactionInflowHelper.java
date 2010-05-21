package com.atomikos.icatch.jca;


import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.system.Configuration;



/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A helper class to handle JCA transaction inflow.
 * An instance of this class can be constructed at
 * any time; it will delegate to the underlying 
 * transaction service to process incoming JCA 
 * transactions. <b>Note: this class will only work
 * if transactions across multiple VMs are supported
 * by your license!</b>
 * 
 * Use this class to implement the JCA transaction inflow
 * for your application server. <br>
 * <b>This class is a low-level
 * integration class, intended to facilitate integration in JCA-compliant J2EE servers. 
 * In particular, J2EE vendors can use this class in their implementation of the
 * JCA 1.5 WorkManager. In addition, this class provides the necessary XATerminator functionality.
 * This class is unlikely to be relevant to normal users of the transaction service.</b>
 * 
 */

public class TransactionInflowHelper
{
	private ImportingTransactionManager itm;
	private CompositeTransactionManager ctm;
	private boolean autoStart;
	
	
	public TransactionInflowHelper()
	{
		
		autoStart = true;
	}
	
	private void checkStartup()
	{
		itm = Configuration.getImportingTransactionManager();
	    ctm = Configuration.getCompositeTransactionManager();
		if ( ctm == null ) {
			if ( ! getAutoStartup() ) {
				throw new IllegalStateException ( 
				"Please start the transaction service first!" );			
			}
			else {
				//auto startup
				UserTransactionManager utm = new UserTransactionManager();
				
                try
                {
                    utm.getStatus();
                }
                catch (SystemException e)
                {
                    throw new RuntimeException ( "Failed to start transaction service.");
                }
                
				
				itm = Configuration.getImportingTransactionManager();
				ctm = Configuration.getCompositeTransactionManager();
				
			}
			
			
		}

		
	}
	
	/**
	 * Set the autostartup property. Default is true.
	 * 
	 * @param value If true, then the transaction service
	 * will start up if not yet running. Otherwise,
	 * the transaction service needs to be started
	 * before this class will work.
	 */
	public void setAutoStartup ( boolean value )
	{
		this.autoStart = value;
	}
	
	/**
	 * Check the auto startup property.
	 * @return
	 */
	public boolean getAutoStartup ()
	{
		return this.autoStart;
	}
	
	/**
	 * Import the transaction with the given Xid.
	 * Calling this method will effectively import the transaction into
	 * the transaction service: a corresponding transaction will
	 * be created, and its termination will be controlled by the
	 * XATerminator methods that use the same Xid value.
	 * 
	 * @param xid The Xid value for which a transaction should be imported (created).
	 * @param timeout The timeout in milliseconds before active transactions
	 * are automatically rolled back.
	 * @param commitOnHeuristic If true, heuristic timeouts will cause heuristic commit.
	 * Otherwise, heuristic timeouts will cause heuristic rollback.
	 * @return InboundTransaction A token for the resulting transaction. 
	 * 
	 * <br>
	 * <b>Note: the underlying transaction will NOT yet be associated with the
	 * calling thread. It is up to the caller to do this by calling resume() on the
	 * token, in the appropriate thread. Also, you need to call the end method on the token before the XATerminator
	 * functionality becomes available for the underlying transaction.</b>
	 */
	
	public InboundTransaction importTransactionWithXid ( Xid xid , long timeout , boolean commitOnHeuristic )
	{
		InboundTransaction ret = null;
		checkStartup();
		
		XidTransaction root = new XidTransaction ( xid );
		XidPropagation propagation = new XidPropagation ( root , timeout );
		itm.importTransaction ( propagation , false , commitOnHeuristic );
		CompositeTransaction tx = ctm.suspend();
		ret = new InboundTransaction ( xid , tx );
		return ret;
	}
	

	/**
	 * Retrieve the XATerminator implementation.
	 * @return The terminator for the imported transactions.
	 * This instance can be used to terminate XA transactions.
	 */

	public XATerminatorImp getXATerminator()
	{
		checkStartup();
		return XATerminatorImp.getInstance();
	}
	

}
