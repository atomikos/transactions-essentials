package com.atomikos.icatch.imp;

import java.util.Properties;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.provider.ConfigProperties;

/**
 * 
 * 
 * 
 * 
 *
 * A test class to assert that the max number of active txs is
 * respected.
 */
public class MaxActivesTester
{
	
	public static void test ( UserTransactionService uts )
	throws Exception
	{
		Properties p = new Properties();
		p.setProperty ( ConfigProperties.MAX_ACTIVES_PROPERTY_NAME , "1");
		uts.init ( p );
		try
        {
            CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
            CompositeTransaction ct1 = ctm.createCompositeTransaction( 1000 );
            
            //new tx should fail
            try {
            	ctm.suspend();
            	ctm.createCompositeTransaction ( 200);
            	throw new Exception ( "Max actives is not respected?");
            }
            catch ( IllegalStateException ok ) {}
            
            ct1.getTransactionControl().getTerminator().rollback();
            
            //now create should work
            try {
            	ct1 = ctm.createCompositeTransaction ( 100 );
            }
            catch ( Exception e ) {
            	throw new Exception ( "Max actives not reached and create fails???");
            }
			ct1.getTransactionControl().getTerminator().rollback();
            
        }
        finally {
			uts.shutdown ( true );
        }
		
		
	}
	
	public static void main ( String[] args ) throws Exception
	{
		UserTransactionService uts =
					new UserTransactionServiceImp();
		test ( uts );
	}

}
