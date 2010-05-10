//$Id: J2eeUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: J2eeUserTransaction.java,v $
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
//Revision 1.2  2006/03/15 10:31:43  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/13 14:49:21  guy
//Updated javadoc.
//
//Revision 1.1  2004/10/12 08:25:07  guy
//Renamed J2EE UserTransaction class.
//
//Revision 1.1  2004/10/08 14:31:07  guy
//Optimized JTA classes for J2EE integration.
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
    private transient TransactionManager txmgr_;

    public J2eeUserTransaction ()
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

        txmgr_ = TransactionManagerImp.getTransactionManager ();

        if ( txmgr_ == null )
            throw new RuntimeException ( "Transaction Service Not Running?" );
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
