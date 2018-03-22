/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

/**
 * Callback interface for logic inside the commit template.
 */

interface CommitCallback {
	
	public void doCommit() throws HeurRollbackException, HeurMixedException,
    HeurHazardException, java.lang.IllegalStateException,
    RollbackException, SysException;
	
}
