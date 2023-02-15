/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
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

import com.atomikos.util.SerializableObjectFactory;

/**
 *
 *
 * A J2EE UserTransaction implementation. J2EE applications can use instances of
 * this class to delimit transactions. Note: J2EE applications should NOT use
 * the default UserTransactionImp in order to avoid that the transaction service
 * is started twice in different locations. Instances can be bound in JNDI (if
 * the application server allows this).
 */

public class J2eeUserTransaction implements UserTransaction, Serializable,
        Referenceable
{

	private static final long serialVersionUID = -7656447860674832182L;

	private transient TransactionManager txmgr_;

    public J2eeUserTransaction()
    {
    }

    /**
     * Referenceable mechanism requires later setup of txmgr_, otherwise binding
     * into JNDI already requires that TM is running.
     */

    private void checkSetup()
    {
        txmgr_ = TransactionManagerImp.getTransactionManager ();
        if (txmgr_ == null) throw new RuntimeException ( "Transaction Service Not Running?" );
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void begin() throws NotSupportedException, SystemException
    {
        checkSetup();
        txmgr_.begin();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void commit() throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.IllegalStateException,
            java.lang.SecurityException
    {
        checkSetup();
        txmgr_.commit();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void rollback() throws IllegalStateException, SystemException,
            SecurityException
    {
        checkSetup();
        txmgr_.rollback();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setRollbackOnly() throws IllegalStateException,
            SystemException
    {
        checkSetup ();
        txmgr_.setRollbackOnly ();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public int getStatus() throws SystemException
    {
        checkSetup();
        return txmgr_.getStatus();
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setTransactionTimeout(int seconds) throws SystemException
    {
        checkSetup();
        txmgr_.setTransactionTimeout(seconds);
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    public Reference getReference() throws NamingException
    {
        return SerializableObjectFactory.createReference(this);
    }
}
