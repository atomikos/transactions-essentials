/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.SysException;

/**
 * Callback interface for logic inside the rollback template.
 *
 */

interface RollbackCallback {

	public void doRollback() throws HeurCommitException,
    HeurMixedException, SysException, HeurHazardException,
    java.lang.IllegalStateException;
	
}
