//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: RemoteClientUserTransaction.java,v $
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
//Revision 1.12  2004/11/10 10:33:19  guy
//Corrected bug: local mode will disable import of remote tx in server VM.
//
//Revision 1.11  2004/11/01 08:05:40  guy
//Updated error messages for not found.
//
//Revision 1.10  2004/11/01 07:37:43  guy
//Updated toString() to new dual behaviour.
//
//Revision 1.9  2004/11/01 06:58:41  guy
//Added dual mode: intra-server VM vs remote (no local TM).
//In the first case, there should be no suspend of the thread!
//
//Revision 1.8  2004/10/13 14:15:24  guy
//Updated javadocs and improved getReference.
//
//Revision 1.7  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2003/08/27 06:23:50  guy
//Adapted to RMI-IIOP.
//
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.5  2003/03/27 08:12:34  guy
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Corrected BUB: timeout not serialized in and out.
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.4  2003/03/26 19:35:48  guy
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Added preservation of timeout setting across JNDI store and lookup.
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.3  2003/03/22 16:03:54  guy
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Updated remote usertx to actually use the timeout settings of the user.
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2003/03/11 06:39:01  guy
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: RemoteClientUserTransaction.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//Revision 1.1.2.6  2003/01/29 17:19:45  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.1.2.5  2002/11/20 18:34:30  guy
//Adapted usertx to use non-default port if needed.
//Added stub files explicitly, to allow make complete to work.
//
//Revision 1.1.2.4  2002/11/17 18:36:13  guy
//Corrected JNDI factory mechanism.
//
//Revision 1.1.2.3  2002/11/16 16:26:28  guy
//Corrected initial bugs.
//
//Revision 1.1.2.2  2002/11/16 13:57:52  guy
//Finished remote usertx implementation.
//
//Revision 1.1.2.1  2002/11/14 16:33:42  guy
//Added support for remote usertxs.
//

package com.atomikos.icatch.jta;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.rmi.PortableRemoteObject;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An implementation of a (remote) client's user transaction. When
 * client-demarcated transactions are enabled, this is the kind of
 * UserTransaction you get by calling getUserTransaction() on the
 * UserTransactionService. Client applications can use the result to control
 * transaction demarcation, and even pass their instance to other VMs so that
 * those can share the same transaction context. The server-side applications
 * can use the toString() method to obtain the transaction identifier of the
 * transaction represented by an instance. This way, an incoming call from a
 * <b>client</b> that demarcates its own transactions <b>only has to ship the
 * UserTransaction</b> to the server (or to other clients) to identify what
 * transaction it is in. <br>
 * 
 * NOTE: remote clients that use instances of this class can do transaction
 * demarcation, but they can <b>not</b> do nested transactions!<br>
 * 
 * NOTE: instances that are meant to be bound in JNDI should be bound <b>without</b>
 * any transaction context (i.e., without calling begin() first).
 */

