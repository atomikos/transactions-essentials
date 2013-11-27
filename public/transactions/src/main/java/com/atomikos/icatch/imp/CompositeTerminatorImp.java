/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.Dictionary;

import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TransactionService;

/**
 * A terminator implementation.
 */

class CompositeTerminatorImp implements CompositeTerminator
{

    protected CoordinatorImp coordinator_ = null;

    protected CompositeTransactionImp transaction_ = null;

    protected TransactionService ts_ = null;

    /**
     * Constructor.
     */

    CompositeTerminatorImp ( TransactionService ts ,
            CompositeTransactionImp transaction , CoordinatorImp coordinator )
    {
        ts_ = ts;
        coordinator_ = coordinator;
        transaction_ = transaction;

    }


    /**
     * @see CompositeTerminator
     */

    public void commit () throws HeurRollbackException, HeurMixedException,
            HeurHazardException, SysException, java.lang.SecurityException,
            RollbackException
    {

        transaction_.doCommit ();
        setSiblingInfoForIncoming1pcRequestFromRemoteClient();
        
        if ( transaction_.isRoot () ) {
            try {
                coordinator_.terminate ( true );
            }

            catch ( RollbackException rb ) {
                throw rb;
            } catch ( HeurHazardException hh ) {
                throw hh;
            } catch ( HeurRollbackException hr ) {
                throw hr;
            } catch ( HeurMixedException hm ) {
                throw hm;
            } catch ( SysException se ) {
                throw se;
            } catch ( Exception e ) {
                throw new SysException (
                        "Unexpected error: " + e.getMessage (), e );
            }
        }

    }


	private void setSiblingInfoForIncoming1pcRequestFromRemoteClient() {
		TransactionControl control = transaction_.getTransactionControl();
		Dictionary cascadelist = control.getExtent ().getRemoteParticipants ();
        coordinator_.setGlobalSiblingCount ( coordinator_
                .getLocalSiblingCount () );
        coordinator_.setCascadeList ( cascadelist );
	}



    /**
     * @see CompositeTerminator
     */

    public void rollback () throws IllegalStateException, SysException
    {
        transaction_.doRollback ();

        if ( transaction_.isRoot () )
            try {
                coordinator_.terminate ( false );
            } catch ( Exception e ) {
                throw new SysException ( "Unexpected error in rollback: " + e.getMessage (), e );
            }
    }

}
