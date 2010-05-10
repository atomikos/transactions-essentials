//$Log: UserTransactionManagerTestJUnit.java,v $
//Revision 1.3  2006/10/30 10:37:10  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.2.2.1  2006/10/06 05:07:23  guy
//ADDED 1009
//
//Revision 1.2  2006/09/22 11:53:27  guy
//ADDED 1003
//
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

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 * 
 * 
 * 
 *
 * 
 */
public class UserTransactionManagerTestJUnit extends
        AbstractJUnitTransactionManagerTest
{

    
    public UserTransactionManagerTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitTransactionManagerTest#getTransactionManager()
     */
    protected TransactionManager getTransactionManager()
    {
        return new UserTransactionManager();
    }
    
    public void testEquivalenceOfInstances() throws Exception
    {
   		UserTransactionManager utm1 = new UserTransactionManager();
		//begin a tx on utm1;
		//this is the actual trigger
		utm1.begin();
   		
   		
   		
   		
   		//second utm should work
   		UserTransactionManager utm2 = new UserTransactionManager();
   		
   		
   		//and rollback on second
   		utm2.rollback();
    }

    public void testForceShutdown()
    {
    		UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
    		tm.setForceShutdown ( true );
    		assertTrue ( tm.getForceShutdown() );
    		tm.setForceShutdown( false );
    		assertFalse ( tm.getForceShutdown() );
    }
    
    public void testInitClose() throws SystemException
    {
    		tearDown();
    		assertNull ( TransactionManagerImp.getTransactionManager() );
    		UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
    		tm.init();
    		assertNotNull ( TransactionManagerImp.getTransactionManager() );
    		tm.close();
    		assertNull ( TransactionManagerImp.getTransactionManager() );
    		setUp();
    		
    }
    
    public void testInitCloseIfAlreadyRunning() throws SystemException
    {
    		UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
		tm.init();
		tm.close();
		//TS should still be there since not started by bean
		assertNotNull ( TransactionManagerImp.getTransactionManager() );
		
    }
    
    public void testAutomaticStartup() throws SystemException
    {
    		tearDown();
    		UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
		//default should be true for backward compability
    		assertTrue ( tm.getStartupTransactionService() );
		tm.setStartupTransactionService ( false );
		assertFalse ( tm.getStartupTransactionService() );
		assertNull ( TransactionManagerImp.getTransactionManager() );
		try {
			tm.init();
			failTest ( "init works if startup disabled and no TM?" );
		}
		catch ( SystemException ok ){}
		//no TM should be there
		assertNull ( TransactionManagerImp.getTransactionManager() );
		tm.close();
		assertNull ( TransactionManagerImp.getTransactionManager() );
    }
    
    public void testUsageAfterClose() throws SystemException, NotSupportedException 
    {
    	UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
		tm.init();
		tm.close();
		try {
			tm.begin();
			fail ( "Usage allowed after close?" );
		}
		catch ( SystemException ok ) {}
    
    }
    
    public void testCloseWithoutInit() 
    {
    	UserTransactionManager tm = ( UserTransactionManager ) getTransactionManager();
		tm.close();
    }
}
