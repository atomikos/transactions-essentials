package com.atomikos.icatch.jta;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.transaction.TransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 *
 * 
 */
public class J2eeTransactionManagerTestJUnit extends
        AbstractJUnitTransactionManagerTest
{

    
    public J2eeTransactionManagerTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitTransactionManagerTest#getTransactionManager()
     */
    protected TransactionManager getTransactionManager()
    {
        return new J2eeTransactionManager();
    }
    
    public void testNoAutomaticStartup() throws Exception
    {
        UserTransactionService uts = getUserTransactionService();
   		uts.shutdown ( true );
   		
   		J2eeTransactionManager tm = new J2eeTransactionManager();
   		
   		//assert referencibility
   		Reference ref = tm.getReference();
		Class clazz = Class.forName ( ref.getFactoryClassName() );
		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
		tm = ( J2eeTransactionManager ) fact.getObjectInstance ( ref , null , null , null );
		
		try {
			tm.begin();
		}
		catch ( Exception normal ) {}
		
		//VITAL: assert TM did not startup due to this
		if ( Configuration.getCompositeTransactionManager() != null )
			throw new Exception ( "Auto startup for J2eeTransactionManager" );

   	
    }

}
