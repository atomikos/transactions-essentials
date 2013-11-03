/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.jta;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.config.TSInitInfo;
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
                TSInitInfo info = uts.createTSInitInfo ();
                uts.init ( info );
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
