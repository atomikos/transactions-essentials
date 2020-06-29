/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.util.SerializableObjectFactory;

/**
 * An implementation of TransactionManager that should be used by J2EE
 * applications. Instances can be bound in JNDI if the application server allows
 * this.
 */
public class J2eeTransactionManager
implements TransactionManager, Serializable, Referenceable, UserTransaction
{

	private static final long serialVersionUID = 8584376600562353607L;

	private transient TransactionManagerImp tm;

    private void checkSetup() throws SystemException
    {
        tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager ();
        if (tm == null) {
            throw new RuntimeException("Transaction Service not running?");
        }
    }

    /**
     * @see javax.transaction.TransactionManager#begin()
     */
    public void begin() throws NotSupportedException, SystemException
    {
        checkSetup();
        tm.begin();

    }

    /**
     * @see javax.transaction.TransactionManager#commit()
     */
    public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException
    {
        checkSetup();
        tm.commit();

    }

    /**
     * @see javax.transaction.TransactionManager#getStatus()
     */
    public int getStatus() throws SystemException
    {
        checkSetup();
        return tm.getStatus();
    }

    /**
     * @see javax.transaction.TransactionManager#getTransaction()
     */
    public Transaction getTransaction() throws SystemException
    {
        checkSetup();
        return tm.getTransaction();
    }

    /**
     * @see javax.transaction.TransactionManager#resume(javax.transaction.Transaction)
     */
    public void resume(Transaction tx) throws InvalidTransactionException,
            IllegalStateException, SystemException
    {
        checkSetup();
        tm.resume(tx);

    }

    /**
     * @see javax.transaction.TransactionManager#rollback()
     */
    public void rollback() throws IllegalStateException, SecurityException,
            SystemException
    {
        checkSetup();
        tm.rollback();

    }

    /**
     * @see javax.transaction.TransactionManager#setRollbackOnly()
     */
    public void setRollbackOnly() throws IllegalStateException,
            SystemException
    {
        checkSetup();
        tm.setRollbackOnly();

    }

    /**
     * @see javax.transaction.TransactionManager#setTransactionTimeout(int)
     */
    public void setTransactionTimeout(int secs) throws SystemException
    {
        checkSetup();
        tm.setTransactionTimeout(secs);

    }

    /**
     * @see javax.transaction.TransactionManager#suspend()
     */
    public Transaction suspend() throws SystemException
    {
        checkSetup();
        return tm.suspend();
    }

    public Reference getReference() throws NamingException
    {
        return SerializableObjectFactory.createReference(this);
    }

}
