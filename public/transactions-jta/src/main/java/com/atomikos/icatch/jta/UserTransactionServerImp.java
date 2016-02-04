/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 *
 *
 * An implementation of the server-side UserTransaction infrastructure. An
 * instance of this class is created and exported automatically (when
 * client-demarcated transactions are enabled).
 */

public class UserTransactionServerImp implements UserTransactionServer
{
	private static final Logger LOGGER = LoggerFactory.createLogger(UserTransactionServerImp.class);

    // @todo assert no exceptions if RMI-IIOP not enabled!

    private static UserTransactionServerImp singleton_ = null;

    public static synchronized UserTransactionServerImp getSingleton ()
    {
        if ( singleton_ == null ) {
            singleton_ = new UserTransactionServerImp ();
        }
        return singleton_;
    }

  

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
                .getProperty ( "com.atomikos.icatch.rmi_export_class" );
        canExport = "UnicastRemoteObject".equals ( exportClass )
                || "PortableRemoteObject".equals ( exportClass );
        if ( !canExport ) {
            LOGGER
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

           

            try {

                if ( "PortableRemoteObject".equals ( exportClass ) ) {
                    PortableRemoteObject.exportObject ( this );
                    exported_ = true;
                } else if ( "UnicastRemoteObject".equals ( exportClass ) ) {
                    UnicastRemoteObject.exportObject ( this );
                    exported_ = true;
                }

            } catch ( Exception e ) {

                throw new SysException (
                        "Error exporting - naming service not running?", e );
            }

            try {

                Context ctx = getInitialContext ();
                ctx.rebind ( name_, this );
            } catch ( Exception e ) {

                throw new SysException (
                        "Please make sure the rmiregistry is running!?", e );
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
                    .getProperty ( "com.atomikos.icatch.rmi_export_class" );
            try {
                if ( "PortableRemoteObject".equals ( exportClass ) )
                    PortableRemoteObject.unexportObject ( this );
                else if ( "UnicastRemoteObject".equals ( exportClass ) )
                    UnicastRemoteObject.unexportObject ( this, true );

                Context ctx = getInitialContext ();
                ctx.unbind ( name_ );
            } catch ( Exception e ) {
                throw new SysException ( e.getMessage (), e );
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

    	TransactionManagerImp tm = getTransactionManager();
        tm.begin ( timeout );
        TransactionImp tx = (TransactionImp) tm.getTransaction ();
        // set serial mode or shared access will NOT work!
        tx.getCT ().setSerial ();
        tm.suspend ();
        return tx.getCT ().getTid ();
    }

    private TransactionManagerImp getTransactionManager() {
    	TransactionManagerImp ret = (TransactionManagerImp) TransactionManagerImp.getTransactionManager();
    	if (ret == null) throw new SysException("Transaction manager not initialized?");
    	return ret;
	}

	/**
     * @see UserTransactionServer
     */

    public void commit ( String tid ) throws RemoteException,
            javax.transaction.RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException
    {
        Transaction tx = getTransactionManager().getJtaTransactionWithId(tid);
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
        Transaction tx = getTransactionManager().getJtaTransactionWithId(tid);
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
        Transaction tx = getTransactionManager().getJtaTransactionWithId(tid);
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
        Transaction tx = getTransactionManager().getJtaTransactionWithId(tid);
        if ( tx != null ) {
            ret = tx.getStatus ();
        }
        return ret;
    }

}
