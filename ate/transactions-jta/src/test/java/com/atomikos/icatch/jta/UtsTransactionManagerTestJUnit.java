package com.atomikos.icatch.jta;

import javax.transaction.TransactionManager;

/**
 * 
 * 
 * 
 *
 * 
 */
public class UtsTransactionManagerTestJUnit extends
        AbstractJUnitTransactionManagerTest
{

    /**
     * @param name
     */
    public UtsTransactionManagerTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitTransactionManagerTest#getTransactionManager()
     */
    protected TransactionManager getTransactionManager()
    {
        return getUserTransactionService().getTransactionManager();
    }

}
