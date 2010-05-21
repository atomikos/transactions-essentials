package com.atomikos.icatch.tcc;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.system.Configuration;

/**
 * Root TCC transactions must be terminated in
 * a separate thread, or there will be
 * re-entrant calls in the TCCService.
 * 
 * @author guy
 *
 */
class NonReentrantTerminationThread extends Thread 
implements SubTxAwareParticipant
{

	private CompositeTransaction ct;
	private boolean commit;
	private boolean done;
	
	public NonReentrantTerminationThread ( CompositeTransaction ct ,
			boolean commit )
	{
		this.ct = ct;
		ct.addSubTxAwareParticipant ( this );
		this.commit = commit;
		this.done = false;
	}
	
	private synchronized void setDone()
	{
		done = true;
		notifyAll();
	}
	
	/**
	 * Waits for max. timeout millis
	 * if not done. 
	 *
	 */
	synchronized void waitUntilDone()
	{

		long timeout = ct.getTimeout();
		if ( ! done ) {
			try {
				wait ( timeout );
			} catch (InterruptedException e) {
				Configuration.logDebug ( "TCC: termination interrupted: " , e );
			}
		}
		//if not done here: let root commit fail with 'active transactions'error
	}
	
	public void committed ( CompositeTransaction ct ) 
	{
		if ( ct == this.ct ) setDone();
		
	}

	public void rolledback ( CompositeTransaction ct ) 
	{
		if ( ct == this.ct ) setDone();
		
	}
	
	public void run()
	{
		try {
			if ( commit ) ct.commit();
			else ct.rollback();
		}
		catch ( Exception e ) {
			Configuration.logDebug ( "TCC: error in termination: " , e );
		}
		
	}

}
