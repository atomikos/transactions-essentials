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
