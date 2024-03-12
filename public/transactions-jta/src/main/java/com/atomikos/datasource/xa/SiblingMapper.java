/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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

	private Map<CompositeTransaction,List<XAResourceTransaction>> siblingsOfSameRoot;
    private XATransactionalResource res;

    private String root ;

    SiblingMapper ( XATransactionalResource res , String root )
    {
        this.siblingsOfSameRoot = new HashMap<CompositeTransaction,List<XAResourceTransaction>>();
        this.res = res;
        this.root = root;
    }
    
    private XAResourceTransaction findSiblingBranchToJoin(CompositeTransaction ct) {
    	XAResourceTransaction ret = null;
    	if (ct.isSerial()) {
    		Iterator <List<XAResourceTransaction>> allSiblingLists = this.siblingsOfSameRoot.values().iterator();
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
		ct.addSubTxAwareParticipant(new ResourceTransactionSuspender(ret)); //cf case 175941
		return ret;
	}

	private XAResourceTransaction createNewBranch(CompositeTransaction ct) {
		XAResourceTransaction ret;
		ret = new XAResourceTransaction(this.res, ct, this.root);
		rememberBranch(ct, ret);
		return ret;
	}

	private XAResourceTransaction findPreviousBranchToJoin(CompositeTransaction ct) {
		List<XAResourceTransaction> candidates = findSiblingsForTransaction(ct);
		return findJoinableBranchInList(candidates);
	}

	private List<XAResourceTransaction> findSiblingsForTransaction(CompositeTransaction ct) {
		List<XAResourceTransaction> ret = this.siblingsOfSameRoot.get(ct);
		if (ret == null) {
			ret = new ArrayList<XAResourceTransaction>();
		}
		return Collections.unmodifiableList(ret);
	}
	
	private void rememberBranch(CompositeTransaction ct, XAResourceTransaction branch) {
		List<XAResourceTransaction> list = this.siblingsOfSameRoot.get(ct);
		if (list == null) {
			list = new ArrayList<XAResourceTransaction>();
			this.siblingsOfSameRoot.put(ct,list);
		}
		list.add(branch);
	}
}
