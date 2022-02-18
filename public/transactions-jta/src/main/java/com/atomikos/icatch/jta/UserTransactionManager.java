/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
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

import com.atomikos.icatch.OrderedLifecycleComponent;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.util.SerializableObjectFactory;

/**
 * A straightforward, zero-setup implementation of a transaction manager. 
 * Applications can use an instance of this class to get a handle to the
 * transaction manager, and automatically startup or recover the transaction
 * service on first use. 
 */
public class UserTransactionManager implements TransactionManager,
        Serializable, Referenceable, UserTransaction, OrderedLifecycleComponent
{
	private static final long serialVersionUID = -655789038710288096L;

	private transient TransactionManagerImp tm;

	private boolean forceShutdown;
	
	private boolean startupTransactionService;
	
	private boolean closed;

	private boolean coreStartedHere;

    private void checkSetup () throws SystemException
    {
    	if (!closed) initializeTransactionManagerSingleton();	
    }

	private void initializeTransactionManagerSingleton() throws SystemException {
		tm = (TransactionManagerImp) TransactionManagerImp
    			.getTransactionManager ();
    	if ( tm == null ) {
    		if ( getStartupTransactionService() ) {
    			startupTransactionService();
    			tm = (TransactionManagerImp) TransactionManagerImp
    			        .getTransactionManager ();
    		}
    		else {
    			throw new SystemException ( "Transaction service not running" );
    		}
    	}
	}

	private void startupTransactionService() {
		coreStartedHere = Configuration.init();
	}
	

	private void shutdownTransactionService() {
		if (coreStartedHere) { 
			Configuration.shutdown(forceShutdown);
			coreStartedHere = false;
		}
	}
    
    public UserTransactionManager()
    {
    		//startup by default, to have backward compatibility
    		this.startupTransactionService = true;
    		this.closed = false;
    }
    
    /**
     * Sets whether the transaction service should be 
     * started if not already running. Optional, defaults to true.
     * 
     * @param startup
     */
    public void setStartupTransactionService ( boolean startup )
    {
    	this.startupTransactionService = startup;
    }
    
    /**
     * Returns true if the transaction service will 
     * be started if not already running.
     * @return
     */
    public boolean getStartupTransactionService()
    {
    	return this.startupTransactionService;
    }
    
    /**
     * Performs initialization if necessary.
     * This will startup the TM (if not running)
     * and perform recovery, unless <b>getStartupTransactionService</b>
     * returns false.
     * 
     * @throws SystemException
     */
    
    public void init() throws SystemException
    {
    	    closed = false;
    		checkSetup();
    }

    /**
     * @see javax.transaction.TransactionManager#begin()
     */
    public void begin () throws NotSupportedException, SystemException
    {
    	if ( closed ) throw new SystemException ( "This UserTransactionManager instance was closed already. Call init() to reuse if desired." );
        checkSetup ();
        tm.begin ();

    }
    
    public boolean getForceShutdown()
    {
    		return forceShutdown;
    }
    
    /**
     * Sets the force shutdown mode to use during close.
     * @param value 
     */
    public void setForceShutdown ( boolean value )
    {
    		this.forceShutdown = value;
    }

    /**
     * @see javax.transaction.TransactionManager#commit()
     */
    public void commit () throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException
    {
    	if ( closed ) throw new SystemException ( "This UserTransactionManager instance was closed already - commit no longer allowed or possible." );
        checkSetup ();
        tm.commit ();

    }

    /**
     * @see javax.transaction.TransactionManager#getStatus()
     */
    public int getStatus () throws SystemException
    {
        checkSetup ();
        return tm.getStatus ();
    }

    /**
     * @see javax.transaction.TransactionManager#getTransaction()
     */
    public Transaction getTransaction () throws SystemException
    {
        checkSetup ();
        return tm.getTransaction ();
    }

    /**
     * @see javax.transaction.TransactionManager#resume(javax.transaction.Transaction)
     */
    public void resume ( Transaction tx ) throws InvalidTransactionException,
            IllegalStateException, SystemException
    {
        checkSetup ();
        tm.resume ( tx );

    }

    /**
     * @see javax.transaction.TransactionManager#rollback()
     */
    public void rollback () throws IllegalStateException, SecurityException,
            SystemException
    {
        tm.rollback ();

    }

    /**
     * @see javax.transaction.TransactionManager#setRollbackOnly()
     */
    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        tm.setRollbackOnly ();

    }

    /**
     * @see javax.transaction.TransactionManager#setTransactionTimeout(int)
     */
    public void setTransactionTimeout ( int secs ) throws SystemException
    {
        checkSetup ();
        tm.setTransactionTimeout ( secs );

    }

    /**
     * @see javax.transaction.TransactionManager#suspend()
     */
    public Transaction suspend () throws SystemException
    {
        checkSetup ();
        return tm.suspend ();
    }

    /**
     * @see javax.naming.Referenceable#getReference()
     */
    public Reference getReference () throws NamingException
    {
        return SerializableObjectFactory.createReference ( this );
    }
    
    /**
     * Closes the transaction service, but only if it was 
     * implicitly started via this instance.
     * In other words, if the transaction service was started
     * in another way then this method will not do anything.
     *
     */
    public void close()
    {
    	shutdownTransactionService();
    	closed = true;
    }


}
