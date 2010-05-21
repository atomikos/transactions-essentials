package com.atomikos.icatch.jta;

import javax.transaction.UserTransaction;

/**
 * 
 * 
 * 
 *
 * 
 */
public class UserTransactionImpTestJUnit extends
        AbstractJUnitUserTransactionTest
{

    public UserTransactionImpTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitUserTransactionTest#getUserTransaction()
     */
    protected UserTransaction getUserTransaction()
    {
        return new UserTransactionImp();
    }

}
