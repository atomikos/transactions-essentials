package com.atomikos.icatch.jta;

import com.atomikos.icatch.config.Configuration;

import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * @author glick
 */
public abstract class AbstractTransactionManager implements TransactionManager {

  private static final long serialVersionUID = -655789038710288096L;

  protected transient TransactionManagerImp tm;

  protected boolean startupTransactionService;

  protected boolean closed;

  protected boolean coreStartedHere;

  /**
   * @see javax.transaction.TransactionManager#getStatus()
   */
  @Override
  public int getStatus () throws SystemException
  {
    checkSetup ();
    return tm.getStatus ();
  }

  protected void startupTransactionService() {
    coreStartedHere = Configuration.init();
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

  protected void checkSetup () throws SystemException
  {
    if (!closed) initializeTransactionManagerSingleton();
  }

  private void initializeTransactionManagerSingleton() throws SystemException {
    tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager ();
    if ( tm == null ) {
      if ( getStartupTransactionService() ) {
        startupTransactionService();
        tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager ();
      }
      else {
        throw new SystemException ( "Transaction service not running" );
      }
    }
  }

  /**
   * Returns true if the transaction service will
   * be started if not already running.
   * @return boolean result of startup
   */
  public boolean getStartupTransactionService()
  {
    return this.startupTransactionService;
  }
}

