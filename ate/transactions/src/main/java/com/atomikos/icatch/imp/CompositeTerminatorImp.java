package com.atomikos.icatch.imp;

import java.util.Dictionary;
import java.util.Stack;

import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TransactionService;

/**
 * 
 * 
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
    		Stack errors = new Stack ();
        TransactionControl control = transaction_.getTransactionControl ();

        transaction_.doCommit ();

        // SET SIBLING INFO: NEEDED FOR
        // INCOMING 1PC REQUEST FROM
        // REMOTE CLIENT
        Dictionary cascadelist = control.getExtent ().getRemoteParticipants ();
        coordinator_.setGlobalSiblingCount ( coordinator_
                .getLocalSiblingCount () );
        coordinator_.setCascadeList ( cascadelist );

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
                errors.push ( e );
                throw new SysException (
                        "Unexpected error: " + e.getMessage (), errors );
            }
        }

    }



    /**
     * @see CompositeTerminator
     */

    public void rollback () throws IllegalStateException, SysException
    {
        Stack errors = new Stack ();

        transaction_.doRollback ();

        if ( transaction_.isRoot () )
            try {
                coordinator_.terminate ( false );
            } catch ( Exception e ) {
                errors.push ( e );
                throw new SysException ( "Unexpected error in rollback: "
                        + e.getMessage (), errors );
            }
    }

}
