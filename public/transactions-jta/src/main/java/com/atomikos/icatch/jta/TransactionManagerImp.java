/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * The main JTA transaction manager singleton.
 */

public class TransactionManagerImp implements TransactionManager,
        SubTxAwareParticipant, Referenceable, UserTransaction

{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionManagerImp.class);


	/**
     * Transaction property name to indicate that the transaction is a 
     * JTA transaction. If this property is set to an arbitrary non-null
     * value then a JTA transaction is assumed by this class.
     * This property is used for detecting incompatible existing transactions:
     * if a transaction exists without this property then the <b>begin</b> method
     * will suspend it (and resume afterwards).
     */
    public static final String JTA_PROPERTY_NAME = "com.atomikos.icatch.jta.transaction";

    private static TransactionManagerImp singleton = null;

    private static int defaultTimeoutInSecondsForNewTransactions;

    private static boolean jtaTransactionsAreSerialByDefault = false;

    private ThreadLocal<Integer> timeoutInSecondsForNewTransactions = new ThreadLocal<Integer>() {
    	protected Integer initialValue() {
    		return defaultTimeoutInSecondsForNewTransactions;
    	};
	};

    private Map<String, TransactionImp> jtaTransactionToCoreTransactionMap;

    private CompositeTransactionManager compositeTransactionManager;

    private boolean enableAutomatRegistrationOfUnknownXAResources;

    
    private static final void raiseNoTransaction() 
    {
    		StringBuffer msg = new StringBuffer();
    		msg.append ( "This method needs a transaction for the calling thread and none exists.\n" );
    		msg.append ( "Possible causes: either you didn't start a transaction,\n" );
    		msg.append ( "it rolledback due to timeout, or it was committed already.\n" );
    		msg.append ( "ACTIONS: You can try one of the following: \n" );
    		msg.append ( "1. Make sure you started a transaction for the thread.\n" );
    		msg.append ( "2. Make sure you didn't terminate it yet.\n" );
    		msg.append ( "3. Increase the transaction timeout to avoid automatic rollback of long transactions;\n" );
    		msg.append ( "   check http://www.atomikos.com/Documentation/JtaProperties for how to do this." );
    		LOGGER.logWarning ( msg.toString() );
    		throw new IllegalStateException ( msg.toString() );
    }

    /**
     * Sets the default serial mode for new txs.
     * 
     * @param value
     *            If true, then new txs will be set to serial mode.
     */

    public static void setDefaultSerial ( boolean value )
    {
        jtaTransactionsAreSerialByDefault = value;
    }

    /**
     * Gets the default mode for new txs.
     * 
     * @return boolean If true, then new txs started through here will be in
     *         serial mode.
     */

    public static boolean getDefaultSerial ()
    {
        return jtaTransactionsAreSerialByDefault;
    }
    
    /**
     * Set the default transaction timeout value.
     * 
     * @param defaultTimeoutValueInSeconds
     * 				the default transaction timeout value in seconds.
     */
	public static void setDefaultTimeout ( int defaultTimeoutValueInSeconds )
	{
		defaultTimeoutInSecondsForNewTransactions = defaultTimeoutValueInSeconds;
	}
	
	/**
	 * Get the default timeout value.
	 * 
	 * @return the default transaction timeout value in seconds.
	 */
	public static int getDefaultTimeout ()
	{
		return defaultTimeoutInSecondsForNewTransactions;
	}


    /**
     * Install a transaction manager.
     * 
     * @param ctm
     *            The composite transaction manager to use.
     * @param automaticResourceRegistration
     *            If true, then unknown XAResource instances should lead to the
     *            addition of a new temporary resource. If false, then unknown
     *            resources will lead to an exception.
     * 
     */

    public static synchronized void installTransactionManager (
            CompositeTransactionManager ctm ,
            boolean automaticResourceRegistration )
    {
    	if ( ctm != null ) {
    		 singleton = new TransactionManagerImp ( ctm,
                     automaticResourceRegistration );
    	} else  {
    		singleton = null;
    	}         

    }

    /**
     * Gets the installed transaction manager, if any.
     * 
     * @return TransactionManager The installed instance, null if none.
     */

    public static TransactionManager getTransactionManager ()
    {
        return singleton;
    }

    private TransactionManagerImp ( CompositeTransactionManager ctm ,
            boolean automaticResourceRegistration )
    {
        compositeTransactionManager = ctm;
        jtaTransactionToCoreTransactionMap = new HashMap<String, TransactionImp>();
        enableAutomatRegistrationOfUnknownXAResources = automaticResourceRegistration;
    }

    private void addToMap ( String tid , TransactionImp tx )
    {
        synchronized ( jtaTransactionToCoreTransactionMap ) {
            jtaTransactionToCoreTransactionMap.put ( tid , tx );
        }
    }

    private void removeFromMap ( String tid )
    {
        synchronized ( jtaTransactionToCoreTransactionMap ) {
            jtaTransactionToCoreTransactionMap.remove ( tid );
        }
    }
    
    /**
     * @return TransactionImp The relevant instance, or null.
     */
    TransactionImp getJtaTransactionWithId ( String tid )
    {
        synchronized ( jtaTransactionToCoreTransactionMap ) {
             return jtaTransactionToCoreTransactionMap.get ( tid );
        }
    }

    private CompositeTransaction getCompositeTransaction() throws ExtendedSystemException 
    {
    	CompositeTransaction ct = null;
    	 try {
             ct = compositeTransactionManager.getCompositeTransaction ();
         } catch ( SysException se ) {
         	String msg = "Error while retrieving the transaction for the calling thread";
         	LOGGER.logWarning( msg , se);
            throw new ExtendedSystemException ( msg , se );
         }
    	 establishJtaTransactionContextIfNecessary(ct);
         return ct;
    }

	private void establishJtaTransactionContextIfNecessary(
			CompositeTransaction ct) {
		if ( isJtaTransaction(ct) ) {
             TransactionImp jtaTransaction = getJtaTransactionWithId ( ct.getTid () );
             if ( jtaTransaction == null ) {
                 recreateCompositeTransactionAsJtaTransaction(ct);
             }
         }
	}
    
    

    /**
     * Gets any previous transaction with the given identifier.
     * 
     * @return Transaction The instance, or null if not found.
     */

    public Transaction getTransaction ( String tid )
    {
        return getJtaTransactionWithId ( tid );
    }

    /**
     * Creates a new transaction and associate it with the current thread. If the
     * current thread already has a transaction, then a local subtransaction
     * will be created. 
     */

    public void begin () throws NotSupportedException, SystemException
    {
    	
        begin ( getTransactionTimeout() );
    }

    /**
     * Custom begin to guarantee a timeout value through an argument.
     */

    public void begin ( int timeout ) throws NotSupportedException,
            SystemException
    {
        CompositeTransaction ct = null;
        ResumePreviousTransactionSubTxAwareParticipant resumeParticipant = null;
        
        ct = compositeTransactionManager.getCompositeTransaction();
        if ( ct != null && ct.getProperty (  JTA_PROPERTY_NAME ) == null ) {
            LOGGER.logWarning ( "JTA: temporarily suspending incompatible transaction: " + ct.getTid() +
                    " (will be resumed after JTA transaction ends)" );
            ct = compositeTransactionManager.suspend();
            resumeParticipant = new ResumePreviousTransactionSubTxAwareParticipant ( ct );
        }
        
        try {
            ct = compositeTransactionManager.createCompositeTransaction ( ( ( long ) timeout ) * 1000 );
            if ( resumeParticipant != null ) ct.addSubTxAwareParticipant ( resumeParticipant );
            if ( ct.isRoot () && getDefaultSerial () )
                ct.setSerial ();
            ct.setProperty ( JTA_PROPERTY_NAME , "true" );
        } catch ( SysException se ) {
        	String msg = "Error in begin()";
        	LOGGER.logWarning( msg , se );
            throw new ExtendedSystemException ( msg , se );
        }
        recreateCompositeTransactionAsJtaTransaction(ct);
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public Transaction getTransaction () throws SystemException
    {
        TransactionImp ret = null;        
        CompositeTransaction ct = getCompositeTransaction();
        if ( ct != null) ret = getJtaTransactionWithId ( ct.getTid () );
        return ret;
    }

	private TransactionImp recreateCompositeTransactionAsJtaTransaction(
			CompositeTransaction ct) {
		TransactionImp ret = null;
		if (ct.getState ().equals ( TxState.ACTIVE )) { // setRollbackOnly may have been called!
			ret = new TransactionImp ( ct, enableAutomatRegistrationOfUnknownXAResources );
			addToMap ( ct.getTid (), ret );
			ct.addSubTxAwareParticipant ( this );
		}
		return ret;
	}

    private boolean isJtaTransaction(CompositeTransaction ct) {
		boolean ret = false;
		if (ct !=null && ct.getProperty( JTA_PROPERTY_NAME ) != null) ret = true;
		return ret;
	}

	/**
     * @see javax.transaction.TransactionManager
     */

    public void setTransactionTimeout ( int seconds ) throws SystemException
    {
        if ( seconds > 0 ) {
            timeoutInSecondsForNewTransactions.set(seconds);
        } else if ( seconds == 0 ) {
            timeoutInSecondsForNewTransactions.set(defaultTimeoutInSecondsForNewTransactions);
        } else {
        	String msg = "setTransactionTimeout: value must be >= 0";
        	LOGGER.logWarning( msg );
        	throw new SystemException ( msg );
        }
            
    }

    public int getTransactionTimeout ()
    {
        return timeoutInSecondsForNewTransactions.get();
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public Transaction suspend() throws SystemException
    {
        TransactionImp ret = (TransactionImp) getTransaction();   
        if ( ret != null ) {
        	suspendUnderlyingCompositeTransaction(); 
        	ret.suspendEnlistedXaResources(); // cf case 61305
        }
        return ret;
    }

	private void suspendUnderlyingCompositeTransaction()
			throws ExtendedSystemException {
        try {
            compositeTransactionManager.suspend();
        } catch ( SysException se ) {
        	String msg = "Unexpected error while suspending the existing transaction for the current thread";
        	LOGGER.logWarning( msg , se );
            throw new ExtendedSystemException ( msg , se );
        }
	}

    /**
     * @see javax.transaction.TransactionManager
     */

    public void resume ( Transaction tobj ) throws InvalidTransactionException,
            IllegalStateException, SystemException
    {
        if ( tobj == null || !(tobj instanceof TransactionImp) ) {
        	String msg = "The specified transaction object is invalid for this configuration: " + tobj;
        	LOGGER.logWarning( msg );
            throw new InvalidTransactionException ( msg );
        }

        TransactionImp tximp = (TransactionImp) tobj;
        try {
            compositeTransactionManager.resume ( tximp.getCT () );
        } catch ( SysException se ) {
        	String msg = "Unexpected error while resuming the transaction in the calling thread";
        	LOGGER.logWarning( msg , se );
            throw new ExtendedSystemException(msg , se );
        }
        tximp.resumeEnlistedXaReources();

    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public int getStatus() throws SystemException
    {
        int ret = Status.STATUS_NO_TRANSACTION;       
        Transaction tx = getTransaction();
        if ( tx == null ) {
            ret = Status.STATUS_NO_TRANSACTION;
        } else {
            ret = tx.getStatus ();
        }
        return ret;
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void commit () throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.IllegalStateException,
            java.lang.SecurityException
    {
        Transaction tx = getTransaction();
        if ( tx == null ) raiseNoTransaction();
        tx.commit();
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void rollback () throws IllegalStateException, SystemException,
            SecurityException
    {
        Transaction tx = getTransaction();
        if ( tx == null ) raiseNoTransaction();
        tx.rollback();
       
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        Transaction tx = getTransaction(); 
        if ( tx == null ) raiseNoTransaction();
        try {
            tx.setRollbackOnly ();
        } catch ( SecurityException se ) {
            String msg = "Unexpected error during setRollbackOnly";
            LOGGER.logWarning( msg , se );
            throw new ExtendedSystemException ( msg, se );
        }
    }

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant
     */

    public void committed ( CompositeTransaction tx )
    {
        removeFromMap ( tx.getTid () );
    }

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant
     */

    public void rolledback ( CompositeTransaction tx )
    {
        removeFromMap ( tx.getTid () );
    }
    
    /**
     * @see javax.naming.Referenceable#getReference()
     */

    public Reference getReference () throws NamingException
    {
        return new Reference ( getClass ().getName (), new StringRefAddr (
                "name", "TransactionManager" ), TransactionManagerFactory.class
                .getName (), null );
    }

}
