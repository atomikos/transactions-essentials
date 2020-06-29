/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;

import junit.framework.TestCase;

/**
 * 
 * 
 * 
 *
 * 
 */
public abstract class AbstractJUnitMaxActivesTest extends TestCase
{

   
   
    public AbstractJUnitMaxActivesTest ( String name )
    {
        super ( name );
    }
    
    protected abstract void startTS ( int maxActivesValue );
    
    protected abstract void stopTS();
    
    protected final void setUp()
    {
        
        startTS ( 1 );
    }
    
    protected final void tearDown()
    {
        stopTS();
    }
    
    public void testMaxActives()
    throws Exception
    {
        
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
        CompositeTransaction ct1 = ctm.createCompositeTransaction( 1000 );
        
        //new tx should fail
        try {
        	ctm.suspend();
        	ctm.createCompositeTransaction ( 200);
        	throw new Exception ( "Max actives is not respected?");
        }
        catch ( IllegalStateException ok ) {}
        
        ct1.rollback();
        
        //now create should work
        try {
        	ct1 = ctm.createCompositeTransaction ( 100 );
        }
        catch ( Exception e ) {
        	throw new Exception ( "Max actives not reached and create fails???");
        }
		ct1.rollback();
    }

}
