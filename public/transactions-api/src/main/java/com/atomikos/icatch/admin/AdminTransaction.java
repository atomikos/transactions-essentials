/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * An administration interface for a transaction. Allows inspection of heuristic
 * info, as well as forced two-phase commit methods.
 */

 public interface AdminTransaction {

	/**
	 * Gets the transaction identifier.
	 *
	 * @return String The unique id.
	 */

	 String getTid();

	/**
	 * Gets the transaction's state.
	 *
	 * @return int The state, one of the predefined states. NOTE: the state is
	 *         an int rather than the generic Object, because instances need to
	 *         be Serializable.
	 */

	 TxState getState();

	/**
	 * Tests if the transaction's 2PC outcome was commit. Needed especially for
	 * the heuristic states, if the desired outcome (instead of the actual
	 * state) needs to be retrieved. For instance, if the state is
	 * STATE_HEUR_HAZARD then extra information is needed for determining if the
	 * desired outcome was commit or rollback. This method helps here.
	 *
	 *
	 * @return True if commit was decided (either heuristically or by the super
	 *         coordinator).
	 */

	 boolean wasCommitted();

	/**
	 * Forces commit of the transaction.
	 *
	 * @exception HeurRollbackException
	 *                If rolled back in the meantime.
	 *
	 * @exception HeurMixedException
	 *                If part of it was rolled back.
	 * @exception HeurHazardException
	 *                On possible conflicts.
	 * @exception SysException
	 */

	 void forceCommit() throws HeurRollbackException,
			HeurHazardException, HeurMixedException, SysException;

	/**
	 * Forces rollback of the transaction.
	 *
	 * @exception HeurCommitException
	 *                If heuristically committed in the meantime.
	 *
	 * @exception HeurHazardException
	 *                If the state is not certain.
	 *
	 * @exception HeurMixedException
	 *                If partially rolled back.
	 *
	 * @exception SysException
	 */

	 void forceRollback() throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException;

	/**
	 * Forces the system to forget about the transaction.
	 */

	 void forceForget();

	/**
	 * Retrieves the descriptive details for each participant involved in this
	 * transaction.
	 */

	 String[] getParticipantDetails();

	 /**
	  * 
	  * @return True if this transaction has expired past its timeout.
	  */
	  boolean hasExpired();

}
