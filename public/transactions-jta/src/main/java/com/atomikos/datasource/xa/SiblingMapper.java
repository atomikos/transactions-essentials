/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.datasource.xa;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;

/**
 *
 *
 * A SiblingMapper encapsulates the mapping policy for assigning a
 * ResourceTransaction to a composite tx instance.
 */

class SiblingMapper
{

    protected Hashtable siblings_;
    protected XATransactionalResource res_;

    protected String root_ ;

    SiblingMapper ( XATransactionalResource res , String root )
    {
        siblings_ = new Hashtable ();
        res_ = res;
        root_ = root;
    }

    /* 
     * if resource uses weak compare mode then
     * do NOT reuse restx instances, since the
     * TMJOIN flag may fail if multiple resource mgrs
     * for the same vendor are in use.
     * the same holds for acceptsAllXAResources
     * also, in order to allow concurrent enlistings
     * for the same XAResource, we need to return
     * a new restx if the one found is still active
     */
    
    private boolean canBeReused(XAResourceTransaction restx) {
    	boolean ret = false;
    	ret = !(restx == null || 
    			res_.usesWeakCompare() || 
    			res_.acceptsAllXAResources () ||
    			restx.isActive());
    	return ret;
    }
    
    private XAResourceTransaction findSiblingXAResourceTransactionToReuse(CompositeTransaction ct) {
    	XAResourceTransaction ret = null;
    	Enumeration enumm = siblings_.elements();
    	while ( ret == null && enumm.hasMoreElements() ) {
    		XAResourceTransaction candidate = (XAResourceTransaction) enumm.nextElement ();
    		if (canBeReused(candidate) && ct.isSerial()) ret = candidate;
    	}
    	return ret;
    }
    
    protected ResourceTransaction map ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        Stack errors = new Stack ();
        XAResourceTransaction last = null;
        try {
            last = (XAResourceTransaction) siblings_.get(ct);
            if ( !canBeReused(last) ) {
                last = findSiblingXAResourceTransactionToReuse(ct);
                if ( last == null ) {
                    last = new XAResourceTransaction ( res_, ct , root_ );
                    siblings_.put ( ct, last );
                }
            }
        } catch ( Exception e ) {
            errors.push ( e );
            throw new ResourceException ( "ResourceTransaction map failure", errors );
        }
        ct.addParticipant ( last );
        return last;
    }
}
