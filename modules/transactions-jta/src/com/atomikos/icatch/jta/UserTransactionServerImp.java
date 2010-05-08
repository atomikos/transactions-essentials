//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//$Log: UserTransactionServerImp.java,v $
//Revision 1.2  2006/09/19 08:03:55  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:11  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:28  guy
//Extracted init properties as constants and replaced all literal references.
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
//Revision 1.1.1.1  2006/03/09 14:59:11  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.13  2004/10/13 14:15:24  guy
//Updated javadocs and improved getReference.
//
//Revision 1.12  2004/10/11 13:39:37  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.11  2004/10/08 08:32:02  guy
//corrected bug: null pointer reference in init
//
//Revision 1.10  2004/09/28 11:26:42  guy
//Applied Singleton pattern, and added getUserTransaction method.
//Corrected bug in shutdown.
//
//Revision 1.9  2004/09/27 12:27:19  guy
//Changed this class to the singleton pattern.
//
//Revision 1.8  2004/09/27 11:35:57  guy
//Improved diagnostics, and added tolerance for incomplete properties.
//
//Revision 1.7  2004/03/23 06:52:06  guy
//Corrected property names to new ones.
//
//Revision 1.6  2003/09/10 08:57:13  guy
//Modified getInitialContext: first convert properties to hashtable or the
//JNDI will not find the default values.
//
//Revision 1.5  2003/09/01 15:27:55  guy
//Modified exception wrapping in init: more verbose messages.
//Added JRMP native stubs for WebLogic and JBoss compatibility.
//
//Revision 1.4  2003/08/27 06:23:50  guy
//Adapted to RMI-IIOP.
//
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//Revision 1.3  2003/03/22 16:03:54  guy
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//Updated remote usertx to actually use the timeout settings of the user.
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//Revision 1.2  2003/03/11 06:39:01  guy
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionServerImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $
//
//Revision 1.1.2.7  2003/01/31 15:45:19  guy
//Adapted to set/get Properties in TSInitInfo.
//
//Revision 1.1.2.6  2003/01/29 17:19:46  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.1.2.5  2002/11/20 18:34:30  guy
//Adapted usertx to use non-default port if needed.
//Added stub files explicitly, to allow make complete to work.
//
//Revision 1.1.2.4  2002/11/18 18:51:54  guy
//Nothing really changed here.
//
//Revision 1.1.2.3  2002/11/16 16:35:05  guy
//Made more descriptive error if rmiregistry is not running.
//
//Revision 1.1.2.2  2002/11/16 16:26:29  guy
//Corrected initial bugs.
//
//Revision 1.1.2.1  2002/11/16 13:57:52  guy
//Finished remote usertx implementation.
//

package com.atomikos.icatch.jta;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An implementation of the server-side UserTransaction infrastructure. An
 * instance of this class is created and exported automatically (when
 * client-demarcated transactions are enabled).
 */

public class UserTransactionServerImp implements UserTransactionServer
{
    // @todo assert no exceptions if RMI-IIOP not enabled!

    private static UserTransactionServerImp singleton_ = null;

    public static synchronized UserTransactionServerImp getSingleton ()
    {
        if ( singleton_ == null ) {
            singleton_ = new UserTransactionServerImp ();
        }
        return singleton_;
    }

    private TransactionManagerImp tm_;
    // the transaction manager to delegate to

    private String name_;
    // to bind in JNDI

    private boolean exported_;
    // true iff export done

    private String initialContextFactory_;
    private String providerUrl_;

    private Properties properties_;

    /**
     * Creates a new instance.
     * 
     */

    private UserTransactionServerImp ()
    {

        name_ = "UserTransactionServer";
        exported_ = false;
    }

    /**
     * Utility method to return an initial context based on the contents of the
     * properties. This is needed because the JNDI does not recognize the
     * default properties unless they are explicitly converted to a Hashtable.
     * 
     * @return An initial context whose environment depends on the properties.
     * @throws NamingException
     */
    private Context getInitialContext () throws NamingException
    {
        Hashtable env = new Hashtable ();
        Enumeration enumm = properties_.propertyNames ();
        while ( enumm.hasMoreElements () ) {
            String name = (String) enumm.nextElement ();
            String value = properties_.getProperty ( name );
            env.put ( name, value );
        }
        return new InitialContext ( env );
    }

    /**
     * Get a usertx for this server.
     * 
     * @return UserTransaction Null if the server is not exported; a usertx that
     *         can be used at remote clients otherwise.
     */

    public UserTransaction getUserTransaction ()
    {
        UserTransaction ret = null;
        if ( exported_ ) {
            ret = new RemoteClientUserTransaction ( getName (),
                    initialContextFactory_, providerUrl_ );
        }
        return ret;
    }

    /**
     * Get the name on which this instance is listening in RMI.
     * 
     * @return String The name.
     */

