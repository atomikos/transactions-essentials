/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
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
}