public final class RemoteClientUserTransaction implements UserTransaction,
        Externalizable, Referenceable
{

    static final int DEFAULT_TIMEOUT = 30;

    private transient UserTransactionServer txmgrServer_;
    // not null if used outside server VM

    private transient TransactionManager txmgr_;
    // not null if used in server VM

    private transient Hashtable threadToTidMap_;

    private int timeout_;

    private String name_;
    // the RMI name to lookup the remote server.

    private String initialContextFactory_;

    private String providerUrl_;

    private boolean imported_;

    // if true: no commit/rollback allowed
    // this is the case for instances that are
    // passed on between remote clients
    // Only the client that begins the tx
    // may commit/abort it.

    /**
     * No-argument constructor, as required by Externalizable interface.
     */

    public RemoteClientUserTransaction ()
    {
        threadToTidMap_ = new Hashtable ();
        timeout_ = DEFAULT_TIMEOUT;
        imported_ = false;
    }

    /**
     * Preferred constructor.
     * 
     * @param name
     *            The unique name of the UserTransactionServer.
     * @param initialContextFactory
     *            The initial context factory of the <b>server</b> JNDI
     *            context.
     * @param providerUrl
     *            The provider URL of the <b>server</b> JNDI context.
     */

    public RemoteClientUserTransaction ( String name ,
            String initialContextFactory , String providerUrl )
    {
        initialContextFactory_ = initialContextFactory;
        providerUrl_ = providerUrl;

        name_ = name;
        threadToTidMap_ = new Hashtable ();
        timeout_ = DEFAULT_TIMEOUT;
        imported_ = false;
    }

    // /**
    // *For use by JNDI factory
    // */
    //       
    // RemoteClientUserTransaction ( String url )
    // {
    // this();
    // name_ = url;
    // }

    private String getNotFoundMessage ()
    {
        String errorMsg = "Name not found: "
                + name_
                + "\n"
                + "Please check that: \n"
                + "	-server property com.atomikos.icatch.client_demarcation is set to true \n"
                + "   -server property com.atomikos.icatch.rmi_export_class is correct \n"
                + "	-server property java.naming.factory.initial is "
                + initialContextFactory_ + "\n"
                + "	-server property java.naming.provider.url is "
                + providerUrl_ + "\n"
                + "	-the naming service is running on port " + providerUrl_
                + "\n" + "	-the transaction server is running";

        return errorMsg;
    }

    /**
     * Referenceable mechanism requires later setup of txmgr_, otherwise binding
     * into JNDI already requires that TM is running.
     * 
     * @return boolean True if running in server VM, false if remote.
     */

    private boolean checkSetup ()
    {
        // first try to get intra-VM txmgr
        txmgr_ = TransactionManagerImp.getTransactionManager ();

        // if no intra-VM tm: use remote tm
        if ( txmgr_ == null ) {

            try {
                Hashtable env = new Hashtable ();
                env.put ( Context.INITIAL_CONTEXT_FACTORY,
                        initialContextFactory_ );
                env.put ( Context.PROVIDER_URL, providerUrl_ );
                Context ctx = new InitialContext ( env );
                txmgrServer_ = (UserTransactionServer) PortableRemoteObject
                        .narrow ( ctx.lookup ( name_ ),
                                UserTransactionServer.class );

            } catch ( Exception e ) {
                e.printStackTrace ();
                throw new RuntimeException ( getNotFoundMessage () );
            }
            if ( txmgrServer_ == null )
                throw new RuntimeException ( getNotFoundMessage () );
        }

        return txmgr_ != null;
    }

    private synchronized void setThreadMapping ( String tid )
    {
        Thread thread = Thread.currentThread ();
        threadToTidMap_.put ( thread, tid );
    }

    private synchronized String removeThreadMapping ()
    {
        Thread thread = Thread.currentThread ();
        return (String) threadToTidMap_.remove ( thread );
    }

    private synchronized String getThreadMapping ()
    {
        Thread thread = Thread.currentThread ();
        return (String) threadToTidMap_.get ( thread );
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void begin () throws NotSupportedException, SystemException
    {
        boolean local = checkSetup ();
        if ( local )
            txmgr_.begin ();
        else {

            String tid = getThreadMapping ();
            if ( tid != null ) {
                // error: no nested txs supported
                throw new NotSupportedException (
                        "Nested transaction not allowed here" );
            }

            try {
                tid = txmgrServer_.begin ( timeout_ );
            } catch ( RemoteException re ) {
                throw new SystemException ( re.getMessage () );
            }
            setThreadMapping ( tid );
        }
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
        boolean local = checkSetup ();
        if ( local )
            txmgr_.commit ();
        else {

            if ( imported_ )
                throw new SecurityException ( "Commit not allowed: not creator" );

            String tid = removeThreadMapping ();
            if ( tid == null )
                throw new IllegalStateException ( "No transaction for thread" );

            try {
                txmgrServer_.commit ( tid );
            } catch ( RemoteException re ) {
                throw new SystemException ( re.getMessage () );
            }
        }
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void rollback () throws IllegalStateException, SystemException,
            SecurityException
    {

        boolean local = checkSetup ();
        if ( local )
            txmgr_.rollback ();
        else {

            if ( imported_ )
                throw new SecurityException (
                        "Rollback not allowed: not creator" );
            String tid = removeThreadMapping ();
            if ( tid == null )
                throw new IllegalStateException ( "No transaction for thread" );

            try {
                txmgrServer_.rollback ( tid );
            } catch ( RemoteException re ) {
                throw new SystemException ( re.getMessage () );
            }
        }
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        boolean local = checkSetup ();
        if ( local )
            txmgr_.setRollbackOnly ();
        else {

            String tid = getThreadMapping ();
            if ( tid == null )
                throw new IllegalStateException ( "No transaction for thread" );
            try {
                txmgrServer_.setRollbackOnly ( tid );
            } catch ( RemoteException re ) {
                throw new SystemException ( re.getMessage () );
            }
        }
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public int getStatus () throws SystemException
    {
        int ret = Status.STATUS_NO_TRANSACTION;
        boolean local = checkSetup ();
        if ( local )
            ret = txmgr_.getStatus ();
        else {

            String tid = getThreadMapping ();
            if ( tid != null ) {
                try {
                    ret = txmgrServer_.getStatus ( tid );
                } catch ( RemoteException re ) {
                    throw new SystemException ( re.getMessage () );
                }
            }
        }
        return ret;
    }

    /**
     * @see javax.transaction.UserTransaction
     */

    public void setTransactionTimeout ( int seconds ) throws SystemException
    {

        timeout_ = seconds;
    }

    /**
     * Overrides the default behaviour, to allow retrieving the corresponding
     * transaction at the server side.
     * 
     * @return String The transaction ID (tid) of the transaction for which the
     *         thread is executing. Null if no transaction.
     */

    public String toString ()
    {
        String ret = null;
        boolean local = checkSetup ();
        if ( local ) {
            Transaction tx = null;
            try {
                tx = txmgr_.getTransaction ();
            } catch ( SystemException e ) {
                String msg = "Error getting transaction";
                Configuration.logWarning ( msg, e );
            }
            if ( tx != null )
                ret = tx.toString ();
        }
        // happens if not local, OR:
        // if local, but no tx found for thread
        // this occurs for an imported instance
        // from a remote client!!!
        if ( ret == null )
            ret = getThreadMapping ();

        return ret;
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    /**
     * @see Referenceable
     */

    public Reference getReference () throws NamingException
    {
        RefAddr nameRef = new StringRefAddr ( "ServerName", name_ );
        RefAddr urlRef = new StringRefAddr ( "ProviderUrl", providerUrl_ );
        RefAddr factRef = new StringRefAddr ( "ContextFactory",
                initialContextFactory_ );
        RefAddr timeoutRef = new StringRefAddr ( "Timeout", new Integer (
                timeout_ ).toString () );
        Reference ref = new Reference ( getClass ().getName (),
                new StringRefAddr ( "name", "RemoteClientUserTransaction" ),
                RemoteClientUserTransactionFactory.class.getName (), null );
        ref.add ( nameRef );
        ref.add ( urlRef );
        ref.add ( factRef );
        ref.add ( timeoutRef );
        return ref;
    }

    //
    //
    // IMPLEMENTATION OF EXTERNALIZABLE
    //
    //

    /**
     * @see Externalizable
     */

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        // Implement two-fold behaviour:
        // if thread not currently associated with a tx
        // then write null;
        // else write the current TID in order
        // to ship context among remote clients.

        String tid = getThreadMapping ();
        out.writeObject ( tid );
        out.writeObject ( name_ );
        out.writeObject ( initialContextFactory_ );
        out.writeObject ( providerUrl_ );
        out.writeInt ( timeout_ );
    }

    /**
     * @see Externalizable
     */

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        // Try if a tid is there;
        // if yes then this means that the streaming out
        // was done for a transaction context;
        // hence restore that context
        String tid = (String) in.readObject ();
        if ( tid != null ) {
            setThreadMapping ( tid );
            imported_ = true;
            // this will mark the instance to not
            // allow commit/rollback
        }
        name_ = (String) in.readObject ();
        initialContextFactory_ = (String) in.readObject ();
        providerUrl_ = (String) in.readObject ();
        timeout_ = in.readInt ();
    }

}
