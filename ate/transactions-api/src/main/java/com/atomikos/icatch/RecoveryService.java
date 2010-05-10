package com.atomikos.icatch;

/**
 * 
 * 
 * 
 * 
 *
 * A handle to the TM that resources can use to recover.
 */

public interface RecoveryService
{
	/**
	 * Ask the TM to recover.
	 * Resources should call this method whenever 
	 * recovery is needed. The earliest time at which
	 * resources can call this method is when
	 * <b>setRecoveryService</b> is called on the resource.
	 * Note: resources that are registered <b>before</b> the
	 * TM starts up will not need to call this method.
	 * On the other hand, resources that are registered
	 * when the transaction service is already running should use
	 * this method to trigger recovery. It is up to the implementation
	 * of the resource to determine whether this is desirable.
	 *
	 */
	
	public void recover();
	
	/**
	 * Get the transaction service's unique name.
	 * Resources can use this name to determine what resource 
	 * transactions need to be considered for recovery by this
	 * transaction service.
	 * 
	 * @return String The name of the TM.
	 */
	
	public String getName();
}
