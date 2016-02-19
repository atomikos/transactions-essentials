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

package com.atomikos.datasource.xa;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class RecoveryScan {
	
	public static interface XidSelector {
		boolean selects(Xid xid);
	}
	
	public static List<XID> recoverXids(XAResource xaResource, XidSelector selector) throws XAException {
		List<XID> ret = new ArrayList<XID>();

        boolean done = false;
        int flags = XAResource.TMSTARTRSCAN;
        Xid[] xidsFromLastScan = null;
        List<XID> allRecoveredXidsSoFar = new ArrayList<XID>();
        do {
        	xidsFromLastScan = xaResource.recover(flags);
            flags = XAResource.TMNOFLAGS;
            done = (xidsFromLastScan == null || xidsFromLastScan.length == 0);
            if (!done) {

                // TEMPTATIVELY SET done TO TRUE
                // TO TOLERATE ORACLE 8.1.7 INFINITE
                // LOOP (ALWAYS RETURNS SAME RECOVER
                // SET). IF A NEW SET OF XIDS IS RETURNED
                // THEN done WILL BE RESET TO FALSE

                done = true;
                for ( int i = 0; i < xidsFromLastScan.length; i++ ) {
                	XID xid = new XID ( xidsFromLastScan[i] );
                    // our own XID implements equals and hashCode properly
                    if (!allRecoveredXidsSoFar.contains(xid)) {
                        // a new xid is returned -> we can not be in a recovery loop -> go on
                        allRecoveredXidsSoFar.add(xid);
                        done = false;
                        if (selector.selects(xid)) {
                        	ret.add(xid);
                        }
                    }
                }
            }
        } while (!done);
		
		return ret;
	}
}

