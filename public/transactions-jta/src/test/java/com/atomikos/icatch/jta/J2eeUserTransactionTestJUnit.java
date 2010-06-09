package com.atomikos.icatch.jta;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 *
 * 
 */
public class J2eeUserTransactionTestJUnit extends
        AbstractJUnitUserTransactionTest
{

    /**
     * @param name
     */
    public J2eeUserTransactionTestJUnit(String name)
    {
        super(name);
    }

    /**
     * @see com.atomikos.icatch.jta.AbstractJUnitUserTransactionTest#getUserTransaction()
     */
    protected UserTransaction getUserTransaction()
    {
        return new J2eeUserTransaction();
    }
    
    public void testReferencibility()
    throws Exception
    {
        J2eeUserTransaction utx = new J2eeUserTransaction();
        Reference ref = utx.getReference();
   		if ( ref == null ) throw new Exception ( "getReference fails" );
   		Class clazz = Class.forName ( ref.getFactoryClassName() );
   		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
   		utx = ( J2eeUserTransaction ) fact.getObjectInstance ( ref , null , null , null );
   		assertNotNull ( utx );
    }
    
    public void testNoAutomaticStartup()
    throws Exception
    {
        UserTransactionService uts = getUserTransactionService();
   		uts.shutdown ( true );
        UserTransaction utx = getUserTransaction();
        try {
   			utx.begin();
   		}
   		catch ( Exception normal ) {}
   		
   		//VITAL: assert that TM is not running due to this
   		if ( Configuration.getCompositeTransactionManager() != null ) 
   			throw new Exception ( "Auto startup for J2eeUserTransaction" );
   		
   		TSInitInfo info = uts.createTSInitInfo();
	   	info.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
    	info.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath()
    	        );
    	uts.init ( info );
   		
   		utx.begin();
   		if ( utx.getStatus() == Status.STATUS_NO_TRANSACTION ) 
   			throw new Exception ( "No tx started" );
   		utx.rollback();
   		
   		uts.shutdown ( true );
    }

}
