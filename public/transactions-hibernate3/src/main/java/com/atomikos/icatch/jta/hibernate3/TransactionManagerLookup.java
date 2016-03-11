/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate3;

import java.util.Properties;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;

import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * 
 * 
 * 
 * This class is provided for Hibernate3 integration.
 * To use Atomikos as the Hibernate JTA transaction manager,
 * specify this class as the value of the 
 * <b>hibernate.transaction.manager_lookup_class</b> of the
 * hibernate configuration properties.
 * 
 */
public class TransactionManagerLookup implements org.hibernate.transaction.TransactionManagerLookup
{

	private UserTransactionManager utm;
	
	public TransactionManagerLookup()
	{
		utm = new UserTransactionManager();
	}
	


    public TransactionManager getTransactionManager(Properties props) throws HibernateException
    {
        return utm;
    }

    public String getUserTransactionName()
    {
        return null;
    }


    // new in Hibernate 3.3
	public Object getTransactionIdentifier(Transaction transaction)
	{
		return transaction;
	}

}
