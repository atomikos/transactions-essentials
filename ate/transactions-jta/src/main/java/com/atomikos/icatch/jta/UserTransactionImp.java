package com.atomikos.icatch.jta;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.admin.imp.SimpleLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.util.SerializableObjectFactory;

/**
 * 
 * 
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

        // REMOVED FOLLOWING IF CHECK: DON'T CACHE THE TXMGR TO MAKE INSTANCES
        // RESILIENT TO RESTART IN TOMCAT. OTHERWISE, CLIENT APPS SEE THEIR
        // USERTX REFERENCES INVALIDATED AND THIS IS INTOLERABLE
        // if ( txmgr_ == null ) {
        // txmgr_ = TransactionManagerImp.getTransactionManager();

        synchronized ( TransactionManagerImp.class ) {

            txmgr_ = TransactionManagerImp.getTransactionManager ();

            // FOLLOWING COMMENTED OUT: NEW RECOVERY IN 2.0 ALLOWS US TO START
            // THE TM
            // IF NOT ALREADY RUNNING!!!
            // if ( txmgr_ == null )
            // throw new RuntimeException ( "No transaction monitor installed?"
            // );

            // NEW FROM 2.0: if TM is not running, just start it. Any resources
            // can be registered later.
            if ( txmgr_ == null ) {
                UserTransactionService uts = new UserTransactionServiceImp ();
                TSInitInfo info = uts.createTSInitInfo ();
                uts.registerLogAdministrator ( SimpleLogAdministrator
                        .getInstance () );
                uts.init ( info );
                txmgr_ = TransactionManagerImp.getTransactionManager ();
            }

        }

        // }
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
