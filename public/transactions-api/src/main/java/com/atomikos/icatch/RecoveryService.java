/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import com.atomikos.recovery.RecoveryLog;


/**
 * A handle to the TM that resources can use to recover.
 */

public interface RecoveryService
{
	
	/**
	 * @return String The unique name of the TM. Resources can use this name to determine what resource 
	 * transactions need to be considered for recovery by this
	 * transaction service.
	 */
	
	 String getName();
	
	
	RecoveryLog getRecoveryLog();
	
	/**
	 * Instructs the core to do a full recovery cycle.
	 * 
	 * @return False if no recovery was done, 
	 * for instance if this node is not responsible for recovery
	 * or if there was a concurrent shutdown.
	 */
	boolean performRecovery();
	
	/**
	 * Asks the core to do a full recovery cycle.
	 * 
	 * @param lax True to allow for lax optimisation
	 * so the actual overhead of recovery can be avoided in
	 * some cases. Depending on your deployment, lax mode 
	 * may be accurate (or not).
	 *  
	 * @return False if no recovery was done.
	 */
	boolean performRecovery(boolean lax);
}
