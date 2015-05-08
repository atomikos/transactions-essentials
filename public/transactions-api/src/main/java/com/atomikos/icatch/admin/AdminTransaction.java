/**
 * Copyright (C) 2000-2015 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.admin;

import java.io.Serializable;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;

 /**
  * An administration interface for a transaction.
  * Allows inspection of heuristic info,
  * as well as forced two-phase commit methods.
  */

public interface AdminTransaction extends Serializable
{
	 public static final int STATE_ACTIVE = -3;

	 public static final int STATE_PREPARING = -2;

     public static final int STATE_UNKNOWN = -1;

     public static final int STATE_PREPARED = 0;

     public static final int STATE_HEUR_COMMITTED = 1;

     public static final int STATE_HEUR_ABORTED = 2;

     public static final int STATE_HEUR_HAZARD = 3;

     public static final int STATE_HEUR_MIXED = 4;

     public static final int STATE_ABORTING = 5;

     public static final int STATE_COMMITTING = 6;

     public static final int STATE_TERMINATED = 7;



       /**
        * Gets the transaction identifier.
        *
        * @return String The unique id.
        */

      public String getTid();

       /**
        *Gets the transaction's state.
        *
        * @return int The state, one of the predefined states.
        * NOTE: the state is an int rather than the generic Object,
        * because instances need to be Serializable.
        */

      public int getState();

       /**
        * Tests if the transaction's 2PC outcome was commit.
        * Needed especially for the heuristic states, if the
        * desired outcome (instead of the actual state) needs
        * to be retrieved. For instance, if the state is STATE_HEUR_HAZARD
        * then extra information is needed for determining if the desired
        * outcome was commit or rollback. This method helps here.
        *
        *
        * @return True if commit was decided (either heuristically
        * or by the super coordinator).
        */

      public boolean wasCommitted();


      /**
       * Forces commit of the transaction.
       *
       * @exception HeurRollbackException If rolled back in the meantime.
       *
       * @exception HeurMixedException If part of it was rolled back.
       * @exception HeurHazardException On possible conflicts.
       * @exception SysException
       */


      public void forceCommit()
      throws HeurRollbackException,
             HeurHazardException,
             HeurMixedException,
             SysException;


      /**
       * Forces rollback of the transaction.
       *
       * @exception HeurCommitException If heuristically committed in
       * the meantime.
       *
       * @exception HeurHazardException If the state is not certain.
       *
       * @exception  HeurMixedException If partially rolled back.
       *
       * @exception SysException
       */


      public void forceRollback()
      throws HeurCommitException,
             HeurMixedException,
             HeurHazardException,
             SysException;

      /**
       *Forces the system to forget about the transaction.
       */

      public void forceForget();
      
      /**
       * Retrieves the descriptive details for each participant involved in this transaction.
       */

      public String[] getParticipantDetails();
      
}
