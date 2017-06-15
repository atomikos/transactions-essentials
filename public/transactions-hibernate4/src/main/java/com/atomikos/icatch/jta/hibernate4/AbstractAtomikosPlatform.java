package com.atomikos.icatch.jta.hibernate4;

import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author glick
 */
public class AbstractAtomikosPlatform implements JtaPlatform
{
  private static final long serialVersionUID = -797444425934689078L;

  protected TransactionManager txMgr;
  protected UserTransaction userTx;

  public AbstractAtomikosPlatform()
  {
    super();
  }

  @Override
  public Object getTransactionIdentifier(Transaction transaction) {
    // generally we use the transaction itself.
    return transaction;
  }

  @Override
  public void registerSynchronization(Synchronization synchronization) {
    try {
      this.txMgr.getTransaction()
          .registerSynchronization(synchronization);
    } catch (Exception e) {
      throw new JtaPlatformException(
          "Could not access JTA Transaction to register synchronization",
          e);
    }
  }

  @Override
  public boolean canRegisterSynchronization() {
    try {
      if (this.txMgr.getTransaction() != null) {
        return this.txMgr.getTransaction().getStatus() == Status.STATUS_ACTIVE;
      }
    } catch (SystemException se) {
      throw new TransactionException( "Could not determine transaction status", se );
    }
    return false;
  }

  @Override
  public int getCurrentStatus() throws SystemException {
    return retrieveTransactionManager().getStatus();
  }

  @Override
  public UserTransaction retrieveUserTransaction() {
    return this.userTx;
  }

  @Override
  public TransactionManager retrieveTransactionManager() {
    return this.txMgr;
  }
}
