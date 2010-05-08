//$Id: UserTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: UserTransactionImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.10  2005/05/11 17:07:04  guy
//Corrected synch startup to lock on TransactionManagerImp,
//or autostart would mess up across UserTransactionManager and UserTransactionImp
//if called in different threads.
//
//Revision 1.9  2005/05/11 10:41:12  guy
//Corrected bug: checkSetup methods need to be threadsafe across instances.
//
//Revision 1.8  2004/10/13 14:15:24  guy
//Updated javadocs and improved getReference.
//
//Revision 1.7  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2004/10/08 14:31:07  guy
//Optimized JTA classes for J2EE integration.
//
//Revision 1.5  2004/10/01 08:56:06  guy
//*** empty log message ***
//
//Revision 1.4  2004/09/20 14:50:19  guy
//Added init logic in UserTransactionImp.
//Changed TransactionManagerImp: installCompositeTransactionManager
//checks for null.
//
//Revision 1.3  2004/09/17 16:13:40  guy
//Added dynamic registration of TemporaryXATransactionalResource for
//unknown XAResource enlists.
//Changed UserTransactionImp to init TM if not done yet.
//Added an easy UserTransactionManager class for zero-setup usage.
//
//Revision 1.2  2004/03/22 15:37:39  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.1.1.10.1  2004/02/19 12:22:48  guy
//Removed caching of tm to tolerate intermediate restart of TM.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

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
