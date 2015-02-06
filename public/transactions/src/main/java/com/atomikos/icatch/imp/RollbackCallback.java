package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.SysException;

/**
 * Callback interface for logic inside the rollback template.
 *
 */

interface RollbackCallback {

	public HeuristicMessage[] doRollback() throws HeurCommitException,
    HeurMixedException, SysException, HeurHazardException,
    java.lang.IllegalStateException;
	
}
