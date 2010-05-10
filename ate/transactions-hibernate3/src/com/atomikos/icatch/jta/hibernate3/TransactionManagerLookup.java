//$Id: TransactionManagerLookup.java,v 1.1 2006/09/22 08:38:17 guy Exp $
//$Log: TransactionManagerLookup.java,v $
//Revision 1.1  2006/09/22 08:38:17  guy
//ADDED 1006
//
//Revision 1.1  2006/09/21 15:21:12  guy
//ADDED 1004
//
//Revision 1.1.1.1  2006/04/29 08:55:44  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:30  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/05/14 13:43:47  guy
//Added hibernate integration package.
//
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
