/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.util.SerializableObjectFactory;

/**
 * Our UserTransaction implementation for J2SE transactions. This class is
 * special in that it automatically starts up and recover the transaction
 * service on first use. <b>Note: don't use this class in J2EE applications in
 * order to avoid starting different transaction engines in the same application
 * server! J2EE applications should use J2eeUserTransaction instead.</b>
 */

public class UserTransactionImp implements UserTransaction, Serializable,
        Referenceable
{
	private static final long serialVersionUID = -865418426269785202L;
	
	private transient TransactionManager txmgr_;

    /**
     * No-argument constructor.
     */

    public UserTransactionImp ()
    {
    }

    /**
     * Referenceable mechanism requires later setup of txmgr_, otherwise binding
     * into JNDI already requires that TM is running.
     */

    private void checkSetup ()
    {
        synchronized ( TransactionManagerImp.class ) {
            txmgr_ = TransactionManagerImp.getTransactionManager ();
            if ( txmgr_ == null ) {
                UserTransactionService uts = new UserTransactionServiceImp ();
                uts.init();
                txmgr_ = TransactionManagerImp.getTransactionManager ();
            }
        }
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void begin () throws NotSupportedException, SystemException
    {
        checkSetup ();
        txmgr_.begin ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void commit () throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.IllegalStateException,
            java.lang.SecurityException
    {
        checkSetup ();
        txmgr_.commit ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void rollback () throws IllegalStateException, SystemException,
            SecurityException
    {
        checkSetup ();
        txmgr_.rollback ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        checkSetup ();
        txmgr_.setRollbackOnly ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public int getStatus () throws SystemException
    {
        checkSetup ();
        return txmgr_.getStatus ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setTransactionTimeout ( int seconds ) throws SystemException
    {
        checkSetup ();
        txmgr_.setTransactionTimeout ( seconds );
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    public Reference getReference () throws NamingException
    {
        return SerializableObjectFactory.createReference ( this );
    }
}
