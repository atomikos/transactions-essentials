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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.util.SerializableObjectFactory;

/**
 * A straightforward, zero-setup implementation of a transaction manager. 
 * Applications can use an instance of this class to get a handle to the
 * transaction manager, and automatically startup or recover the transaction
 * service on first use. 
 */
public class UserTransactionManager implements TransactionManager,
        Serializable, Referenceable, UserTransaction
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(UserTransactionManager.class);

	private static final long serialVersionUID = -655789038710288096L;

	private transient TransactionManagerImp tm;
    
    private UserTransactionServiceImp uts;

	private boolean forceShutdown;
	
	private boolean startupTransactionService;
	
	private boolean closed;

    private void checkSetup () throws SystemException
    {
    	if ( closed ) throw new SystemException ( "This UserTransactionManager instance was closed already. Call init() to reuse if desired." );
    	
    	if ( tm == null ) { // case 75549: avoid synch overhead if already initialized
    		synchronized ( TransactionManagerImp.class ) {
    			tm = (TransactionManagerImp) TransactionManagerImp
    			.getTransactionManager ();
    			if ( tm == null ) {
    				if ( getStartupTransactionService() ) {
    					startupTransactionService();
    				}
    				else {
    					throw new SystemException ( "Transaction service not running" );
    				}
    			}
    		}
    	}
    }

	private void startupTransactionService() {
		uts = new UserTransactionServiceImp ();
		uts.init();
		tm = (TransactionManagerImp) TransactionManagerImp
		        .getTransactionManager ();
	}
	

	private void shutdownTransactionService() {
		if ( uts != null ) { // null if we did not start core here
			uts.shutdown ( forceShutdown );
			uts = null;
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
        checkSetup ();
        tm.rollback ();

    }

    /**
     * @see javax.transaction.TransactionManager#setRollbackOnly()
     */
    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        checkSetup ();
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
