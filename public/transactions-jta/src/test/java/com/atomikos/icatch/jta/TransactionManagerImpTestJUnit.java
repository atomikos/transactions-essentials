package com.atomikos.icatch.jta;

import javax.transaction.TransactionManager;

/**
 * 
 * 
 * 
 *
 * 
 */
public class TransactionManagerImpTestJUnit extends
        AbstractJUnitTransactionManagerTest
{

  
    public TransactionManagerImpTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitTransactionManagerTest#getTransactionManager()
     */
    protected TransactionManager getTransactionManager()
    {
        return TransactionManagerImp.getTransactionManager();
    }

}
