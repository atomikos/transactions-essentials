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

package com.atomikos.icatch.imp;

import junit.framework.TestCase;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;

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
