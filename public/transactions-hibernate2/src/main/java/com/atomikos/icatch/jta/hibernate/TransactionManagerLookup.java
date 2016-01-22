/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