    public String getName ()
    {
        return name_;
    }

    /**
     * Initializes the server object. Should be called as the first method, and
     * only <b>after</b> the JTA transaction manager has been set up.
     * 
     * @param properties
     *            The JNDI environment to use.
     * @param tmUniqueName
     *            The unique name that the TM is listening on.
     */

    public void init ( String tmUniqueName , Properties properties )
            throws SysException
    {

        boolean canExport = false;
        String exportClass = properties
                .getProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME );
        canExport = "UnicastRemoteObject".equals ( exportClass )
                || "PortableRemoteObject".equals ( exportClass );
        if ( !canExport ) {
            Configuration
                    .logWarning ( "Client transaction demarcation not supported for "
                            + "com.atomikos.icatch.rmi_export_class="
                            + exportClass );
            exported_ = false;
        } else {
            name_ = tmUniqueName + "UserTransactionServer";
            providerUrl_ = properties.getProperty ( Context.PROVIDER_URL );
            if ( providerUrl_ == null ) {
                throw new SysException ( "Startup property "
                        + Context.PROVIDER_URL
                        + " must be set for client demarcation." );
            }
            initialContextFactory_ = properties
                    .getProperty ( Context.INITIAL_CONTEXT_FACTORY );
            if ( initialContextFactory_ == null ) {
                throw new SysException ( "Startup property "
                        + Context.INITIAL_CONTEXT_FACTORY
                        + " must be set for client demarcation." );
            }
            properties_ = properties;

            tm_ = (TransactionManagerImp) TransactionManagerImp
                    .getTransactionManager ();
            if ( tm_ == null )
                throw new SysException ( "No TM found" );

            try {

                if ( "PortableRemoteObject".equals ( exportClass ) ) {
                    PortableRemoteObject.exportObject ( this );
                    exported_ = true;
                } else if ( "UnicastRemoteObject".equals ( exportClass ) ) {
                    UnicastRemoteObject.exportObject ( this );
                    exported_ = true;
                }

            } catch ( Exception e ) {
                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException (
                        "Error exporting - naming service not running?", errors );
            }

            try {

                Context ctx = getInitialContext ();
                ctx.rebind ( name_, this );
            } catch ( Exception e ) {
                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException (
                        "Please make sure the rmiregistry is running!?", errors );
            }

        }

    }

    /**
     * Performs shutdown of the server. Should be called last.
     */

    public void shutdown () throws SysException
    {

        if ( exported_ ) {
            String exportClass = properties_
                    .getProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME );
            try {
                if ( "PortableRemoteObject".equals ( exportClass ) )
                    PortableRemoteObject.unexportObject ( this );
                else if ( "UnicastRemoteObject".equals ( exportClass ) )
                    UnicastRemoteObject.unexportObject ( this, true );

                Context ctx = getInitialContext ();
                ctx.unbind ( name_ );
            } catch ( Exception e ) {
                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException ( e.getMessage (), errors );
            }
            // set exported to false, to make sure this method is
            // idempotent
            exported_ = false;
        }

    }

    /**
     * @see UserTransactionServer
     */

    public String begin ( int timeout ) throws RemoteException,
            SystemException, NotSupportedException
    {

        tm_.begin ( timeout );
        TransactionImp tx = (TransactionImp) tm_.getTransaction ();
        // set serial mode or shared access will NOT work!
        tx.getCT ().getTransactionControl ().setSerial ();
        tm_.suspend ();
        return tx.getCT ().getTid ();
    }

    /**
     * @see UserTransactionServer
     */

    public void commit ( String tid ) throws RemoteException,
            javax.transaction.RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException
    {
        Transaction tx = tm_.getPreviousInstance ( tid );
        if ( tx == null ) {
            throw new javax.transaction.RollbackException (
                    "Transaction not found: " + tid );
        }
        tx.commit ();
    }

    /**
     * @see UserTransactionServer
     */

    public void rollback ( String tid ) throws RemoteException,
            IllegalStateException, SecurityException, SystemException
    {
        Transaction tx = tm_.getPreviousInstance ( tid );
        if ( tx != null ) {
            tx.rollback ();
        }
    }

    /**
     * @see UserTransactionServer
     */

    public void setRollbackOnly ( String tid ) throws RemoteException,
            java.lang.IllegalStateException, SystemException
    {
        Transaction tx = tm_.getPreviousInstance ( tid );
        if ( tx != null ) {
            tx.setRollbackOnly ();
        }
    }

    /**
     * @see UserTransactionServer
     */

    public int getStatus ( String tid ) throws RemoteException, SystemException
    {
        int ret = Status.STATUS_NO_TRANSACTION;
        Transaction tx = tm_.getPreviousInstance ( tid );
        if ( tx != null ) {
            ret = tx.getStatus ();
        }
        return ret;
    }

}
