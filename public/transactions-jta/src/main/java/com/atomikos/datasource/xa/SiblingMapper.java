/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;

/**
 * A SiblingMapper encapsulates the policies for creating or reusing XA branches.
 * 
 * Assumption: there is one instance per root transaction, per resource.
 */

class SiblingMapper
{

    protected Map<CompositeTransaction,List<XAResourceTransaction>> siblingsOfSameRoot_;
    protected XATransactionalResource res_;

    protected String root_ ;

    SiblingMapper ( XATransactionalResource res , String root )
    {
        siblingsOfSameRoot_ = new HashMap<CompositeTransaction,List<XAResourceTransaction>>();
        res_ = res;
        root_ = root;
    }
    
    private XAResourceTransaction findSiblingBranchToJoin(CompositeTransaction ct) {
    	XAResourceTransaction ret = null;
    	if (ct.isSerial()) {
    		Iterator <List<XAResourceTransaction>> allSiblingLists = siblingsOfSameRoot_.values().iterator();
    		while (ret == null && allSiblingLists.hasNext()) {
    			List<XAResourceTransaction> siblings = allSiblingLists.next();
    			ret = findJoinableBranchInList(siblings);
    		}
    	}
    	return ret;
    }
    
    private XAResourceTransaction findJoinableBranchInList(List<XAResourceTransaction> siblings) {
		XAResourceTransaction ret = null;
		Iterator<XAResourceTransaction> it = siblings.iterator();
		while (ret == null && it.hasNext()) {
			XAResourceTransaction candidate = it.next();
			if (candidate.supportsTmJoin()) ret = candidate;
		}
		return ret;
	}

	protected synchronized ResourceTransaction findOrCreateBranchForTransaction(CompositeTransaction ct)
            throws ResourceException, IllegalStateException
    {
        XAResourceTransaction ret = findOrCreateBranchWithResourceException(ct);
        ct.addParticipant(ret);
        return ret;
    }

	private XAResourceTransaction findOrCreateBranchWithResourceException(CompositeTransaction ct) {		
        XAResourceTransaction ret = null;
        try {
            ret = findOrCreateBranch(ct);
        } catch (Exception e) {
            throw new ResourceException ( "Failed to get branch", e );
        }
		return ret;
	}

	private XAResourceTransaction findOrCreateBranch(CompositeTransaction ct) {
		XAResourceTransaction ret;
		ret = findPreviousBranchToJoin(ct);
		if (ret == null) {
		    ret = findSiblingBranchToJoin(ct);
		    if (ret == null) {
		        ret = createNewBranch(ct);
		    }
		}
		return ret;
	}

	private XAResourceTransaction createNewBranch(CompositeTransaction ct) {
		XAResourceTransaction ret;
		ret = new XAResourceTransaction(res_, ct, root_);
		rememberBranch(ct, ret);
		return ret;
	}

	private XAResourceTransaction findPreviousBranchToJoin(CompositeTransaction ct) {
		List<XAResourceTransaction> candidates = findSiblingsForTransaction(ct);
		return findJoinableBranchInList(candidates);
	}

	private List<XAResourceTransaction> findSiblingsForTransaction(CompositeTransaction ct) {
		List<XAResourceTransaction> ret = siblingsOfSameRoot_.get(ct);
		if (ret == null) {
			ret = new ArrayList<XAResourceTransaction>();
		}
		return Collections.unmodifiableList(ret);
	}
	
	private void rememberBranch(CompositeTransaction ct, XAResourceTransaction branch) {
		List<XAResourceTransaction> list = siblingsOfSameRoot_.get(ct);
		if (list == null) {
			list = new ArrayList<XAResourceTransaction>();
			siblingsOfSameRoot_.put(ct,list);
		}
		list.add(branch);
	}
}
