/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate;

import java.util.Properties;

import javax.transaction.TransactionManager;

import net.sf.hibernate.HibernateException;

import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * 
 * 
 * 
 * This class is provided for Hibernate integration.
 * To use Atomikos as the Hibernate JTA transaction manager,
 * specify this class as the value of the 
 * <b>hibernate.transaction.manager_lookup_class</b> of the
 * hibernate configuration properties.
 * 
 */
public class TransactionManagerLookup
    implements net.sf.hibernate.transaction.TransactionManagerLookup
{

	UserTransactionManager utm;
	
	public TransactionManagerLookup()
	{
		utm = new UserTransactionManager();
	}
	

    /* (non-Javadoc)
     * @see net.sf.hibernate.transaction.TransactionManagerLookup#getTransactionManager(java.util.Properties)
     */
    public TransactionManager getTransactionManager(Properties arg0)
        throws HibernateException
    {
        return utm;
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.transaction.TransactionManagerLookup#getUserTransactionName()
     */
    public String getUserTransactionName()
    {
        
        return null;
    }

}
