//$Log: TransactionManagerImpTestJUnit.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:19  guy
//Imported 3.0 development into CVS repository.
//

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
