//$Id: InboundTransaction.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//$Log: InboundTransaction.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:16  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:44  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:31  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/05/09 08:07:03  guy
//Finished JCA package implementation.
//
//Revision 1.1  2005/05/06 11:41:50  guy
//Completed package for JCA inbound transaction handling.
//
package com.atomikos.icatch.jca;

import javax.transaction.xa.Xid;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * A transaction token for an inbound JCA transaction.
 * This class represents an underlying transaction 
 * and simplifies the JCA-related handling of thread-association
 * and termination issues. An instance is 
 * returned when importing via the TransactionInflowHelper.
 * <p>
 * Note: if full access to the underlying transaction is needed, then
 * this can be done via the TransactionManager interfaces
 * after resume() has been called.
 * 
 */

public class InboundTransaction
{
	private Xid xid;
	private CompositeTransaction tx;
	private CompositeTransactionManager ctm;
	private ImportingTransactionManager itm;
	private XATerminatorImp terminator;
	
	InboundTransaction ( Xid xid , CompositeTransaction tx )
	{
		this.xid = xid;
		this.tx = tx;
		this.ctm = Configuration.getCompositeTransactionManager();
		this.itm = Configuration.getImportingTransactionManager();
		this.terminator = XATerminatorImp.getInstance();
	}
	
	/**
	 * Resumes the transaction in the calling thread.
	 * Calling this method will associate the underlying
	 * transaction with the calling thread.
	 * <b>This method must be called before any work can
	 * be done in the context of this transaction.</b>
	 *
	 */
	
	public void resume()
	{
		ctm.resume ( tx );	
	}
	
	 /**
	  * Suspends the transaction from the calling thread.
	  * Calling this method will dissociate the 
	  * transaction from the calling thread.
	  * This method can be called to continue the 
	  * transaction in another thread (with a later
	  * resume).
	  */
	 
	public void suspend()
	{
		if ( ctm.getCompositeTransaction() == null )
			throw new IllegalStateException ( "Resume must be called first!");
		
		tx = ctm.suspend();
	}
	
	 /**
	  * Ends the underlying transaction. Subsequent
	  * calls to resume() and suspend() are not allowed.
	  * <br>
	  * <b>This method must be called in order to 
	  * make the XATerminator functionality work.</b>
	  * 
	  * 
	  * @param success If true then the transaction
	  * is maintained until it times out or terminates
	  * via the XATerminator methods, whichever comes first.
	  * If false then the transaction is rolled back 
	  * immediately. In that case, the XATerminator
	  * functionality is no longer required nor relevant.
	  * @exception RollbackException If the transaction
	  * has already been rolled back due to timeout.
	  */
	 
	public void end ( boolean success ) throws RollbackException
	{
		if ( ctm.getCompositeTransaction() == null )
			resume();
		
		Extent extent = itm.terminated ( success );
		Participant participant = 
			( Participant ) extent.getParticipants().peek();
		
		if ( success ) 
			terminator.registerParticipant ( xid , participant );
	}
	
}
